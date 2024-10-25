module org.example.matchinggameserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens org.example.matchinggameserver to javafx.fxml;
    exports org.example.matchinggameserver;
}