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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author arant
 */
public class EmployeeLoginController implements Initializable {
    
    @FXML private VBox loginCard;
        
    @FXML
    private TextField employee_username;
    
    @FXML
    private PasswordField employee_pin;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
     
       
         playSlideUpAnimation();
    }    
    
    @FXML
    private void switchToAdmin(MouseEvent event) throws IOException {
        App.setRoot("Admin_login");
    }
    
     // pang animation
   private void playSlideUpAnimation() {
    javafx.animation.PauseTransition wait = new javafx.animation.PauseTransition(
        javafx.util.Duration.millis(200));

   wait.setOnFinished(e -> {
    // Multi-step fade for smooth ease in
    javafx.animation.KeyFrame f0 = new javafx.animation.KeyFrame(
        javafx.util.Duration.ZERO,
        new javafx.animation.KeyValue(loginCard.opacityProperty(), 0));

    javafx.animation.KeyFrame f1 = new javafx.animation.KeyFrame(
        javafx.util.Duration.millis(300),
        new javafx.animation.KeyValue(loginCard.opacityProperty(), 0.05,
            javafx.animation.Interpolator.LINEAR));

    javafx.animation.KeyFrame f2 = new javafx.animation.KeyFrame(
        javafx.util.Duration.millis(700),
        new javafx.animation.KeyValue(loginCard.opacityProperty(), 0.4,
            javafx.animation.Interpolator.LINEAR));

    javafx.animation.KeyFrame f3 = new javafx.animation.KeyFrame(
        javafx.util.Duration.millis(1200),
        new javafx.animation.KeyValue(loginCard.opacityProperty(), 1.0,
            javafx.animation.Interpolator.LINEAR));

    javafx.animation.Timeline fadeTimeline = new javafx.animation.Timeline(f0, f1, f2, f3);

    // Slide
    javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(
        javafx.util.Duration.millis(900), loginCard);
    slide.setFromY(40);
    slide.setToY(0);
    slide.setInterpolator(javafx.animation.Interpolator.SPLINE(0.16, 1.0, 0.3, 1.0));

    fadeTimeline.play();
    slide.play();
});

    wait.play();
}

    
    
    @FXML
    public void handleEmployeeLogin(){
        String username = employee_username.getText();
        String pin = employee_pin.getText();
        
        
        if(username.isEmpty() || pin.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields!");
            alert.showAndWait();
            return;
        }
        
        //connect sa databse
        
        try(Connection con = DataBaseConnection.getConnection();
                PreparedStatement prep = con.prepareStatement(
                    "SELECT * FROM USERS WHERE USERNAME =? AND PIN = ?")){
            
            prep.setString(1, username);
            prep.setString(2, pin);
            
            try(ResultSet r = prep.executeQuery()){
                if(r.next()){
                    
                    //para mahimong reference ang ID sa current na ga log in sa system (for time out and ang username   
                     SessionManager.setLoggedInUserId(r.getInt("UserID"));
                     SessionManager.setLoggedInUserName(r.getString("Username"));
                     
                    
                    // Get the employee's ID from the result set
                    int userId = r.getInt("UserID"); // change "ID" to your actual column name

                    // Record the time in
                    try(PreparedStatement timeIn = con.prepareStatement(
                            "UPDATE USERS SET TIMEIN = ?, isActive = 1 WHERE UserID = ?")){  // change column/table names as needed
                        timeIn.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
                        timeIn.setInt(2, userId);
                        timeIn.executeUpdate();
                    }

                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Login Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Welcome Employee!!");
                    alert.showAndWait();
                
                //kuhaon ang kuan para ma amx ang menu window
                Stage stage = (Stage) employee_username.getScene().getWindow();
                App.setRoot("menu");

                // resize to match menu dimensions
                stage.setWidth(1136);
                stage.setHeight(890);
                stage.centerOnScreen(); // centers it on the monitor after resize
                
               }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid username or PIN!");
                    alert.showAndWait();
                }
            }
            
            
            
        }catch (SQLException e){
            e.printStackTrace();
        }catch (IOException e) {
        e.printStackTrace();
        }
        
        
        
        
    }
    
}
