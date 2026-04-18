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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddProductController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private ImageView productImageView;
    @FXML private Label imageLabel;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> availabilityComboBox;
    
   

    private byte[] imageBytes = null;
    private int editProductId  = -1; // -1 means adding new

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoryComboBox.getItems().addAll("Coffee", "Frappes", "Desserts", "Pastries");
        categoryComboBox.setValue("Coffee");

        availabilityComboBox.getItems().addAll("Available", "Unavailable");
        availabilityComboBox.setValue("Available");
    }

    // Called when editing an existing product
    public void setProduct(Products p) {
        this.editProductId = p.getId();
        titleLabel.setText("Edit Product");
        nameField.setText(p.getName());
        priceField.setText(String.valueOf(p.getPrice()));
        categoryComboBox.setValue(p.getCategory());
        availabilityComboBox.setValue(p.getIsAvailable() ? "Available" : "Unavailable");
        descriptionField.setText(p.getDescription() != null ? p.getDescription() : "");

        // Load existing image
        if (p.getPicture() != null && p.getPicture().length > 0) {
            imageBytes = p.getPicture();
            Image img = new Image(new ByteArrayInputStream(imageBytes));
            productImageView.setImage(img);
            imageLabel.setText("Image loaded");
        }
    }

    @FXML
    private void handlePickImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
         // Set initial directory para deretso nas folder kay kapoy mag navigate hahays
        File initialDir = new File("C:/Users/arant/OneDrive/Documents/NetBeansProjects/ClosureCafePOS/src/main/resources/images");
        if (initialDir.exists()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        Stage stage = (Stage) nameField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                imageBytes = Files.readAllBytes(file.toPath());
                Image img = new Image(new ByteArrayInputStream(imageBytes));
                productImageView.setImage(img);
                imageLabel.setText(file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSave() {
        String name        = nameField.getText().trim();
        String priceText   = priceField.getText().trim();
        String category    = categoryComboBox.getValue();
        String avail       = availabilityComboBox.getValue();
        String description = descriptionField.getText().trim();

        // Validate
        if (name.isEmpty() || priceText.isEmpty() || category == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in Name, Price, and Category!");
            alert.showAndWait();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Price must be a valid number!");
            alert.showAndWait();
            return;
        }

        int isAvailable = avail.equals("Available") ? 1 : 0;

        try (Connection con = DataBaseConnection.getConnection()) {
            if (editProductId == -1) {
                // INSERT new product
                PreparedStatement prep = con.prepareStatement(
                    "INSERT INTO products (Name, Price, Category, IsAvailable, Description, Picture) " +
                    "VALUES (?, ?, ?, ?, ?, ?)");
                prep.setString(1, name);
                prep.setDouble(2, price);
                prep.setString(3, category);
                prep.setInt(4, isAvailable);
                prep.setString(5, description);
                prep.setBytes(6, imageBytes);
                prep.executeUpdate();
            } else {
                // UPDATE existing product
                if (imageBytes != null) {
                    PreparedStatement prep = con.prepareStatement(
                        "UPDATE products SET Name=?, Price=?, Category=?, " +
                        "IsAvailable=?, Description=?, Picture=? WHERE ProductID=?");
                    prep.setString(1, name);
                    prep.setDouble(2, price);
                    prep.setString(3, category);
                    prep.setInt(4, isAvailable);
                    prep.setString(5, description);
                    prep.setBytes(6, imageBytes);
                    prep.setInt(7, editProductId);
                    prep.executeUpdate();
                } else {
                    // No new image selected, keep existing
                    PreparedStatement prep = con.prepareStatement(
                        "UPDATE products SET Name=?, Price=?, Category=?, " +
                        "IsAvailable=?, Description=? WHERE ProductID=?");
                    prep.setString(1, name);
                    prep.setDouble(2, price);
                    prep.setString(3, category);
                    prep.setInt(4, isAvailable);
                    prep.setString(5, description);
                    prep.setInt(6, editProductId);
                    prep.executeUpdate();
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(editProductId == -1
                ? "Product added successfully!"
                : "Product updated successfully!");
            alert.showAndWait();

            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save product. Please try again.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
    
    
 
    
    
    // todo 
    // 1. fix this file
    // 2. load the products in the menu
    // finish everything
}
