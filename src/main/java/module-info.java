//module org.example.matchinggameserver {
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires java.desktop;
//    requires jdk.compiler;
//    requires java.sql;
//
//
//    exports org.example.matchinggameserver.controller to javafx.fxml; // Export your controller package
//    exports org.example.matchinggameserver; // Export your model package
//}
module org.example.matchinggameserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // or any other required modules
    requires java.desktop; // Add this line to include javax.swing

    opens org.example.matchinggameserver.controller to javafx.fxml; // Allow reflective access
    exports org.example.matchinggameserver; // Export necessary packages

}
