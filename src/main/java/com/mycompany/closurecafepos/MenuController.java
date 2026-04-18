package com.mycompany.closurecafepos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MenuController implements Initializable {

    @FXML private Pane logout;
    @FXML private Text employeeName;
    @FXML private Text menuTime;
    @FXML private GridPane menuGrid;
    @FXML private ScrollPane scrollpPane;
    @FXML private VBox orderPanel;

    // Order sidebar FXML elements
    @FXML private VBox orderItemsContainer;
    @FXML private Text subtotalText;
    @FXML private Text taxText;
    @FXML private Text totalText;
    @FXML private Text orderCountText;

    // Order tracking
    private Map<Integer, OrderItem> orderItems = new LinkedHashMap<>();
    private List<Products> allProducts = new ArrayList<>();
    
    
    @FXML private Label employeeInitials;
    
    @FXML private Text orderNumberText;
    
    
    @FXML private Button btnAll, btnCoffee, btnFrappes, btnDesserts, btnPastries;
    @FXML private TextField searchField;
    private String currentCategory = "All";

    @FXML private void filterAll()      { setActiveMenuBtn(btnAll);      currentCategory = "All";      applyMenuFilter(); }
    @FXML private void filterCoffee()   { setActiveMenuBtn(btnCoffee);   currentCategory = "Coffee";   applyMenuFilter(); }
    @FXML private void filterFrappes()  { setActiveMenuBtn(btnFrappes);  currentCategory = "Frappes";  applyMenuFilter(); }
    @FXML private void filterDesserts() { setActiveMenuBtn(btnDesserts); currentCategory = "Desserts"; applyMenuFilter(); }
    @FXML private void filterPastries() { setActiveMenuBtn(btnPastries); currentCategory = "Pastries"; applyMenuFilter(); }

@FXML private void handleSearch() { applyMenuFilter(); }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // order number
        loadOrderNumber();
        
        // Clock
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            String time = java.time.LocalTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
            menuTime.setText(time);
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        employeeName.setText(SessionManager.getLoggedInUserName());
        loadMenuProducts();
        
        // Get initials from username para i display
        String username = SessionManager.getLoggedInUserName();
        String[] parts = username.split("[_ ]");
        String initials;
        if (parts.length >= 2) {
            initials = (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        } else {
            initials = username.length() >= 2
                ? username.substring(0, 2).toUpperCase()
                : username.substring(0, 1).toUpperCase();
        }
        employeeInitials.setText(initials);
    }
    
    private void loadOrderNumber() {
    try (Connection con = DataBaseConnection.getConnection();
         PreparedStatement prep = con.prepareStatement(
             "SELECT COUNT(*) + 1 FROM transactions")) {
        ResultSet r = prep.executeQuery();
        if (r.next()) {
            int nextOrder = r.getInt(1);
            orderNumberText.setText(String.format("Order #%03d", nextOrder));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private void loadMenuProducts() {
        allProducts.clear();
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement prep = con.prepareStatement(
                 "SELECT ProductID, Name, Category, Price, IsAvailable, Picture, Description " +
                 "FROM products WHERE IsAvailable = 1 ORDER BY Category, Name")) {
            ResultSet r = prep.executeQuery();
            while (r.next()) {
                Products p = new Products(
                    r.getInt("ProductID"),
                    r.getString("Name"),
                    r.getString("Category"),
                    r.getDouble("Price"),
                    r.getBoolean("IsAvailable")
                );
                p.setPicture(r.getBytes("Picture"));
                p.setDescription(r.getString("Description"));
                allProducts.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        displayProducts(allProducts);
    }

    private void displayProducts(List<Products> products) {
    menuGrid.getChildren().clear();
    int col = 0, row = 0;
    for (Products p : products) {
        VBox card = createMenuCard(p);
        GridPane.setMargin(card, new Insets(8));
        menuGrid.add(card, col, row);
        col++;
        if (col == 3) { col = 0; row++; }
    }
}

    private VBox createMenuCard(Products p) {
    VBox card = new VBox(6);
    card.setPrefWidth(205.0);
    card.setMaxWidth(205.0);   // ✅ lock the max width
    card.setMinWidth(205.0);   // ✅ lock the min width too
    card.setPrefHeight(260.0);
    card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                  "-fx-border-color: #D9C8B4; -fx-border-radius: 15; " +
                  "-fx-padding: 0 0 10 0;");

    // Image — fixed size, no binding needed
    ImageView imageView = new ImageView();
    imageView.setFitWidth(205.0);   // ✅ match card width exactly
    imageView.setFitHeight(140.0);
    imageView.setPreserveRatio(false);

    // Clip — same fixed size
    javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(205, 140);
    clip.setArcWidth(30);
    clip.setArcHeight(30);
    imageView.setClip(clip);

    if (p.getPicture() != null && p.getPicture().length > 0) {
        imageView.setImage(new Image(new ByteArrayInputStream(p.getPicture())));
    }

    // Name
    Label nameLbl = new Label(p.getName());
    nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a2e1a; -fx-font-size: 13px;");
    nameLbl.setPadding(new Insets(4, 10, 0, 10));

    // Description
    Label descLbl = new Label(p.getDescription() != null ? p.getDescription() : "");
    descLbl.setStyle("-fx-text-fill: #997359; -fx-font-size: 10px;");
    descLbl.setPadding(new Insets(0, 10, 0, 10));
    descLbl.setWrapText(true);
    descLbl.setMaxWidth(185);
    descLbl.setMaxHeight(30);

    // Price
    Label priceLbl = new Label("₱" + String.format("%,.2f", p.getPrice()));
    priceLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #7A4A2A; -fx-font-size: 14px;");
    priceLbl.setPadding(new Insets(0, 10, 0, 10));

    // Button
    Button addBtn = new Button("+ Add to order");
    addBtn.setStyle("-fx-background-color: #7A4A2A; -fx-background-radius: 10; " +
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
    addBtn.setPrefWidth(170);
    addBtn.setPrefHeight(32);
    addBtn.setOnAction(e -> addToOrder(p));

    HBox btnBox = new HBox(addBtn);
    btnBox.setAlignment(Pos.CENTER);
    btnBox.setPadding(new Insets(4, 0, 0, 0));

    card.getChildren().addAll(imageView, nameLbl, descLbl, priceLbl, btnBox);
    return card;
}

    private void addToOrder(Products p) {
        if (orderItems.containsKey(p.getId())) {
            orderItems.get(p.getId()).increment();
        } else {
            orderItems.put(p.getId(), new OrderItem(p));
        }
        refreshOrderPanel();
    }

        private void refreshOrderPanel() {
        orderItemsContainer.getChildren().clear();
        orderItemsContainer.setSpacing(8);
        orderItemsContainer.setPadding(new Insets(10, 10, 10, 10));

        double subtotal = 0;
        int totalItems = 0;

        for (OrderItem item : orderItems.values()) {
            orderItemsContainer.getChildren().add(createOrderItemRow(item));
            subtotal += item.getSubtotal();
            totalItems += item.getQuantity();
        }

        double tax   = subtotal * 0.12;
        double total = subtotal + tax;

        subtotalText.setText("₱" + String.format("%,.2f", subtotal));
        taxText.setText("₱" + String.format("%,.2f", tax));
        totalText.setText("₱" + String.format("%,.2f", total));
        orderCountText.setText(totalItems + " item" + (totalItems != 1 ? "s" : "") +
                               " • " + orderItems.size() + " product" +
                               (orderItems.size() != 1 ? "s" : ""));
    }

    
        // inag add order, mao ni mo kuan sa sidebar sa menu
    private VBox createOrderItemRow(OrderItem item) {
    VBox card = new VBox(6);
    card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                  "-fx-border-color: #EDE0D0; -fx-border-radius: 12; " +
                  "-fx-padding: 10 14 10 14;");
    card.setPrefWidth(390);

    // Top row — name + price
    HBox topRow = new HBox();
    topRow.setAlignment(Pos.CENTER_LEFT);

    // Small color dot
    Pane dot = new Pane();
    dot.setPrefSize(10, 10);
    dot.setStyle("-fx-background-color: #7A4A2A; -fx-background-radius: 5;");
    HBox.setMargin(dot, new Insets(0, 8, 0, 0));

    Label nameLbl = new Label(item.getProduct().getName());
    nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a2e1a; -fx-font-size: 13px;");
    HBox.setHgrow(nameLbl, javafx.scene.layout.Priority.ALWAYS);

    Label priceLbl = new Label("₱" + String.format("%,.2f", item.getSubtotal()));
    priceLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a2e1a; -fx-font-size: 13px;");

    topRow.getChildren().addAll(dot, nameLbl, priceLbl);

    // Bottom row — minus, qty, plus, remove
    HBox bottomRow = new HBox(8);
    bottomRow.setAlignment(Pos.CENTER_LEFT);
    bottomRow.setPadding(new Insets(4, 0, 0, 0));

    Button minusBtn = new Button("-");
    minusBtn.setStyle("-fx-background-color: #F5EFE8; -fx-border-color: #DED0BF; " +
                      "-fx-border-radius: 15; -fx-background-radius: 15; " +
                      "-fx-text-fill: #4a2e1a; -fx-font-weight: bold; -fx-font-size: 14px;");
    minusBtn.setPrefSize(30, 30);

    Label qtyLbl = new Label(String.valueOf(item.getQuantity()));
    qtyLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a2e1a; -fx-font-size: 14px;");
    qtyLbl.setMinWidth(24);
    qtyLbl.setAlignment(Pos.CENTER);

    Button plusBtn = new Button("+");
    plusBtn.setStyle("-fx-background-color: #7A4A2A; -fx-border-radius: 15; " +
                     "-fx-background-radius: 15; -fx-text-fill: white; " +
                     "-fx-font-weight: bold; -fx-font-size: 14px;");
    plusBtn.setPrefSize(30, 30);

    // Spacer to push remove button to right
    Pane spacer = new Pane();
    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

    Button removeBtn = new Button("✕");
    removeBtn.setStyle("-fx-background-color: #fff0f0; -fx-border-color: #f0bfbf; " +
                       "-fx-border-radius: 8; -fx-background-radius: 8; " +
                       "-fx-text-fill: #97343C; -fx-font-size: 11px;");
    removeBtn.setPrefSize(28, 28);

    minusBtn.setOnAction(e -> {
        item.decrement();
        if (item.getQuantity() == 0) {
            orderItems.remove(item.getProduct().getId());
        }
        refreshOrderPanel();
    });

    plusBtn.setOnAction(e -> {
        item.increment();
        refreshOrderPanel();
    });

    removeBtn.setOnAction(e -> {
        orderItems.remove(item.getProduct().getId());
        refreshOrderPanel();
    });

    bottomRow.getChildren().addAll(minusBtn, qtyLbl, plusBtn, spacer, removeBtn);
    card.getChildren().addAll(topRow, bottomRow);
    return card;
}

    @FXML
    public void logOut(javafx.scene.input.MouseEvent event) throws IOException, SQLException {
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement prep = con.prepareStatement(
                 "UPDATE users SET TimeOut = NOW(), isActive = 0 WHERE UserID = ?")) {
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

        Stage stage = (Stage) logout.getScene().getWindow();
        App.setRoot("EmployeeLogin");
        stage.setWidth(900);
        stage.setHeight(630);
        stage.centerOnScreen();
    }
    
    
    private void applyMenuFilter() {
    String search = searchField.getText().toLowerCase().trim();
    List<Products> filtered = new ArrayList<>();
    for (Products p : allProducts) {
        boolean matchCat    = currentCategory.equals("All") || p.getCategory().equals(currentCategory);
        boolean matchSearch = p.getName().toLowerCase().contains(search);
        if (matchCat && matchSearch) filtered.add(p);
    }
    displayProducts(filtered);
}

    private void setActiveMenuBtn(Button active) {
        String inactive = "-fx-background-color: #F5EFE8; -fx-background-radius: 20; " +
                          "-fx-border-color: #997359; -fx-border-radius: 20; -fx-text-fill: #6f4e37;";
        String activeS  = "-fx-background-color: #6F4E37; -fx-background-radius: 20; " +
                          "-fx-border-radius: 20; -fx-text-fill: white;";
        btnAll.setStyle(inactive); btnCoffee.setStyle(inactive);
        btnFrappes.setStyle(inactive); btnDesserts.setStyle(inactive);
        btnPastries.setStyle(inactive);
        active.setStyle(activeS);
    }


    // iang order, mo save sa databse
    @FXML
    private void handlePlaceOrder() {
        if (orderItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Order");
            alert.setHeaderText(null);
            alert.setContentText("Please add items to the order first!");
            alert.showAndWait();
            return;
        }

        try (Connection con = DataBaseConnection.getConnection()) {
            con.setAutoCommit(false); // start transaction

            try {
                // Calculate totals
                double subtotal = 0;
                for (OrderItem item : orderItems.values()) {
                    subtotal += item.getSubtotal();
                }
                double tax   = subtotal * 0.12;
                double total = subtotal + tax;

                // 1. Insert into transactions table
                PreparedStatement transStmt = con.prepareStatement(
                    "INSERT INTO transactions (UserID, Date, Subtotal, Tax, TotalAmount) " +
                    "VALUES (?, NOW(), ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
                );
                transStmt.setInt(1, SessionManager.getLoggedInUserId());
                transStmt.setDouble(2, subtotal);
                transStmt.setDouble(3, tax);
                transStmt.setDouble(4, total);
                transStmt.executeUpdate();

                // Get the generated TransactionID
                ResultSet generatedKeys = transStmt.getGeneratedKeys();
                int transactionId = -1;
                if (generatedKeys.next()) {
                    transactionId = generatedKeys.getInt(1);
                }

                // 2. Insert each item into transactionitems table
                PreparedStatement itemStmt = con.prepareStatement(
                    "INSERT INTO transactionitems (TransactionID, ProductID, Quantity, UnitPrice, Subtotal) " +
                    "VALUES (?, ?, ?, ?, ?)"
                );

                for (OrderItem item : orderItems.values()) {
                    double itemSubtotal = item.getSubtotal();
                    itemStmt.setInt(1, transactionId);
                    itemStmt.setInt(2, item.getProduct().getId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getProduct().getPrice());
                    itemStmt.setDouble(5, itemSubtotal);
                    itemStmt.addBatch(); // batch insert for efficiency
                }
                itemStmt.executeBatch();

                con.commit(); // save everything
                
                // i kuan ika pila na ka number
                loadOrderNumber();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Order Placed");
                alert.setHeaderText(null);
                alert.setContentText("Order placed successfully!\nTotal: ₱" +
                    String.format("%,.2f", total));
                alert.showAndWait();

                // Clear the order
                orderItems.clear();
                refreshOrderPanel();

            } catch (SQLException e) {
                con.rollback(); // undo everything if something fails
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to place order. Please try again.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

@FXML
private void handleVoidOrder() {
    if (orderItems.isEmpty()) return;
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Void Order");
    confirm.setHeaderText(null);
    confirm.setContentText("Are you sure you want to void the current order?");
    confirm.showAndWait().ifPresent(response -> {
        if (response == javafx.scene.control.ButtonType.OK) {
            orderItems.clear();
            refreshOrderPanel();
        }
    });
}
}