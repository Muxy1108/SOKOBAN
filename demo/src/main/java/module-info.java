module SOKOBAN {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.demo to javafx.fxml;
    exports SOKOBAN;
}