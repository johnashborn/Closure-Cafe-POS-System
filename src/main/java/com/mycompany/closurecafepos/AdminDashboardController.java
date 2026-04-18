/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.closurecafepos;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author arant
 */
public class AdminDashboardController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Text adminName;
    
    // paras attendance 
    @FXML
    private BarChart<String, Number> salesChart;
    @FXML private TableView<UserAttendance> attendanceTable;
    @FXML private TableColumn<UserAttendance, String> attNameCol;
    @FXML private TableColumn<UserAttendance, String> attRoleCol;
    @FXML private TableColumn<UserAttendance, String> attTimeInCol;

    // paras 4 ka pane 
    @FXML private Label totalSales;
    @FXML private Label revenue;
    @FXML private Label employees;
    @FXML private Label items;
    @FXML private Button logoutButton;
    @FXML private Button addEmployee;
    @FXML private Button addProducts;
    
    //paras employee tables
    @FXML private TableView<EmployeeRow> employeesTable;
    @FXML private TableColumn<EmployeeRow, String> empUsernameCol;
    @FXML private TableColumn<EmployeeRow, String> empRoleCol;
    @FXML private TableColumn<EmployeeRow, String> empStatusCol;
    @FXML private TableColumn<EmployeeRow, String> empTimeInCol;
    @FXML private TableColumn<EmployeeRow, String> empTimeOutCol;
    @FXML private TableColumn<EmployeeRow, String> empActionsCol;
    
    //paras products
    @FXML private TableView<Products> productsTable;
    @FXML private TableColumn<Products, String> prodNameCol;
    @FXML private TableColumn<Products, String> prodCategoryCol;
    @FXML private TableColumn<Products, String> prodPriceCol;
    @FXML private TableColumn<Products, String> prodStatusCol;
    @FXML private TableColumn<Products, String> prodActionsCol;
    
    @FXML private Pane contentArea;
    
    private ObservableList<Products> allProducts = FXCollections.observableArrayList();
    
    //load ang dhasboard contents, mao ni ang default view
    private java.util.List<javafx.scene.Node> dashboardContent;
    
    
    
    // ang mga buttons sa sidebar
    @FXML private Button btnDashboard;
    @FXML private Button btnSalesReport;
    @FXML private Button btnEmployees;
    @FXML private Button btnProducts;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO\
        
       // para mo fill jud sa pane ang kada table
        attendanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        employeesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
        adminName.setText(SessionManager.getLoggedInUserName());
        loadSalesChart();
        loadAttendance();
        loadMetrics(); 
        loadEmployees();
        loadProducts();
        setActiveSidebarButton(btnDashboard); // I active ang color sa dashboard button by default
        
        // save the default dashboard content
        dashboardContent = new java.util.ArrayList<>(contentArea.getChildren());
    }    
    
    // pang change ug color if active or dili active ang buttons a sidebar
    private void setActiveSidebarButton(Button activeBtn) {
    String inactiveStyle = "-fx-background-color: transparent; -fx-background-radius: 20; " +
                           "-fx-text-fill: rgba(245,239,232,0.6);";
    String activeStyle   = "-fx-background-color: #6F4E37; -fx-background-radius: 20; " +
                           "-fx-text-fill: white;";

    btnDashboard.setStyle(inactiveStyle);
    btnSalesReport.setStyle(inactiveStyle);
    btnEmployees.setStyle(inactiveStyle);
    btnProducts.setStyle(inactiveStyle);

    activeBtn.setStyle(activeStyle);
}
    
    // change ug view padung sa sales report

    @FXML
    public void handleShowSalesReport() {
        setActiveSidebarButton(btnSalesReport);
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/SalesReport.fxml"));
            javafx.scene.Node salesView = loader.load();
            contentArea.getChildren().setAll(salesView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleShowDashboard() {
        setActiveSidebarButton(btnDashboard);
        contentArea.getChildren().setAll(dashboardContent);
    }
    
    // para sa employee section
    @FXML
  public void handleShowEmployees() {
      setActiveSidebarButton(btnEmployees);
      try {
          javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
              getClass().getResource("/fxml/Employees.fxml"));
          javafx.scene.Node empView = loader.load();
          contentArea.getChildren().setAll(empView);
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
    
    @FXML
    public void handleShowProducts() {
        setActiveSidebarButton(btnProducts);
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/AdminProducts.fxml"));
            javafx.scene.Node prodView = loader.load();
            contentArea.getChildren().setAll(prodView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void adminLogout() throws IOException{
        // Update TimeOut for the logged-in user
    try (Connection con = DataBaseConnection.getConnection();
         PreparedStatement prep = con.prepareStatement(
             "UPDATE users SET TimeOut = NOW() WHERE UserID = ?")) {

        prep.setInt(1, SessionManager.getLoggedInUserId());
        prep.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("LogOut Successful");
    alert.setHeaderText(null);
    alert.setContentText("Good bye!!");
    alert.showAndWait();

    // Navigate back to login
    Stage stage = (Stage) adminName.getScene().getWindow();
    App.setRoot("Admin_login");
    stage.setWidth(900);  
    stage.setHeight(630);
    stage.centerOnScreen();
     
    }
    
    // I load ang data sa database sa charts
    private void loadSalesChart() {
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Sales");

    try (Connection con = DataBaseConnection.getConnection();
         PreparedStatement prep = con.prepareStatement(
             "SELECT DAYNAME(Date) as Day, SUM(TotalAmount) as Total " +
             "FROM transactions " +
             "WHERE WEEK(Date) = WEEK(NOW()) " +
             "GROUP BY DAYNAME(Date), DAYOFWEEK(Date) " +
             "ORDER BY DAYOFWEEK(Date)")) {

        ResultSet r = prep.executeQuery();
        while (r.next()) {
            series.getData().add(
                new XYChart.Data<>(r.getString("Day"), r.getDouble("Total"))
            );
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    salesChart.getData().add(series);
    salesChart.setLegendVisible(false);
}
    
    
    
    // I load ang attendance
    private void loadAttendance() {
    // Name column — avatar circle + name + position
    attNameCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) { setGraphic(null); return; }
            UserAttendance row = getTableView().getItems().get(getIndex());

            // Avatar circle with initials
            String[] parts = item.split("[_ ]");
            String initials;
            if (parts.length >= 2) {
                initials = (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
            } else {
                initials = item.length() >= 2
                    ? item.substring(0, 2).toUpperCase()
                    : item.substring(0, 1).toUpperCase();
            }
            Label avatar = new Label(initials);
            avatar.setPrefSize(32, 32);
            avatar.setStyle("-fx-background-color: #f5ead8; -fx-background-radius: 16; " +
                            "-fx-text-fill: #7A4A2A; -fx-font-weight: bold; -fx-font-size: 11; " +
                            "-fx-alignment: center;");

            // Name + position stacked
            Label nameLbl = new Label(item);
            nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a2e1a; -fx-font-size: 12;");
            Label posLbl = new Label(row.getPosition());
            posLbl.setStyle("-fx-text-fill: #997359; -fx-font-size: 10;");
            javafx.scene.layout.VBox nameBox = new javafx.scene.layout.VBox(1, nameLbl, posLbl);

            javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(8, avatar, nameBox);
            box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            setGraphic(box);
        }
    });
    attNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    // Role column — pill badge
    attRoleCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) { setGraphic(null); return; }
            Label badge = new Label(item);
            String color = item.equals("Admin") ? "#f5ead8" : "#eaf3de";
            String text  = item.equals("Admin") ? "#7A4A2A" : "#3B6D11";
            String border = item.equals("Admin") ? "#c9a87c" : "#a2c5a2";
            badge.setStyle("-fx-background-color: " + color + "; -fx-text-fill: " + text + "; " +
                           "-fx-border-color: " + border + "; -fx-border-radius: 20; " +
                           "-fx-background-radius: 20; -fx-padding: 2 10 2 10; " +
                           "-fx-font-weight: bold; -fx-font-size: 10;");
            setGraphic(badge);
        }
    });
    attRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

    // Time In column
    attTimeInCol.setCellValueFactory(new PropertyValueFactory<>("timeIn"));

    // Load data
    ObservableList<UserAttendance> list = FXCollections.observableArrayList();
    try (Connection con = DataBaseConnection.getConnection();
         PreparedStatement prep = con.prepareStatement(
             "SELECT Username, Role, TimeIn FROM users " +
             "WHERE DATE(TimeIn) = CURDATE() ORDER BY TimeIn ASC")) {
        ResultSet r = prep.executeQuery();
        while (r.next()) {
            String timeIn = r.getTimestamp("TimeIn") != null
                ? new java.text.SimpleDateFormat("hh:mm a").format(r.getTimestamp("TimeIn"))
                : "—";
            list.add(new UserAttendance(
                r.getString("Username"),
                r.getString("Role"),
                timeIn
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    attendanceTable.setItems(list);
}
    
    
    // I load ang data sa database sa 4 ka pane 
    private void loadMetrics() {
    try (Connection con = DataBaseConnection.getConnection()) {

        // Today's sales
        PreparedStatement s1 = con.prepareStatement(
            "SELECT SUM(TotalAmount) FROM transactions WHERE DATE(Date) = CURDATE()");
        ResultSet r1 = s1.executeQuery();
        if (r1.next()) totalSales.setText("₱" + String.format("%,.2f", r1.getDouble(1)));

        // Monthly revenue
        PreparedStatement s2 = con.prepareStatement(
            "SELECT SUM(TotalAmount) FROM transactions WHERE MONTH(Date) = MONTH(NOW()) AND YEAR(Date) = YEAR(NOW())");
        ResultSet r2 = s2.executeQuery();
        if (r2.next()) revenue.setText("₱" + String.format("%,.2f", r2.getDouble(1)));

        // Total employees
        PreparedStatement s3 = con.prepareStatement(
            "SELECT COUNT(*) FROM users WHERE IsActive = 1");
        ResultSet r3 = s3.executeQuery();
        if (r3.next()) employees.setText(String.valueOf(r3.getInt(1)));

        // SCALAR SUBQUERY: products with price above average
        PreparedStatement s4 = con.prepareStatement(
            "SELECT COUNT(*) FROM products " +
            "WHERE IsAvailable = 1 " +
            "AND Price > (SELECT AVG(Price) FROM products)");
        ResultSet r4 = s4.executeQuery();
        if (r4.next()) items.setText(String.valueOf(r4.getInt(1)));

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
    //mG butang ug kanang mini table sa employee
    private void loadEmployees() {
    empUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
    empTimeInCol.setCellValueFactory(new PropertyValueFactory<>("timeIn"));
    empTimeOutCol.setCellValueFactory(new PropertyValueFactory<>("timeOut"));

    // Role column — pill badge style
    empRoleCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) { setGraphic(null); return; }
            Label badge = new Label(item);
            badge.setStyle("-fx-background-color: #F5EFE8; -fx-border-color: #997359; " +
                           "-fx-border-radius: 20; -fx-background-radius: 20; " +
                           "-fx-padding: 2 10 2 10; -fx-text-fill: #6b4c33; -fx-font-weight: bold;");
            setGraphic(badge);
        }
    });
    empRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

    // Status column — green dot
    empStatusCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) { setGraphic(null); return; }
            Label lbl = new Label("● " + item);
            lbl.setStyle("-fx-text-fill: " + (item.equals("Active") ? "#3B6D11" : "#97343C") + 
                         "; -fx-font-size: 11;");
            setGraphic(lbl);
        }
    });
    empStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

    // Actions column — edit and delete buttons
    empActionsCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
        private final Button editBtn = new Button("✎");
        private final Button deleteBtn = new Button("✕");
        private final HBox box = new HBox(6, editBtn, deleteBtn);
        {
            editBtn.setStyle("-fx-background-color: #F5EFE8; -fx-border-color: #DED0BF; " +
                             "-fx-border-radius: 6; -fx-background-radius: 6;");
            deleteBtn.setStyle("-fx-background-color: #fff5f5; -fx-border-color: #f0bfbf; " +
                               "-fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #97343C;");
            box.setAlignment(Pos.CENTER);

            deleteBtn.setOnAction(e -> {
                EmployeeRow row = getTableView().getItems().get(getIndex());
                deleteEmployee(row.getUserId());
            });
        }
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : box);
        }
    });

    // Load data from database
    ObservableList<EmployeeRow> list = FXCollections.observableArrayList();
    try (Connection con = DataBaseConnection.getConnection();
         PreparedStatement prep = con.prepareStatement(
             "SELECT UserID, Username, Role, IsActive, TimeIn, TimeOut FROM users ORDER BY Username")) {
        ResultSet r = prep.executeQuery();
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("hh:mm a");
        while (r.next()) {
            String timeIn  = r.getTimestamp("TimeIn")  != null ? fmt.format(r.getTimestamp("TimeIn"))  : "—";
            String timeOut = r.getTimestamp("TimeOut") != null ? fmt.format(r.getTimestamp("TimeOut")) : "—";
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
                loadEmployees(); // refresh table
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    });
}
    
    // I load ang products sa table
    private void loadProducts() {
    // Name column — bold name + category below
    prodNameCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) { setGraphic(null); return; }
            Products row = getTableView().getItems().get(getIndex());
            Label nameLbl = new Label(item);
            nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a2e1a; -fx-font-size: 12;");
            Label catLbl = new Label(row.getCategory());
            catLbl.setStyle("-fx-text-fill: #997359; -fx-font-size: 10;");
            javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(2, nameLbl, catLbl);
            setGraphic(box);
        }
    });
    prodNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    // Category column
    prodCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

    // Price column
    prodPriceCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) { setText(null); return; }
            Products row = getTableView().getItems().get(getIndex());
            setText("₱" + String.format("%,.2f", row.getPrice()));
            setStyle("-fx-font-weight: bold; -fx-text-fill: #4a2e1a;");
        }
    });
    prodPriceCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    // Status column
    prodStatusCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) { setGraphic(null); return; }
            Products row = getTableView().getItems().get(getIndex());
            boolean available = row.getIsAvailable();
            Label lbl = new Label(available ? "● Available" : "● Unavailable");
            lbl.setStyle("-fx-text-fill: " + (available ? "#3B6D11" : "#97343C") + "; -fx-font-size: 11;");
            setGraphic(lbl);
        }
    });
    prodStatusCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    // Actions column — edit and delete
    prodActionsCol.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
        private final Button editBtn   = new Button("✎");
        private final Button deleteBtn = new Button("✕");
        private final HBox box = new HBox(6, editBtn, deleteBtn);
        {
            editBtn.setStyle("-fx-background-color: #F5EFE8; -fx-border-color: #DED0BF; " +
                             "-fx-border-radius: 6; -fx-background-radius: 6;");
            deleteBtn.setStyle("-fx-background-color: #fff5f5; -fx-border-color: #f0bfbf; " +
                               "-fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #97343C;");
            box.setAlignment(Pos.CENTER);
            deleteBtn.setOnAction(e -> {
                Products row = getTableView().getItems().get(getIndex());
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Product");
                confirm.setHeaderText(null);
                confirm.setContentText("Delete \"" + row.getName() + "\"?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        ProductDAO.deleteProduct(row.getId());
                        loadProducts();
                    }
                });
            });
        }
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : box);
        }
    });
    prodActionsCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    // Load from database
    allProducts.setAll(ProductDAO.getAllProducts());
    productsTable.setItems(allProducts);
}

    // Filter methods
    @FXML private void filterAll()      { productsTable.setItems(allProducts); }
    @FXML private void filterCoffee()   { filterByCategory("Coffee"); }
    @FXML private void filterFrappes()  { filterByCategory("Frappes"); }
    @FXML private void filterDesserts() { filterByCategory("Desserts"); }
    @FXML private void filterPastries() { filterByCategory("Pastries"); }

    private void filterByCategory(String category) {
        ObservableList<Products> filtered = FXCollections.observableArrayList();
        for (Products p : allProducts) {
            if (p.getCategory().equals(category)) filtered.add(p);
        }
        productsTable.setItems(filtered);
    }

    @FXML private void handleAddProduct() {
        // wire this up later when you build the add product dialog
    }
    
}
