package com.mycompany.closurecafepos;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SalesReportController implements Initializable {

    @FXML private BarChart<String, Number> weeklySalesChart;
    @FXML private LineChart<String, Number> profitTrendChart;
    @FXML private TableView<TopProduct> topProductsTable;
    @FXML private TableColumn<TopProduct, String> topProdNameCol;
    @FXML private TableColumn<TopProduct, String> topProdCatCol;
    @FXML private TableColumn<TopProduct, Integer> topProdSalesCol;
    @FXML private TableColumn<TopProduct, String> topProdRevCol;

    // Summary labels
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalOrdersLabel;
    @FXML private Label avgOrderLabel;
    @FXML private Label topEmployeeLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadWeeklySales();
        loadTopProducts();
        loadProfitTrend();
        loadSummaryMetrics();
    }

    // Uses VIEW: daily_sales_summary + GROUP BY + ORDER BY
    private void loadWeeklySales() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales");
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement prep = con.prepareStatement(
                 "SELECT DAYNAME(SaleDate) as Day, TotalRevenue " +
                 "FROM daily_sales_summary " +
                 "WHERE WEEK(SaleDate) = WEEK(NOW()) " +
                 "ORDER BY SaleDate ASC")) {
            ResultSet r = prep.executeQuery();
            while (r.next()) {
                series.getData().add(new XYChart.Data<>(
                    r.getString("Day"), r.getDouble("TotalRevenue")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        weeklySalesChart.getData().add(series);
        weeklySalesChart.setLegendVisible(false);
    }

    // Uses VIEW: product_sales_performance + HAVING + ORDER BY
    private void loadTopProducts() {
        topProdNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        topProdCatCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        topProdSalesCol.setCellValueFactory(new PropertyValueFactory<>("totalSold"));
        topProdRevCol.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        ObservableList<TopProduct> list = FXCollections.observableArrayList();
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement prep = con.prepareStatement(
                 "SELECT Name, Category, TotalSold, TotalRevenue " +
                 "FROM product_sales_performance " +
                 "HAVING TotalSold > 0 " +
                 "ORDER BY TotalSold DESC " +
                 "LIMIT 10")) {
            ResultSet r = prep.executeQuery();
            while (r.next()) {
                list.add(new TopProduct(
                    r.getString("Name"),
                    r.getString("Category"),
                    r.getInt("TotalSold"),
                    "₱" + String.format("%,.2f", r.getDouble("TotalRevenue"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        topProductsTable.setItems(list);
    }

    // Uses VIEW: daily_sales_summary + GROUP BY week
    private void loadProfitTrend() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement prep = con.prepareStatement(
                 "SELECT CONCAT('Week ', WEEK(SaleDate)) as WeekLabel, " +
                 "SUM(TotalRevenue) as WeekRevenue " +
                 "FROM daily_sales_summary " +
                 "WHERE YEAR(SaleDate) = YEAR(NOW()) " +
                 "GROUP BY WEEK(SaleDate) " +
                 "ORDER BY WEEK(SaleDate)")) {
            ResultSet r = prep.executeQuery();
            while (r.next()) {
                series.getData().add(new XYChart.Data<>(
                    r.getString("WeekLabel"), r.getDouble("WeekRevenue")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        profitTrendChart.getData().add(series);
        profitTrendChart.setLegendVisible(false);
    }

    // Uses SUBQUERIES + JOIN + GROUP BY + HAVING
    private void loadSummaryMetrics() {
        try (Connection con = DataBaseConnection.getConnection()) {

            // Scalar subquery: total revenue this month
            PreparedStatement s1 = con.prepareStatement(
                "SELECT SUM(TotalAmount) as MonthRevenue, " +
                "COUNT(*) as MonthOrders, " +
                "AVG(TotalAmount) as AvgOrder " +
                "FROM transactions " +
                "WHERE MONTH(Date) = MONTH(NOW()) AND YEAR(Date) = YEAR(NOW())");
            ResultSet r1 = s1.executeQuery();
            if (r1.next()) {
                if (totalRevenueLabel != null)
                    totalRevenueLabel.setText("₱" + String.format("%,.2f", r1.getDouble("MonthRevenue")));
                if (totalOrdersLabel != null)
                    totalOrdersLabel.setText(String.valueOf(r1.getInt("MonthOrders")));
                if (avgOrderLabel != null)
                    avgOrderLabel.setText("₱" + String.format("%,.2f", r1.getDouble("AvgOrder")));
            }

            // Correlated subquery: top performing employee this month
            PreparedStatement s2 = con.prepareStatement(
                "SELECT u.Username, SUM(t.TotalAmount) as TotalSales " +
                "FROM users u " +
                "JOIN transactions t ON u.UserID = t.UserID " +
                "WHERE MONTH(t.Date) = MONTH(NOW()) " +
                "AND t.TotalAmount > (SELECT AVG(TotalAmount) FROM transactions) " +
                "GROUP BY u.UserID, u.Username " +
                "HAVING TotalSales > 0 " +
                "ORDER BY TotalSales DESC " +
                "LIMIT 1");
            ResultSet r2 = s2.executeQuery();
            if (r2.next()) {
                if (topEmployeeLabel != null)
                    topEmployeeLabel.setText(r2.getString("Username"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}