package org.example.matchinggameserver.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.matchinggameserver.dao.UserDAO;
import org.example.matchinggameserver.view.Admin;

public class AdminController implements Runnable{

    @FXML
    private TextArea threadListTextArea;

    @FXML
    private TextArea roomListTextArea;

    @FXML
    private TextArea messListTextArea;

    @FXML
    private TextField serverMess;

    @FXML
    private Button getThreadsButton;

    @FXML
    private Button getRoomsButton;

    @FXML
    private Button publishMessageButton;

    private final UserDAO userDAO;

    public AdminController() {
        userDAO = new UserDAO();
    }

    // Method to get the list of threads
    @FXML
    protected void getThreads() {
//        // TODO: Implement the logic to retrieve and display threads
//        threadListTextArea.clear();
//        // Example data, replace this with your actual thread fetching logic
//        threadListTextArea.setText("Thread 1\nThread 2\nThread 3");


        StringBuilder res = new StringBuilder();
        String room;
        int i = 1;
        for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
            if (serverThread.getRoom() == null)
                room = null;
            else room = "" + serverThread.getRoom().getId();
            if (serverThread.getUser() != null) {
                res.append(i).append(". Client-number: ").append(serverThread.getClientNumber()).append(", User-ID: ").append(serverThread.getUser().getID()).append(", Room: ").append(room).append("\n");
            } else {
                res.append(i).append(". Client-number: ").append(serverThread.getClientNumber()).append(", User-ID: null, Room: ").append(room).append("\n");
            }
            i++;
        }
//        threadRoomListView.setText(res.toString());
        threadListTextArea.clear();
        threadListTextArea.setText(res.toString());
    }

    // Method to get the list of rooms
    @FXML
    protected void getRooms() {
//        // TODO: Implement the logic to retrieve and display rooms
//        roomListTextArea.clear();
//        // Example data, replace this with your actual room fetching logic
//        roomListTextArea.setText("Room A\nRoom B\nRoom C");
        StringBuilder res = new StringBuilder();
        int i = 1;
        for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
            Room room1 = serverThread.getRoom();
            String listUser = "List user ID: ";
            if (room1 != null) {
                if (room1.getNumberOfUser() == 1) {
                    listUser += room1.getUser1().getUser().getID();
                } else {
                    listUser += room1.getUser1().getUser().getID() + ", " + room1.getUser2().getUser().getID();
                }
                res.append(i).append(". Room_ID: ").append(room1.getId()).append(", Number of player: ").append(room1.getNumberOfUser()).append(", ").append(listUser).append("\n");
                i++;
            }

        }
//        threadRoomListView.setText(res.toString());
        roomListTextArea.clear();
        roomListTextArea.setText(res.toString());

    }

    // Method to send a message
    @FXML
    protected void sendMessage() {
        String message = serverMess.getText().trim();
        if (!message.isEmpty() && Server.serverThreadBus != null) {
            Server.serverThreadBus.boardCast(-1, "chat-server,Thông báo từ máy chủ : " + message);
//            messListTextArea.appendText("Server: " + message + "\n");
            addMessage("Server: " + message);
            serverMess.clear();
        } else if (Server.serverThreadBus == null) {
            messListTextArea.appendText("Error: Server is not initialized.\n");
        }
    }


    public void addMessage(String message) {
        Platform.runLater(() -> {
            messListTextArea.appendText(message + "\n");
        });
    }


    @Override
    public void run() {
        new AdminController().getThreads();
    }
    @FXML
    public void initialize() {
        System.out.println("messListTextArea initialized: " + (messListTextArea != null));
    }
}
