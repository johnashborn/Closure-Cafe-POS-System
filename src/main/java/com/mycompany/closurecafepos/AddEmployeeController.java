/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.closurecafepos;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddEmployeeController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField pinField;
    @FXML private ComboBox<String> roleComboBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleComboBox.getItems().addAll("Admin", "Cashier");
        roleComboBox.setValue("Cashier");
    }

    @FXML
    private void handleSave() {
        String username = usernameField.getText().trim();
        String pin      = pinField.getText().trim();
        String role     = roleComboBox.getValue();

        if (username.isEmpty() || pin.isEmpty() || role == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields!");
            alert.showAndWait();
            return;
        }

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement prep = con.prepareStatement(
                 "INSERT INTO users (Username, PIN, Role, IsActive) VALUES (?, ?, ?, 0)")) {

            prep.setString(1, username);
            prep.setString(2, pin);
            prep.setString(3, role);
            prep.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Employee added successfully!");
            alert.showAndWait();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save employee. Please try again.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
