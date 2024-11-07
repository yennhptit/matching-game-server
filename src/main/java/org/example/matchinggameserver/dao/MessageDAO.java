package org.example.matchinggameserver.dao;

import org.example.matchinggameserver.model.Message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO extends DAO {

    public MessageDAO() {
        super();
    }

    public void sendMessage(int senderId, int receiverId, String content, String type) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO messages (sender_id, receiver_id, message, timestamp, type) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, senderId);
            preparedStatement.setInt(2, receiverId);
            preparedStatement.setString(3, content);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(5, type);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Message> getMessages(int userId1, int userId2) {
        List<Message> messages = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement(
                    "SELECT * FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp ASC"
            );
            preparedStatement.setInt(1, userId1);
            preparedStatement.setInt(2, userId2);
            preparedStatement.setInt(3, userId2);
            preparedStatement.setInt(4, userId1);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("id"),
                        rs.getInt("sender_id"),
                        rs.getInt("receiver_id"),
                        rs.getString("message"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    public Message getLastMessage(int userId1, int userId2) {
        Message lastMessage = null;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(
                    "SELECT * FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp DESC LIMIT 1"
            );
            preparedStatement.setInt(1, userId1);
            preparedStatement.setInt(2, userId2);
            preparedStatement.setInt(3, userId2);
            preparedStatement.setInt(4, userId1);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                lastMessage = new Message(
                        rs.getInt("id"),
                        rs.getInt("sender_id"),
                        rs.getInt("receiver_id"),
                        rs.getString("message"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getString("type")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastMessage;
    }
    public List<Message> getLastMessagesForUser(int userId) {
        List<Message> lastMessages = new ArrayList<>();
        try {
            // SQL query to get the latest message for each user pair involving `userId`
            PreparedStatement preparedStatement = con.prepareStatement(
                    "SELECT m.id, m.sender_id, m.receiver_id, m.message, m.timestamp, m.type " +
                            "FROM messages m " +
                            "INNER JOIN ( " +
                            "    SELECT " +
                            "        CASE " +
                            "            WHEN sender_id = ? THEN receiver_id " +
                            "            ELSE sender_id " +
                            "        END AS other_user_id, " +
                            "        MAX(timestamp) AS latest_timestamp " +
                            "    FROM messages " +
                            "    WHERE sender_id = ? OR receiver_id = ? " +
                            "    GROUP BY other_user_id " +
                            ") AS latest_messages " +
                            "ON ((m.sender_id = ? AND m.receiver_id = latest_messages.other_user_id) " +
                            "    OR (m.sender_id = latest_messages.other_user_id AND m.receiver_id = ?)) " +
                            "AND m.timestamp = latest_messages.latest_timestamp " +
                            "ORDER BY m.timestamp DESC"
            );

// Set parameters
            preparedStatement.setInt(1, userId); // For sender_id
            preparedStatement.setInt(2, userId); // For the WHERE clause sender_id
            preparedStatement.setInt(3, userId); // For the WHERE clause receiver_id
            preparedStatement.setInt(4, userId); // For the ON condition sender_id
            preparedStatement.setInt(5, userId); // For the ON condition receiver_id


            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                lastMessages.add(new Message(
                        rs.getInt("id"),
                        rs.getInt("sender_id"),
                        rs.getInt("receiver_id"),
                        rs.getString("message"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastMessages;
    }


}
