package com.pharmacy.controllers;

import com.pharmacy.dao.UserDao;
import com.pharmacy.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private final UserDao userDao = new UserDao();

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        // Disable button during processing
        loginButton.setDisable(true);
        errorLabel.setText("Authenticating...");
        errorLabel.setStyle("-fx-text-fill: #3498db;"); // Blue

        // Authenticate hitting the database
        User authenticatedUser = userDao.authenticateUser(username, password);

        if (authenticatedUser != null) {
            errorLabel.setText("Login Successful! Redirecting...");
            errorLabel.setStyle("-fx-text-fill: #2ecc71;"); // Green
            navigateToDashboard(event, authenticatedUser);
        } else {
            errorLabel.setText("Invalid username or password.");
            errorLabel.setStyle("-fx-text-fill: #e74c3c;"); // Red
            loginButton.setDisable(false);
        }
    }

    private void navigateToDashboard(ActionEvent event, User user) {
        try {
            URL fxmlLocation = getClass().getResource("/com/pharmacy/views/Dashboard.fxml");
            if(fxmlLocation == null){
                throw new IOException("Cannot find Dashboard.fxml");
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // Pass the logged-in user to the Dashboard Controller
            DashboardController dashboardController = loader.getController();
            dashboardController.initUserData(user);

            // Get the current window (Stage) and swap the Scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);

            // Inherit the global CSS stylesheet
            URL cssLocation = getClass().getResource("/com/pharmacy/css/style.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("Medical Store System - Dashboard (" + user.getRole() + ")");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load dashboard interface.");
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            loginButton.setDisable(false);
        }
    }
}
