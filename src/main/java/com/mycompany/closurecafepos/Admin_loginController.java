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
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author arant
 */
public class Admin_loginController implements Initializable {
    
    @FXML private VBox loginCard;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
       
        playSlideUpAnimation();
    }
    
    @FXML
    private void switchToEmployee(MouseEvent event) throws IOException {
        App.setRoot("Employeelogin");
    }
    
    
    // pang animation
private void playSlideUpAnimation() {
    javafx.animation.PauseTransition wait = new javafx.animation.PauseTransition(
        javafx.util.Duration.millis(150));

    wait.setOnFinished(e -> {
        // More dramatic ease-in with more keyframes
        javafx.animation.KeyFrame f0 = new javafx.animation.KeyFrame(
            javafx.util.Duration.ZERO,
            new javafx.animation.KeyValue(loginCard.opacityProperty(), 0));

        javafx.animation.KeyFrame f1 = new javafx.animation.KeyFrame(
            javafx.util.Duration.millis(200),
            new javafx.animation.KeyValue(loginCard.opacityProperty(), 0.0,
                javafx.animation.Interpolator.LINEAR));

        javafx.animation.KeyFrame f2 = new javafx.animation.KeyFrame(
            javafx.util.Duration.millis(500),
            new javafx.animation.KeyValue(loginCard.opacityProperty(), 0.1,
                javafx.animation.Interpolator.LINEAR));

        javafx.animation.KeyFrame f3 = new javafx.animation.KeyFrame(
            javafx.util.Duration.millis(900),
            new javafx.animation.KeyValue(loginCard.opacityProperty(), 0.5,
                javafx.animation.Interpolator.LINEAR));

        javafx.animation.KeyFrame f4 = new javafx.animation.KeyFrame(
            javafx.util.Duration.millis(1400),
            new javafx.animation.KeyValue(loginCard.opacityProperty(), 1.0,
                javafx.animation.Interpolator.LINEAR));

        javafx.animation.Timeline fadeTimeline = new javafx.animation.Timeline(
            f0, f1, f2, f3, f4);

        // Slide — longer duration to match fade
        javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(
            javafx.util.Duration.millis(1100), loginCard);
        slide.setFromY(60);
        slide.setToY(0);
        slide.setInterpolator(javafx.animation.Interpolator.SPLINE(0.16, 1.0, 0.3, 1.0));

        fadeTimeline.play();
        slide.play();
    });

    wait.play();
}
    
    
    @FXML
    private TextField admin_username;
    
    @FXML
    private PasswordField admin_pin;
    
  
    
    
   @FXML
public void handleLogin() throws IOException {
    String adminUsername = admin_username.getText();
    String pin = admin_pin.getText();

    if (adminUsername.isEmpty() || pin.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Please fill in all fields!");
        alert.showAndWait();
        return;
    }
    
    
    //mag connect sa database
    try (Connection connection = DataBaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
             "SELECT * FROM USERS WHERE USERNAME = ? AND PIN = ? AND ROLE = 'ADMIN' ")) {

        preparedStatement.setString(1, adminUsername);
        preparedStatement.setString(2, pin);

        try (ResultSet r = preparedStatement.executeQuery()) {
            if (r.next()) {
                
               SessionManager.setLoggedInUserId(r.getInt("UserId"));
               SessionManager.setLoggedInUserName(r.getString("Username"));
               
               
               // Get the admin's ID from the result set
                    int userId = r.getInt("UserID"); 

                    // Record the time in
                    try(PreparedStatement timeIn = connection.prepareStatement(
                        "UPDATE USERS SET TIMEIN = ? WHERE UserID = ?")){ 
                        timeIn.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
                        timeIn.setInt(2, userId);
                        timeIn.executeUpdate();
                    }
                 
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Successful");
                alert.setHeaderText(null);
                alert.setContentText("Welcome Admin!!");
                alert.showAndWait();
                
                //kuhaon ang kuan para ma amx ang menu window
                Stage stage = (Stage) admin_username.getScene().getWindow();
                App.setRoot("AdminDashboard");

                // resize to match menu dimensions
                stage.setWidth(1170);
                stage.setHeight(880);
                stage.centerOnScreen(); // centers it on the monitor after resize
                
                
               
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or PIN!");
                alert.showAndWait();
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    } 
}
    
    
}
