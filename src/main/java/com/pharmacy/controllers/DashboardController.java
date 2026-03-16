package com.pharmacy.controllers;

import com.pharmacy.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TextField txtGlobalSearch;
    @FXML private StackPane contentArea;

    private User loggedInUser;

    @FXML
    public void initialize() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome to Medical Store System");
        }
    }

    public void initUserData(User user) {
        this.loggedInUser = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome back, " + user.getUsername() + " (" + user.getRole() + ")");
        }
    }

    @FXML
    public void loadInventory(ActionEvent event) {
        loadView("/com/pharmacy/views/Inventory.fxml");
    }

    @FXML
    public void loadPOS(ActionEvent event) {
        loadView("/com/pharmacy/views/POS.fxml");
    }

    @FXML
    public void loadSuppliers(ActionEvent event) {
        loadView("/com/pharmacy/views/Suppliers.fxml");
    }

    @FXML
    public void loadCustomers(ActionEvent event) {
        loadView("/com/pharmacy/views/Customers.fxml");
    }

    @FXML
    public void loadReports(ActionEvent event) {
        loadView("/com/pharmacy/views/Reports.fxml");
    }

    @FXML
    public void loadAuditTrail(ActionEvent event) {
        loadView("/com/pharmacy/views/AuditTrail.fxml");
    }

    @FXML
    public void handleGlobalSearch(ActionEvent event) {
        String query = txtGlobalSearch.getText();
        if (query != null && !query.trim().isEmpty()) {
            loadSearchView(query.trim());
        }
    }

    @FXML
    public void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/pharmacy/views/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/com/pharmacy/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Medical Store System");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Object controller = loader.getController();

            if (controller instanceof POSController) {
                ((POSController) controller).setCurrentUser(loggedInUser);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            handleLoadError(fxmlPath, e);
        }
    }

    private void loadSearchView(String query) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pharmacy/views/Search.fxml"));
            Parent view = loader.load();
            SearchController controller = loader.getController();
            
            controller.performSearch(query);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            handleLoadError("/com/pharmacy/views/Search.fxml", e);
        }
    }

    private void handleLoadError(String fxmlPath, Exception e) {
        System.err.println("Failed to load view: " + fxmlPath);
        System.err.println("Error details: " + e.getMessage());
        e.printStackTrace();

        VBox errorBox = new VBox(10);
        errorBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        Label title = new Label("Module Error or Not Yet Implemented");
        title.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label details = new Label("Path: " + fxmlPath + "\nError: " + e.toString());
        details.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        details.setWrapText(true);
        details.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        errorBox.getChildren().addAll(title, details);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(errorBox);
    }
}
