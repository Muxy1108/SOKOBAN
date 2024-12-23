module SOKOBAN {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires javafx.media;


    opens com.example.demo to javafx.fxml;
    exports SOKOBAN;
}