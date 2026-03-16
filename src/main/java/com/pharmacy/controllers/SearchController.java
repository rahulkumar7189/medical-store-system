package com.pharmacy.controllers;

import com.pharmacy.dao.AuditLogDao;
import com.pharmacy.dao.MedicineDao;
import com.pharmacy.dao.SupplierDao;
import com.pharmacy.models.AuditLog;
import com.pharmacy.models.Medicine;
import com.pharmacy.models.Supplier;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    @FXML private Label lblSearchTitle;

    @FXML private TableView<Medicine> medicineTable;
    @FXML private TableColumn<Medicine, String> colMedName;
    @FXML private TableColumn<Medicine, String> colMedBatch;
    @FXML private TableColumn<Medicine, Integer> colMedStock;
    @FXML private TableColumn<Medicine, Date> colMedExpiry;

    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, String> colSupName;
    @FXML private TableColumn<Supplier, String> colSupContact;

    @FXML private TableView<AuditLog> logTable;
    @FXML private TableColumn<AuditLog, String> colLogUser;
    @FXML private TableColumn<AuditLog, String> colLogAction;
    @FXML private TableColumn<AuditLog, Timestamp> colLogTime;

    private final MedicineDao medicineDao = new MedicineDao();
    private final SupplierDao supplierDao = new SupplierDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Init Tables
        colMedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colMedBatch.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        colMedStock.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colMedExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));

        colSupName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSupContact.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        colLogUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colLogAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colLogTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
    }

    public void performSearch(String query) {
        lblSearchTitle.setText("Search Results for: \"" + query + "\"");

        List<Medicine> medicines = medicineDao.searchMedicines(query);
        medicineTable.setItems(FXCollections.observableArrayList(medicines));

        List<Supplier> suppliers = supplierDao.searchSuppliers(query);
        supplierTable.setItems(FXCollections.observableArrayList(suppliers));

        List<AuditLog> logs = auditLogDao.searchLogs(query);
        logTable.setItems(FXCollections.observableArrayList(logs));
    }
}
