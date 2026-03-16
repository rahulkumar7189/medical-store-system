package com.pharmacy.controllers;

import com.pharmacy.dao.AuditLogDao;
import com.pharmacy.models.AuditLog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;

public class AuditTrailController implements Initializable {

    @FXML private TableView<AuditLog> auditTable;
    @FXML private TableColumn<AuditLog, Integer> colId;
    @FXML private TableColumn<AuditLog, String> colUser;
    @FXML private TableColumn<AuditLog, String> colAction;
    @FXML private TableColumn<AuditLog, String> colDetails;
    @FXML private TableColumn<AuditLog, Timestamp> colTimestamp;
    @FXML private TextField txtSearch;

    private final AuditLogDao auditLogDao = new AuditLogDao();
    private ObservableList<AuditLog> logList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colDetails.setCellValueFactory(new PropertyValueFactory<>("details"));
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        logList = FXCollections.observableArrayList();
        loadLogs();
    }

    private void loadLogs() {
        logList.clear();
        logList.addAll(auditLogDao.getAllLogs());
        auditTable.setItems(logList);
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        String term = txtSearch.getText().trim();
        logList.clear();
        if (term.isEmpty()) {
            logList.addAll(auditLogDao.getAllLogs());
        } else {
            logList.addAll(auditLogDao.searchLogs(term));
        }
        auditTable.setItems(logList);
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        txtSearch.clear();
        loadLogs();
    }
}
