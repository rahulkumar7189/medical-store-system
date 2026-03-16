package com.pharmacy.controllers;

import com.pharmacy.dao.SupplierDao;
import com.pharmacy.models.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SuppliersController implements Initializable {

    // Table GUI
    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, Integer> colId;
    @FXML private TableColumn<Supplier, String> colName;
    @FXML private TableColumn<Supplier, String> colContact;
    @FXML private TableColumn<Supplier, String> colAddress;

    // Form GUI
    @FXML private TextField txtName;
    @FXML private TextField txtContact;
    @FXML private TextArea txtAddress;
    @FXML private Label lblStatus;

    // Data handling
    private final SupplierDao supplierDao = new SupplierDao();
    private ObservableList<Supplier> supplierList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Link columns to Supplier POJO fields
        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        supplierList = FXCollections.observableArrayList();
        loadTableData();
    }

    private void loadTableData() {
        supplierList.clear();
        List<Supplier> dbSuppliers = supplierDao.getAllSuppliers();
        supplierList.addAll(dbSuppliers);
        supplierTable.setItems(supplierList);
    }

    @FXML
    public void handleAddSupplier(ActionEvent event) {
        String name = txtName.getText();
        String contact = txtContact.getText();
        String address = txtAddress.getText();

        if (name.isEmpty() || contact.isEmpty()) {
            lblStatus.setText("Error: Company Name and Contact Info are required.");
            lblStatus.setStyle("-fx-text-fill: red;");
            return;
        }

        // Create new Supplier with placeholder ID (DB handles true auto-increment)
        Supplier newSupplier = new Supplier(0, name, contact, address, null);
        
        boolean success = supplierDao.addSupplier(newSupplier);

        if (success) {
            lblStatus.setText("Supplier mapped successfully!");
            lblStatus.setStyle("-fx-text-fill: green;");
            clearForm(null);
            loadTableData(); // Hot refresh table to show new specific ID
        } else {
            lblStatus.setText("Database insertion failed.");
            lblStatus.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void clearForm(ActionEvent event) {
        txtName.clear();
        txtContact.clear();
        txtAddress.clear();
        if(event != null) lblStatus.setText(""); // Only clear status if clicked by user
    }
}
