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

public class EditEmployeeController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField pinField;
    @FXML private ComboBox<String> roleComboBox;

    private int userId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleComboBox.getItems().addAll("Admin", "Cashier");
    }

    // Called from EmployeesController to pre-fill the form
    public void setEmployee(EmployeeRow employee) {
        this.userId = employee.getUserId();
        usernameField.setText(employee.getUsername());
        roleComboBox.setValue(employee.getRole());
        // PIN left blank intentionally
    }

    @FXML
    private void handleSave() {
        String username = usernameField.getText().trim();
        String pin      = pinField.getText().trim();
        String role     = roleComboBox.getValue();

        if (username.isEmpty() || role == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Username and Role cannot be empty!");
            alert.showAndWait();
            return;
        }

        try (Connection con = DataBaseConnection.getConnection()) {
            // If PIN is blank, don't update it
            if (pin.isEmpty()) {
                PreparedStatement prep = con.prepareStatement(
                    "UPDATE users SET Username = ?, Role = ? WHERE UserID = ?");
                prep.setString(1, username);
                prep.setString(2, role);
                prep.setInt(3, userId);
                prep.executeUpdate();
            } else {
                PreparedStatement prep = con.prepareStatement(
                    "UPDATE users SET Username = ?, PIN = ?, Role = ? WHERE UserID = ?");
                prep.setString(1, username);
                prep.setString(2, pin);
                prep.setString(3, role);
                prep.setInt(4, userId);
                prep.executeUpdate();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Employee updated successfully!");
            alert.showAndWait();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to update employee. Please try again.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
