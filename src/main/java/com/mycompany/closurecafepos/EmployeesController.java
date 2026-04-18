/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class EmployeesController implements Initializable {

    @FXML private BarChart<String, Number> empSalesChart;
    @FXML private TableView<EmployeeRow> employeesTable;
    @FXML private TableColumn<EmployeeRow, String> empUsernameCol;
    @FXML private TableColumn<EmployeeRow, String> empRoleCol;
    @FXML private TableColumn<EmployeeRow, String> empStatusCol;
    @FXML private TableColumn<EmployeeRow, String> empTimeInCol;
    @FXML private TableColumn<EmployeeRow, String> empTimeOutCol;
    @FXML private TableColumn<EmployeeRow, String> empActionsCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadEmployeeSalesChart();
        loadEmployeesTable();
    }

    private void loadEmployeeSalesChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales");

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement prep = con.prepareStatement(
                 "SELECT u.Username, SUM(t.TotalAmount) as Total " +
                 "FROM transactions t " +
                 "JOIN users u ON t.UserID = u.UserID " +
                 "WHERE WEEK(t.Date) = WEEK(NOW()) " +
                 "GROUP BY u.UserID, u.Username " +
                 "ORDER BY Total DESC")) {
            ResultSet r = prep.executeQuery();
            while (r.next()) {
                series.getData().add(new XYChart.Data<>(
                    r.getString("Username"),
                    r.getDouble("Total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        empSalesChart.getData().add(series);
        empSalesChart.setLegendVisible(false);
    }

    private void loadEmployeesTable() {
        empUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        empTimeInCol.setCellValueFactory(new PropertyValueFactory<>("timeIn"));
        empTimeOutCol.setCellValueFactory(new PropertyValueFactory<>("timeOut"));

        // Role badge
        empRoleCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                if (item.equals("Admin")) {
                    badge.setStyle("-fx-background-color: #f5ead8; -fx-border-color: #c9a87c; " +
                                   "-fx-border-radius: 20; -fx-background-radius: 20; " +
                                   "-fx-padding: 2 10 2 10; -fx-text-fill: #7A4A2A; -fx-font-weight: bold;");
                } else {
                    badge.setStyle("-fx-background-color: #eaf3de; -fx-border-color: #a2c5a2; " +
                                   "-fx-border-radius: 20; -fx-background-radius: 20; " +
                                   "-fx-padding: 2 10 2 10; -fx-text-fill: #3B6D11; -fx-font-weight: bold;");
                }
                setGraphic(badge);
            }
        });
        
        empRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Status dot
        empStatusCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label lbl = new Label("● " + item);
                lbl.setStyle("-fx-text-fill: " +
                    (item.equals("Active") ? "#3B6D11" : "#97343C") + "; -fx-font-size: 11;");
                setGraphic(lbl);
            }
        });
        empStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Actions buttons
        empActionsCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final Button editBtn   = new Button("✎");
            private final Button deleteBtn = new Button("✕");
            private final HBox box = new HBox(6, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #F5EFE8; -fx-border-color: #DED0BF; " +
                                 "-fx-border-radius: 6; -fx-background-radius: 6;");
                deleteBtn.setStyle("-fx-background-color: #fff5f5; -fx-border-color: #f0bfbf; " +
                                   "-fx-border-radius: 6; -fx-background-radius: 6; " +
                                   "-fx-text-fill: #97343C;");
                box.setAlignment(Pos.CENTER);
                deleteBtn.setOnAction(e -> {
                    EmployeeRow row = getTableView().getItems().get(getIndex());
                    deleteEmployee(row.getUserId());
                });
                
                editBtn.setOnAction(e -> {
                EmployeeRow row = getTableView().getItems().get(getIndex());
                try {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/fxml/EditEmployee.fxml"));
                    javafx.scene.Parent root = loader.load();

                    // Pass the employee data to the controller
                    EditEmployeeController controller = loader.getController();
                    controller.setEmployee(row);

                    Stage stage = new Stage();
                    stage.setTitle("Edit Employee");
                    stage.setScene(new javafx.scene.Scene(root));
                    stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.showAndWait();

                    // Refresh table after dialog closes
                    loadEmployeesTable();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
                
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        empActionsCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        // Load data
        ObservableList<EmployeeRow> list = FXCollections.observableArrayList();
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement prep = con.prepareStatement(
                "SELECT UserID, Username, Role, IsActive, TimeIn, TimeOut " +
                "FROM users " +
                "WHERE UserID IN (SELECT DISTINCT UserID FROM transactions) " +
                "OR Role = 'Admin' " +
                "ORDER BY Username")){
            ResultSet r = prep.executeQuery();
            java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("hh:mm a");
            while (r.next()) {
                String timeIn  = r.getTimestamp("TimeIn")  != null
                    ? fmt.format(r.getTimestamp("TimeIn"))  : "—";
                String timeOut = r.getTimestamp("TimeOut") != null
                    ? fmt.format(r.getTimestamp("TimeOut")) : "—";
                String status  = r.getInt("IsActive") == 1 ? "Active" : "Inactive";
                list.add(new EmployeeRow(
                    r.getInt("UserID"),
                    r.getString("Username"),
                    r.getString("Role"),
                    status, timeIn, timeOut
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        employeesTable.setItems(list);
        employeesTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void deleteEmployee(int userId) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Employee");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this employee?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try (Connection con = DataBaseConnection.getConnection();
                     PreparedStatement prep = con.prepareStatement(
                         "DELETE FROM users WHERE UserID = ?")) {
                    prep.setInt(1, userId);
                    prep.executeUpdate();
                    loadEmployeesTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    

    @FXML
    private void handleAddEmployee() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/AddEmployee.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Employee");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            // Refresh table after dialog closes
            loadEmployeesTable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
