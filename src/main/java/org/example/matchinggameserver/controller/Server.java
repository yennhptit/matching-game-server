//package org.example.matchinggameserver.controller;
//
//import org.example.matchinggameserver.MainApp;
//import org.example.matchinggameserver.view.Admin;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//public class Server {
//
//    public static volatile ServerThreadBus serverThreadBus = new ServerThreadBus();
//    public static Socket socketOfServer;
//    public static int ROOM_ID;
//    public static volatile Admin admin;
//    public static volatile AdminController adminController; // Thêm biến tĩnh để lưu AdminController
//
//
//    public static void main(String[] args) {
//        // Start JavaFX Application in a separate thread
////        new Thread(() -> {
////            try {
////                // Launch the JavaFX application
////                MainApp.main(args);
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }).start();
//
//        ServerSocket listener = null;
//        serverThreadBus = new ServerThreadBus();
//        System.out.println("Server is waiting to accept user...");
//        int clientNumber = 0;
//        ROOM_ID = 0;
//
//        try {
//            listener = new ServerSocket(7777);
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(
//                10,
//                100,
//                10,
//                TimeUnit.SECONDS,
//                new ArrayBlockingQueue<>(8)
//        );
////        admin = new Admin();
////        admin.run(); // Ensure the Admin UI is up and running
//        new Thread(() -> {
//            try {
//                // Launch the JavaFX application
////
////                MainApp.main(args);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        try {
//            while (true) {
//                socketOfServer = listener.accept();
//                System.out.println(socketOfServer.getInetAddress().getHostAddress());
//                ServerThread serverThread = new ServerThread(socketOfServer, clientNumber++);
//                serverThreadBus.add(serverThread);
//                System.out.println("Số thread đang chạy là: " + serverThreadBus.getLength());
//                executor.execute(serverThread);
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                listener.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//}

package org.example.matchinggameserver.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    public static volatile ServerThreadBus serverThreadBus = new ServerThreadBus();
    public static Socket socketOfServer;
    public static int ROOM_ID;

    public static void startServer() { // Phương thức tĩnh để khởi động server
        ServerSocket listener = null;
        serverThreadBus = new ServerThreadBus();
        System.out.println("Server is waiting to accept user...");
        int clientNumber = 0;
        ROOM_ID = 0;

        try {
            listener = new ServerSocket(7777);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10,
                100,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8)
        );

        try {
            while (true) {
                socketOfServer = listener.accept();
                System.out.println(socketOfServer.getInetAddress().getHostAddress());
                ServerThread serverThread = new ServerThread(socketOfServer, clientNumber++);
                serverThreadBus.add(serverThread);
                System.out.println("Số thread đang chạy là: " + serverThreadBus.getLength());
                executor.execute(serverThread);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                listener.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

