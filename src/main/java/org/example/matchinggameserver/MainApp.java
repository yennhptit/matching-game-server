//package org.example.matchinggameserver;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.image.Image;
//import javafx.stage.Stage;
//import org.example.matchinggameserver.controller.AdminController;
//
//public class MainApp extends Application {
//    private static AdminController adminController; // Thêm biến tĩnh để lưu AdminController
//
//    @Override
//    public void start(Stage primaryStage) {
//        try {
//            // Load the FXML file and set up the scene
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameserver/Admin.fxml"));
//            Scene scene = new Scene(loader.load());
//
//            // Retrieve the controller instance and set up any additional configuration
//            adminController = loader.getController(); // Lưu trữ AdminController
//
//
//            // Set window properties and show the stage
//            primaryStage.setTitle("Memory Game");
//            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/org/example/matchinggameserver/img/logo.png")));
//            primaryStage.setScene(scene);
//            primaryStage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//    public static AdminController getAdminController() { // Thêm phương thức để truy xuất AdminController
//        return adminController;
//    }
//}


package org.example.matchinggameserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.matchinggameserver.controller.AdminController;
import org.example.matchinggameserver.controller.Server;

public class MainApp extends Application {
    private static AdminController adminController; // Thêm biến tĩnh để lưu AdminController

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file and set up the scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/matchinggameserver/Admin.fxml"));
            Scene scene = new Scene(loader.load());

            // Retrieve the controller instance and set up any additional configuration
            adminController = loader.getController(); // Lưu trữ AdminController

            // Set window properties and show the stage
            primaryStage.setTitle("Memory Game");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/org/example/matchinggameserver/img/logo.png")));
            primaryStage.setScene(scene);

            // Ngăn không cho phép thay đổi kích thước cửa sổ
            primaryStage.setResizable(false);

            primaryStage.show();

            // Start the server in a new thread
            new Thread(Server::startServer).start(); // Khởi động server trong luồng riêng

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static AdminController getAdminController() { // Thêm phương thức để truy xuất AdminController
        return adminController;
    }
}
