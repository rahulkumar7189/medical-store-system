module com.pharmacy {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    opens com.pharmacy to javafx.fxml;
    opens com.pharmacy.controllers to javafx.fxml;
    opens com.pharmacy.models to javafx.base, javafx.fxml;
    opens com.pharmacy.dao to javafx.fxml;
    opens com.pharmacy.utils to javafx.fxml;

    exports com.pharmacy;
    exports com.pharmacy.controllers;
    exports com.pharmacy.models;
    exports com.pharmacy.dao;
    exports com.pharmacy.utils;
}
