package com.pharmacy.controllers;

import com.pharmacy.dao.CustomerDao;
import com.pharmacy.models.Customer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> colCustName;
    @FXML private TableColumn<Customer, String> colCustContact;

    @FXML private Label lblHistoryTitle;
    @FXML private ListView<String> listHistory;

    private final CustomerDao customerDao = new CustomerDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCustName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        loadCustomers();

        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showHistory(newSelection);
            }
        });
    }

    private void loadCustomers() {
        List<Customer> customers = customerDao.getAllCustomers();
        customerTable.setItems(FXCollections.observableArrayList(customers));
    }

    private void showHistory(Customer customer) {
        lblHistoryTitle.setText("Showing history for: " + customer.getName());
        List<String> history = customerDao.getCustomerPurchaseHistory(customer.getCustomerId());
        if (history.isEmpty()) {
            listHistory.setItems(FXCollections.observableArrayList("No purchase history found."));
        } else {
            listHistory.setItems(FXCollections.observableArrayList(history));
        }
    }
}
