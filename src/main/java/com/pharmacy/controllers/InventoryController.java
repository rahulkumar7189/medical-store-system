package com.pharmacy.controllers;

import com.pharmacy.dao.MedicineDao;
import com.pharmacy.models.Medicine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    @FXML private TableView<Medicine> medicineTable;
    @FXML private TableColumn<Medicine, Integer> colId;
    @FXML private TableColumn<Medicine, String> colName;
    @FXML private TableColumn<Medicine, String> colBatch;
    @FXML private TableColumn<Medicine, String> colGeneric;
    @FXML private TableColumn<Medicine, String> colCategory;
    @FXML private TableColumn<Medicine, Double> colPrice;
    @FXML private TableColumn<Medicine, Double> colTaxRate;
    @FXML private TableColumn<Medicine, Integer> colQuantity;
    @FXML private TableColumn<Medicine, Integer> colReorder;
    @FXML private TableColumn<Medicine, Date> colExpiry;

    @FXML private TextField txtName;
    @FXML private TextField txtGeneric;
    @FXML private TextField txtBatch;
    @FXML private TextField txtCategory;
    @FXML private TextField txtPrice;
    @FXML private TextField txtTaxRate;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtReorder;
    @FXML private DatePicker dateExpiry;
    @FXML private Label lblStatus;
    @FXML private Label lblAlertBanner;

    private final MedicineDao medicineDao = new MedicineDao();
    private ObservableList<Medicine> medicineList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("medId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        colGeneric.setCellValueFactory(new PropertyValueFactory<>("genericName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTaxRate.setCellValueFactory(new PropertyValueFactory<>("taxRate"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colReorder.setCellValueFactory(new PropertyValueFactory<>("reorderLevel"));
        colExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));

        // Row highlighting: RED = expired, YELLOW = expiring within 30 days, ORANGE = low stock
        medicineTable.setRowFactory(tv -> new TableRow<Medicine>() {
            @Override
            protected void updateItem(Medicine med, boolean empty) {
                super.updateItem(med, empty);
                if (med == null || empty) {
                    setStyle("");
                } else {
                    LocalDate today = LocalDate.now();
                    LocalDate expiry = med.getExpiryDate() != null ? med.getExpiryDate().toLocalDate() : null;

                    if (expiry != null && expiry.isBefore(today)) {
                        setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    } else if (expiry != null && expiry.isBefore(today.plusDays(30))) {
                        setStyle("-fx-background-color: #f39c12;");
                    } else if (med.getQuantity() <= med.getReorderLevel()) {
                        setStyle("-fx-background-color: #fdcb6e;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        medicineList = FXCollections.observableArrayList();
        loadTableData();
        updateAlertBanner();
    }

    private void loadTableData() {
        medicineList.clear();
        List<Medicine> dbMedicines = medicineDao.getAllMedicines();
        medicineList.addAll(dbMedicines);
        medicineTable.setItems(medicineList);
    }

    private void updateAlertBanner() {
        if (lblAlertBanner == null) return;
        int expiredCount = medicineDao.getExpiredMedicines().size();
        int expiringCount = medicineDao.getExpiringMedicines(30).size();
        int lowStockCount = medicineDao.getLowStockMedicines().size();

        StringBuilder alerts = new StringBuilder();
        if (expiredCount > 0) alerts.append("⛔ ").append(expiredCount).append(" EXPIRED  ");
        if (expiringCount > 0) alerts.append("⚠ ").append(expiringCount).append(" expiring within 30 days  ");
        if (lowStockCount > 0) alerts.append("📦 ").append(lowStockCount).append(" items below reorder level");

        if (alerts.length() > 0) {
            lblAlertBanner.setText(alerts.toString().trim());
            lblAlertBanner.setVisible(true);
        } else {
            lblAlertBanner.setText("✅ All inventory levels and expiry dates OK");
            lblAlertBanner.setVisible(true);
        }
    }

    @FXML
    public void handleAddMedicine(ActionEvent event) {
        if (txtName.getText().isEmpty() || txtPrice.getText().isEmpty() ||
            txtQuantity.getText().isEmpty() || dateExpiry.getValue() == null) {
            lblStatus.setText("Error: Please fill all required (*) fields.");
            lblStatus.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            String name = txtName.getText();
            String generic = txtGeneric.getText();
            String batch = txtBatch != null ? txtBatch.getText() : "";
            String category = txtCategory.getText();
            double price = Double.parseDouble(txtPrice.getText());
            double taxRate = (txtTaxRate != null && !txtTaxRate.getText().isEmpty()) ? Double.parseDouble(txtTaxRate.getText()) : 5.0;
            int quantity = Integer.parseInt(txtQuantity.getText());
            int reorder = (txtReorder != null && !txtReorder.getText().isEmpty()) ? Integer.parseInt(txtReorder.getText()) : 10;
            LocalDate localDate = dateExpiry.getValue();
            Date sqlDate = Date.valueOf(localDate);

            Medicine newMed = new Medicine(0, name, generic, batch, category, price, taxRate, quantity, reorder, sqlDate, 0, null);

            boolean success = medicineDao.addMedicine(newMed);

            if (success) {
                lblStatus.setText("✅ Medicine added successfully!");
                lblStatus.setStyle("-fx-text-fill: green;");
                clearForm(null);
                loadTableData();
                updateAlertBanner();
            } else {
                lblStatus.setText("Database insertion failed. Check logs.");
                lblStatus.setStyle("-fx-text-fill: red;");
            }

        } catch (NumberFormatException e) {
            lblStatus.setText("Error: Price/Quantity/Tax must be valid numbers.");
            lblStatus.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void clearForm(ActionEvent event) {
        txtName.clear();
        txtGeneric.clear();
        if (txtBatch != null) txtBatch.clear();
        txtCategory.clear();
        txtPrice.clear();
        if (txtTaxRate != null) txtTaxRate.clear();
        txtQuantity.clear();
        if (txtReorder != null) txtReorder.clear();
        dateExpiry.setValue(null);
        if (event != null) lblStatus.setText("");
    }
}
