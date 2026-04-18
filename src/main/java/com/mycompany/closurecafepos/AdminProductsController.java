/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.closurecafepos;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author arant
 */
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdminProductsController implements Initializable {

    @FXML private GridPane productsGrid;
    @FXML private TextField searchField;
    @FXML private Button btnAll, btnCoffee, btnFrappes, btnDesserts, btnPastries;

    private List<Products> allProducts = new ArrayList<>();
    private String currentCategory = "All";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAllProducts();
        displayProducts(allProducts);
        setActiveButton(btnAll);
    }

    private void loadAllProducts() {
        allProducts.clear();
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement prep = con.prepareStatement(
                 "SELECT ProductID, Name, Category, Price, IsAvailable, Picture, Description " +
                 "FROM products ORDER BY Category, Name")) {
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
    }

    private void displayProducts(List<Products> products) {
        productsGrid.getChildren().clear();
        int col = 0, row = 0;
        for (Products p : products) {
            VBox card = createProductCard(p);
            productsGrid.add(card, col, row);
            col++;
            if (col == 3) { col = 0; row++; }
        }
    }

    private VBox createProductCard(Products p) {
        VBox card = new VBox(8);
        card.setPrefWidth(255.0);
        card.setPrefHeight(320.0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                      "-fx-border-color: #D9C8B4; -fx-border-radius: 15; " +
                      "-fx-padding: 0 0 12 0;");

        // Product image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(255);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(false);
        if (p.getPicture() != null && p.getPicture().length > 0) {
            Image img = new Image(new ByteArrayInputStream(p.getPicture()));
            imageView.setImage(img);
        } else {
            // placeholder if no image
            imageView.setStyle("-fx-background-color: #F5EFE8;");
        }

        // Clip image to rounded top corners
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(255, 160);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        imageView.setClip(clip);

        // Product info
        Label nameLbl = new Label(p.getName());
        nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a2e1a; -fx-font-size: 14px;");
        nameLbl.setPadding(new javafx.geometry.Insets(0, 10, 0, 10));

        Label categoryLbl = new Label(p.getCategory());
        categoryLbl.setStyle("-fx-text-fill: #997359; -fx-font-size: 11px;");
        categoryLbl.setPadding(new javafx.geometry.Insets(0, 10, 0, 10));
        
        // mao ni ang description
        Label descLbl = new Label(p.getDescription() != null && !p.getDescription().isEmpty() 
            ? p.getDescription() : "No description");
        descLbl.setStyle("-fx-text-fill: #997359; -fx-font-size: 11px;");
        descLbl.setPadding(new javafx.geometry.Insets(0, 10, 0, 10));
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(235);
        descLbl.setMaxHeight(40); // limits to about 2 lines

        Label priceLbl = new Label("₱" + String.format("%,.2f", p.getPrice()));
        priceLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #7A4A2A; -fx-font-size: 15px;");
        priceLbl.setPadding(new javafx.geometry.Insets(0, 10, 0, 10));

        // Available badge
        Label availLbl = new Label(p.getIsAvailable() ? "● Available" : "● Unavailable");
        availLbl.setStyle("-fx-text-fill: " + (p.getIsAvailable() ? "#3B6D11" : "#97343C") +
                          "; -fx-font-size: 10px;");
        availLbl.setPadding(new javafx.geometry.Insets(0, 10, 0, 10));

        // Edit and Delete buttons
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        editBtn.setStyle("-fx-background-color: #F5EFE8; -fx-border-color: #997359; " +
                         "-fx-border-radius: 10; -fx-background-radius: 10; " +
                         "-fx-text-fill: #6b4c33; -fx-font-weight: bold;");
        deleteBtn.setStyle("-fx-background-color: #fff5f5; -fx-border-color: #f0bfbf; " +
                           "-fx-border-radius: 10; -fx-background-radius: 10; " +
                           "-fx-text-fill: #97343C; -fx-font-weight: bold;");
        editBtn.setPrefWidth(100);
        deleteBtn.setPrefWidth(100);

        editBtn.setOnAction(e -> handleEditProduct(p));
        deleteBtn.setOnAction(e -> handleDeleteProduct(p));

        javafx.scene.layout.HBox btnBox = new javafx.scene.layout.HBox(8, editBtn, deleteBtn);
        btnBox.setAlignment(javafx.geometry.Pos.CENTER);
        btnBox.setPadding(new javafx.geometry.Insets(4, 10, 0, 10));

      
        card.getChildren().addAll(imageView, nameLbl, categoryLbl, descLbl, priceLbl, availLbl, btnBox);
        return card;
    }


    private void applyFilter() {
        String search = searchField.getText().toLowerCase().trim();
        List<Products> filtered = new ArrayList<>();
        for (Products p : allProducts) {
            boolean matchesCategory = currentCategory.equals("All") ||
                                      p.getCategory().equals(currentCategory);
            boolean matchesSearch   = p.getName().toLowerCase().contains(search);
            if (matchesCategory && matchesSearch) filtered.add(p);
        }
        displayProducts(filtered);
    }

    @FXML
    private void handleSearch() {
        applyFilter();
    }

    @FXML
    private void handleAddProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/AddProduct.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Product");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            // Refresh after adding
            loadAllProducts();
            applyFilter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //pang edit
    private void handleEditProduct(Products p) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/AddProduct.fxml"));
            Parent root = loader.load();
            AddProductController controller = loader.getController();
            controller.setProduct(p); // pre-fill with existing data
            Stage stage = new Stage();
            stage.setTitle("Edit Product");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            loadAllProducts();
            applyFilter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //pang delete
    private void handleDeleteProduct(Products p) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Product");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete \"" + p.getName() + "\"?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ProductDAO.deleteProduct(p.getId());
                loadAllProducts();
                applyFilter();
            }
        });
    }
    
    
    

        private void setActiveButton(Button activeBtn) {
        // Reset all buttons to inactive style
        String inactiveStyle = "-fx-background-color: #F5EFE8; -fx-background-radius: 20; " +
                               "-fx-border-color: #997359; -fx-border-radius: 20; " +
                               "-fx-text-fill: #6f4e37;";
        String activeStyle   = "-fx-background-color: #6F4E37; -fx-background-radius: 20; " +
                               "-fx-border-radius: 20; -fx-text-fill: white;";

        btnAll.setStyle(inactiveStyle);
        btnCoffee.setStyle(inactiveStyle);
        btnFrappes.setStyle(inactiveStyle);
        btnDesserts.setStyle(inactiveStyle);
        btnPastries.setStyle(inactiveStyle);

        // Set the clicked button to active
        activeBtn.setStyle(activeStyle);
    }
        
        // pang filter ug mga categories
        @FXML private void filterAll()      { setActiveButton(btnAll);      currentCategory = "All";      applyFilter(); }
        @FXML private void filterCoffee()   { setActiveButton(btnCoffee);   currentCategory = "Coffee";   applyFilter(); }
        @FXML private void filterFrappes()  { setActiveButton(btnFrappes);  currentCategory = "Frappes";  applyFilter(); }
        @FXML private void filterDesserts() { setActiveButton(btnDesserts); currentCategory = "Desserts"; applyFilter(); }
        @FXML private void filterPastries() { setActiveButton(btnPastries); currentCategory = "Pastries"; applyFilter(); }
    
}
