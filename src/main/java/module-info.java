module com.mycompany.closurecafepos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
 

    opens com.mycompany.closurecafepos to javafx.fxml;
    opens fxml to javafx.fxml; 
    exports com.mycompany.closurecafepos;
}
