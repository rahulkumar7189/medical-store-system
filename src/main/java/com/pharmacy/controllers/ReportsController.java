package com.pharmacy.controllers;

import com.pharmacy.dao.MedicineDao;
import com.pharmacy.dao.SaleDao;
import com.pharmacy.models.Medicine;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML private Label lblDailyRevenue;
    @FXML private Label lblDailySales;
    @FXML private Label lblLowStock;
    @FXML private Label lblExpiring;
    @FXML private Label lblExpired;
    @FXML private VBox bestSellersBox;
    @FXML private VBox revenueBox;

    private final SaleDao saleDao = new SaleDao();
    private final MedicineDao medicineDao = new MedicineDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Daily Stats
        double dailyRevenue = saleDao.getDailyRevenue();
        int dailySales = saleDao.getDailySalesCount();
        int lowStock = medicineDao.getLowStockMedicines().size();
        int expiring = medicineDao.getExpiringMedicines(30).size();
        int expired = medicineDao.getExpiredMedicines().size();

        lblDailyRevenue.setText(String.format("\u20b9%.2f", dailyRevenue));
        lblDailySales.setText(String.valueOf(dailySales));
        lblLowStock.setText(String.valueOf(lowStock));
        lblExpiring.setText(String.valueOf(expiring));
        lblExpired.setText(String.valueOf(expired));

        // Best Sellers
        Map<String, Integer> bestSellers = saleDao.getBestSellers();
        if (bestSellers.isEmpty()) {
            bestSellersBox.getChildren().add(new Label("No sales data yet."));
        } else {
            int rank = 1;
            for (Map.Entry<String, Integer> entry : bestSellers.entrySet()) {
                Label lbl = new Label(rank + ". " + entry.getKey() + " — " + entry.getValue() + " units sold");
                lbl.setStyle("-fx-font-size: 13; -fx-padding: 2 0;");
                bestSellersBox.getChildren().add(lbl);
                rank++;
            }
        }

        // Revenue Trend (last 7 days)
        Map<String, Double> revenueByDay = saleDao.getRevenueByDay(7);
        if (revenueByDay.isEmpty()) {
            revenueBox.getChildren().add(new Label("No revenue data for the past 7 days."));
        } else {
            for (Map.Entry<String, Double> entry : revenueByDay.entrySet()) {
                Label lbl = new Label(entry.getKey() + "  \u2192  \u20b9" + String.format("%.2f", entry.getValue()));
                lbl.setStyle("-fx-font-size: 13; -fx-padding: 2 0;");
                revenueBox.getChildren().add(lbl);
            }
        }
    }
}
