package org.example.matchinggameserver.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.example.matchinggameserver.dao.UserDAO;
import org.example.matchinggameserver.view.Admin;

public class AdminController implements Runnable{

    @FXML
    private ScrollPane messageScrollPane;


    @FXML
    private TextArea threadListTextArea;

    @FXML
    private TextArea roomListTextArea;

//    @FXML
//    private TextArea messListTextArea;

    @FXML
    private TextField serverMess;

    @FXML
    private Button getThreadsButton;

    @FXML
    private Button getRoomsButton;

    @FXML
    private Button publishMessageButton;

    @FXML
    private VBox messageContainer; // VBox để chứa các tin nhắn


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
            Server.serverThreadBus.boardCast(-1, "chat-server,Server : " + message);
//            messListTextArea.appendText("Server: " + message + "\n");
            addMessage("Server: " + message);
            serverMess.clear();
        } else if (Server.serverThreadBus == null) {
//            messListTextArea.appendText("Error: Server is not initialized.\n");
            System.out.println("Error: Server is not initialized.");
        }
    }


//    public void addMessage(String message) {
////        Platform.runLater(() -> {
////            messListTextArea.appendText(message + "\n");
////        });
//        String[] messageSplit = message.split(":", 2);
//        if (messageSplit.length > 1) {
//            Text part1 = new Text(messageSplit[0]);
//            part1.setFill(Color.RED); // Đặt màu đỏ cho phần đầu
//
//            Text part2 = new Text(messageSplit[1]);
//            part2.setFill(Color.BLACK); // Đặt màu đen cho phần còn lại
//
//            // Để in đậm, bạn có thể sử dụng thuộc tính font
//            part1.setStyle("-fx-font-weight: bold;");
//
//            // Tạo một TextFlow và thêm các phần văn bản vào
//            TextFlow textFlow = new TextFlow(part1, part2);
//
//            // Thêm TextFlow vào VBox
//            messageContainer.getChildren().add(textFlow);
//        }
//
//    }
//public void addMessage(String message) {
//    System.out.println("check message: " + message);
//    // Kiểm tra xem thông điệp có dạng [16] hay không
//    if (message.matches("\\[\\d+\\].*")) {
//        // Tách số trong ngoặc vuông và phần còn lại
//        String[] parts = message.split(" ", 2);
//
//        // Tạo phần văn bản cho số trong ngoặc vuông
//        Text part1 = new Text(parts[0] + " "); // Đây là [16]
//        part1.setFill(Color.RED); // Đặt màu đỏ cho phần này
//        part1.setFont(Font.font("System", FontWeight.BOLD, 15)); // Đặt cỡ chữ 15 và in đậm
//
//        // Tạo phần văn bản cho phần còn lại
//        Text part2 = new Text(parts.length > 1 ? parts[1] : ""); // Đây là abc đang online
//        part2.setFill(Color.BLACK); // Đặt màu đen cho phần này
//        part2.setFont(Font.font(15)); // Đặt cỡ chữ 15
//
//        // Tạo một TextFlow để chứa các phần văn bản
//        TextFlow textFlow = new TextFlow(part1, part2);
//
//        // Thêm TextFlow vào VBox
//        Platform.runLater(() -> {
//            messageContainer.getChildren().add(textFlow);
//            scrollToBottom(); // Cuộn xuống cuối sau khi thêm thông điệp
//        });
//    } else {
//        // Tách thông điệp thành hai phần nếu có dấu ':'
//        String[] messageSplit = message.split(":", 2);
//        if (messageSplit.length > 1) {
//            // Tạo phần văn bản cho phần đầu tiên
//            Text part1 = new Text(messageSplit[0] + ":"); // Ví dụ: "Server:"
//            part1.setFill(Color.RED); // Đặt màu đỏ cho phần đầu
//            part1.setFont(Font.font("System", FontWeight.BOLD, 15)); // Đặt cỡ chữ 15 và in đậm
//
//            // Tạo phần văn bản cho phần còn lại
//            Text part2 = new Text(messageSplit[1]);
//            part2.setFill(Color.BLACK); // Đặt màu đen cho phần còn lại
//            part2.setFont(Font.font(15)); // Đặt cỡ chữ 15
//
//            // Tạo một TextFlow để chứa các phần văn bản
//            TextFlow textFlow = new TextFlow(part1, part2);
//
//            // Thêm TextFlow vào VBox
//            Platform.runLater(() -> {
//                messageContainer.getChildren().add(textFlow);
//                scrollToBottom(); // Cuộn xuống cuối sau khi thêm thông điệp
//            });
//        } else {
//            // Nếu không có phần nào được tách ra, thêm toàn bộ thông điệp
//            Platform.runLater(() -> {
//                Text fullMessage = new Text(message);
//                fullMessage.setFill(Color.BLACK);
//                fullMessage.setFont(Font.font(15));
//                messageContainer.getChildren().add(fullMessage);
//                scrollToBottom(); // Cuộn xuống cuối sau khi thêm thông điệp
//            });
//        }
//    }
//    messageContainer.requestLayout(); // Yêu cầu sắp xếp lại layout
//}
public void addMessage(String message) {
    // Kiểm tra xem thông điệp có dạng [16] hay không
    if (message.matches("\\[\\d+\\].*")) {
        // Tách số trong ngoặc vuông và phần còn lại
        String[] parts = message.split(" ", 2);

        // Tạo phần văn bản cho số trong ngoặc vuông
        Text part1 = new Text(parts[0] + " "); // Đây là [16]
        part1.setFill(Color.RED); // Đặt màu đỏ cho phần này
        part1.setFont(Font.font("System", FontWeight.BOLD, 15)); // Đặt cỡ chữ 15 và in đậm

        // Tạo phần văn bản cho phần còn lại
        Text part2 = new Text(parts.length > 1 ? parts[1] : ""); // Đây là abc đang online
        part2.setFill(Color.BLACK); // Đặt màu đen cho phần này
        part2.setFont(Font.font(15)); // Đặt cỡ chữ 15

        // Tạo một TextFlow để chứa các phần văn bản
        TextFlow textFlow = new TextFlow(part1, part2);

        // Thêm TextFlow vào VBox
        Platform.runLater(() -> {
            messageContainer.getChildren().add(textFlow);
        });
    } else {
        // Tách thông điệp thành hai phần nếu có dấu ':'
        String[] messageSplit = message.split(":", 2);
        if (messageSplit.length > 1) {
            // Tạo phần văn bản cho phần đầu tiên
            Text part1 = new Text(messageSplit[0] + ":"); // Ví dụ: "Server:"
            part1.setFill(Color.RED); // Đặt màu đỏ cho phần đầu
            part1.setFont(Font.font("System", FontWeight.BOLD, 15)); // Đặt cỡ chữ 15 và in đậm

            // Tạo phần văn bản cho phần còn lại
            Text part2 = new Text(messageSplit[1]);
            part2.setFill(Color.BLACK); // Đặt màu đen cho phần còn lại
            part2.setFont(Font.font(15)); // Đặt cỡ chữ 15

            // Tạo một TextFlow để chứa các phần văn bản
            TextFlow textFlow = new TextFlow(part1, part2);

            // Thêm TextFlow vào VBox
            Platform.runLater(() -> {
                messageContainer.getChildren().add(textFlow);
            });
        } else {
            // Nếu không có phần nào được tách ra, thêm toàn bộ thông điệp
            Platform.runLater(() -> {
                Text fullMessage = new Text(message);
                fullMessage.setFill(Color.BLACK);
                fullMessage.setFont(Font.font(15));
                messageContainer.getChildren().add(fullMessage);
            });
        }
    }
}

    // Phương thức cuộn đến cuối của ScrollPane
    private void scrollToBottom() {
        // Giả sử bạn có một ScrollPane với fx:id là scrollPane
        messageScrollPane.setVvalue(1.0);
    }




    @Override
    public void run() {
        new AdminController().getThreads();
    }
    @FXML
    public void initialize() {
        assert messageScrollPane != null : "messageScrollPane was not injected";
    }
}
