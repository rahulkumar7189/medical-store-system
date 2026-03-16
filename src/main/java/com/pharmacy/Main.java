package com.pharmacy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.pharmacy.utils.DatabaseConnection;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize DB Connection on Startup
        DatabaseConnection.getConnection();
        com.pharmacy.utils.DatabaseMigration.runMigration();
        
        URL fxmlLocation = getClass().getResource("/com/pharmacy/views/Login.fxml");
        if (fxmlLocation == null) {
            System.err.println("Cannot find Login.fxml!");
            return;
        }
        
        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene = new Scene(root, 800, 600);
        
        URL cssLocation = getClass().getResource("/com/pharmacy/css/style.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        } else {
            System.err.println("Cannot find style.css!");
        }
        
        primaryStage.setTitle("Medical Store System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Ensure Database connection is safely closed when window is closed
        DatabaseConnection.closeConnection();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
