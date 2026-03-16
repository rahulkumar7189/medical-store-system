package com.pharmacy.controllers;

import com.pharmacy.dao.AuditLogDao;
import com.pharmacy.dao.CustomerDao;
import com.pharmacy.dao.MedicineDao;
import com.pharmacy.dao.SaleDao;
import com.pharmacy.models.Medicine;
import com.pharmacy.models.Sale;
import com.pharmacy.models.SaleItem;
import com.pharmacy.models.User;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class POSController implements Initializable {

    @FXML private TableView<Medicine> medicineTable;
    @FXML private TableColumn<Medicine, Integer> colMedId;
    @FXML private TableColumn<Medicine, String> colMedName;
    @FXML private TableColumn<Medicine, String> colMedBatch;
    @FXML private TableColumn<Medicine, Double> colMedPrice;
    @FXML private TableColumn<Medicine, Double> colMedTax;
    @FXML private TableColumn<Medicine, Integer> colMedStock;
    @FXML private TableColumn<Medicine, Date> colMedExpiry;

    @FXML private TableView<SaleItem> cartTable;
    @FXML private TableColumn<SaleItem, String> colCartMedName;
    @FXML private TableColumn<SaleItem, String> colCartBatch;
    @FXML private TableColumn<SaleItem, Double> colCartPrice;
    @FXML private TableColumn<SaleItem, Integer> colCartQuantity;
    @FXML private TableColumn<SaleItem, Double> colCartTax;
    @FXML private TableColumn<SaleItem, Double> colCartTotal;

    @FXML private TextField txtMedSearch;
    @FXML private TextField txtQuantityToAdd;
    @FXML private TextField txtCustomerName;
    @FXML private TextField txtCustomerContact;
    @FXML private ComboBox<String> cmbPaymentMethod;
    @FXML private Label lblAddError;
    @FXML private Label lblSubtotal;
    @FXML private Label lblTax;
    @FXML private Label lblTotal;
    @FXML private Label lblInvoice;

    private User currentUser;
    private final MedicineDao medicineDao = new MedicineDao();
    private final SaleDao saleDao = new SaleDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();
    private final CustomerDao customerDao = new CustomerDao();

    private ObservableList<Medicine> availableMedicines;
    private FilteredList<Medicine> filteredMedicines;
    private ObservableList<SaleItem> cartItems;
    private List<Medicine> allMedicinesReference;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMedId.setCellValueFactory(new PropertyValueFactory<>("medId"));
        colMedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colMedBatch.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        colMedPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colMedTax.setCellValueFactory(new PropertyValueFactory<>("taxRate"));
        colMedStock.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colMedExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));

        colCartQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCartPrice.setCellValueFactory(new PropertyValueFactory<>("priceAtSale"));
        colCartBatch.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        colCartTax.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getLineTax()).asObject());

        colCartMedName.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getMedicineName();
            if (name == null || name.isEmpty()) {
                name = getMedicineNameById(cellData.getValue().getMedId());
            }
            return new SimpleStringProperty(name);
        });

        colCartTotal.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getLineGrandTotal()).asObject());

        // Payment method combo
        if (cmbPaymentMethod != null) {
            cmbPaymentMethod.setItems(FXCollections.observableArrayList("CASH", "CARD", "UPI"));
            cmbPaymentMethod.setValue("CASH");
        }

        availableMedicines = FXCollections.observableArrayList();
        filteredMedicines = new FilteredList<>(availableMedicines, p -> true);
        
        txtMedSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMedicines.setPredicate(medicine -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (medicine.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (medicine.getGenericName() != null && medicine.getGenericName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (medicine.getCategory() != null && medicine.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        cartItems = FXCollections.observableArrayList();
        cartTable.setItems(cartItems);

        loadInventory();
        updateTotals();

        // Generate preview invoice number
        if (lblInvoice != null) {
            lblInvoice.setText(saleDao.generateInvoiceNumber());
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void loadInventory() {
        allMedicinesReference = medicineDao.getAllMedicines();
        // Sort by medId as requested by user
        allMedicinesReference.sort(Comparator.comparingInt(Medicine::getMedId));
        availableMedicines.clear();
        availableMedicines.addAll(allMedicinesReference);
        medicineTable.setItems(filteredMedicines);
    }

    private String getMedicineNameById(int medId) {
        for (Medicine m : allMedicinesReference) {
            if (m.getMedId() == medId) return m.getName();
        }
        return "Unknown";
    }

    @FXML
    public void addToCart(ActionEvent event) {
        lblAddError.setText("");
        Medicine selectedMed = medicineTable.getSelectionModel().getSelectedItem();

        if (selectedMed == null) {
            lblAddError.setText("Please select a medicine.");
            return;
        }

        int qtyToAdd;
        try {
            qtyToAdd = Integer.parseInt(txtQuantityToAdd.getText());
            if (qtyToAdd <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblAddError.setText("Enter valid quantity.");
            return;
        }

        if (qtyToAdd > selectedMed.getQuantity()) {
            lblAddError.setText("Insufficient stock!");
            return;
        }

        boolean itemExists = false;
        for (SaleItem item : cartItems) {
            if (item.getMedId() == selectedMed.getMedId()) {
                int newQty = item.getQuantity() + qtyToAdd;
                if (newQty > selectedMed.getQuantity()) {
                    lblAddError.setText("Total cart quantity exceeds stock.");
                    return;
                }
                item.setQuantity(newQty);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            SaleItem newItem = new SaleItem();
            newItem.setMedId(selectedMed.getMedId());
            newItem.setMedicineName(selectedMed.getName());
            newItem.setBatchNumber(selectedMed.getBatchNumber());
            newItem.setQuantity(qtyToAdd);
            newItem.setPriceAtSale(selectedMed.getPrice());
            newItem.setTaxRate(selectedMed.getTaxRate());
            cartItems.add(newItem);
        }

        cartTable.refresh();
        txtQuantityToAdd.clear();
        updateTotals();
    }

    @FXML
    public void removeFromCart(ActionEvent event) {
        SaleItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartItems.remove(selected);
            updateTotals();
        }
    }

    private void updateTotals() {
        double subtotal = 0;
        double totalTax = 0;
        for (SaleItem item : cartItems) {
            subtotal += item.getLineTotal();
            totalTax += item.getLineTax();
        }
        double total = subtotal + totalTax;

        lblSubtotal.setText(String.format("\u20b9%.2f", subtotal));
        lblTax.setText(String.format("\u20b9%.2f", totalTax));
        lblTotal.setText(String.format("\u20b9%.2f", total));
    }

    @FXML
    public void handleCheckout(ActionEvent event) {
        if (cartItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Cart", "Cannot checkout an empty cart.");
            return;
        }

        int userId = (currentUser != null) ? currentUser.getUserId() : 1;
        String paymentMethod = (cmbPaymentMethod != null) ? cmbPaymentMethod.getValue() : "CASH";

        double subtotal = 0, totalTax = 0;
        for (SaleItem item : cartItems) {
            subtotal += item.getLineTotal();
            totalTax += item.getLineTax();
        }

        String invoiceNumber = saleDao.generateInvoiceNumber();

        // Customer handling
        String custName = txtCustomerName.getText();
        String custContact = txtCustomerContact.getText();
        int customerId = -1;
        if (custContact != null && !custContact.trim().isEmpty()) {
            customerId = customerDao.getOrCreateCustomer(
                (custName == null || custName.trim().isEmpty()) ? "Walk-in Customer" : custName.trim(),
                custContact.trim()
            );
        }

        Sale sale = new Sale();
        sale.setInvoiceNumber(invoiceNumber);
        sale.setCustomerId(customerId);
        sale.setUserId(userId);
        sale.setTotalAmount(subtotal + totalTax);
        sale.setTaxAmount(totalTax);
        sale.setPaymentMethod(paymentMethod);

        List<SaleItem> itemsToBill = new ArrayList<>(cartItems);

        boolean success = saleDao.processSale(sale, itemsToBill);

        if (success) {
            auditLogDao.logAction(userId, "SALE_COMPLETED",
                "Invoice: " + invoiceNumber + " | Total: \u20b9" + String.format("%.2f", sale.getTotalAmount()) + " | Payment: " + paymentMethod);

            showAlert(Alert.AlertType.INFORMATION, "Sale Complete",
                "Transaction processed!\nInvoice: " + invoiceNumber + "\nTotal: \u20b9" + String.format("%.2f", sale.getTotalAmount()) + "\nPayment: " + paymentMethod);

            cartItems.clear();
            txtCustomerName.clear();
            txtCustomerContact.clear();
            updateTotals();
            loadInventory();
            if (lblInvoice != null) lblInvoice.setText(saleDao.generateInvoiceNumber());
        } else {
            showAlert(Alert.AlertType.ERROR, "Checkout Failed", "An error occurred during the transaction.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
