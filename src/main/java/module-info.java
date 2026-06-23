module com.example.carrentalsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.example.carrentalsystem to javafx.fxml;
    opens com.example.carrentalsystem.controller to javafx.fxml;
    opens com.example.carrentalsystem.view to javafx.fxml;
    opens com.example.carrentalsystem.model to javafx.base;

    exports com.example.carrentalsystem;
}