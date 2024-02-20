module com.example.ksiegarnia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.ksiegarnia to javafx.fxml;
    exports com.example.ksiegarnia;
}