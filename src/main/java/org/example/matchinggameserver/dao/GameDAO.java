package org.example.matchinggameserver.dao;

import org.example.matchinggameserver.model.Card;
import org.example.matchinggameserver.model.Match;
import org.example.matchinggameserver.model.MatchHistory;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameDAO extends DAO{

    private static final int num_card = 5;

    public Match updateUserPoint(Long matchId, int userId, boolean isPlusPoint) {
        String sql;
        int pointChange = isPlusPoint ? 1 : 0;
        Match updatedMatch = null; // Đối tượng Match để trả về

        try {
            con.setAutoCommit(false);

            // Kiểm tra người chơi
            String checkPlayerSql = "SELECT player1_id, player2_id, player1_score, player2_score FROM `match` WHERE id = ?";
            try (PreparedStatement checkStatement = con.prepareStatement(checkPlayerSql)) {
                checkStatement.setLong(1, matchId);
                ResultSet rs = checkStatement.executeQuery();

                if (rs.next()) {
                    int player1Id = rs.getInt("player1_id");
                    int player2Id = rs.getInt("player2_id");
                    int player1Score = rs.getInt("player1_score");
                    int player2Score = rs.getInt("player2_score");

                    // Cập nhật điểm cho người chơi
                    if (userId == player1Id) {
                        sql = "UPDATE `match` SET player1_score = player1_score + ? WHERE id = ?";
                        player1Score += pointChange;
                    } else if (userId == player2Id) {
                        sql = "UPDATE `match` SET player2_score = player2_score + ? WHERE id = ?";
                        player2Score += pointChange;
                    } else {
                        System.out.println("User ID không hợp lệ cho trận đấu này.");
                        return null; // Hoặc ném ngoại lệ
                    }

                    // Thực hiện cập nhật điểm
                    try (PreparedStatement updateStatement = con.prepareStatement(sql)) {
                        updateStatement.setInt(1, pointChange);
                        updateStatement.setLong(2, matchId);
                        updateStatement.executeUpdate();
                    }

                    // Kiểm tra xem ai đến 5 điểm trước và cập nhật winner_id
                    int winnerId = -1;
                    if (player1Score >= 10) {
                        winnerId = player1Id;
                    } else if (player2Score >= 10) {
                        winnerId = player2Id;
                    }
                    updatedMatch = new Match();
                    updatedMatch.setId(matchId);
                    updatedMatch.setScore1(player1Score);
                    updatedMatch.setScore2(player2Score);
                    if (winnerId != -1) {
                        String updateWinnerSql = "UPDATE `match` SET winner_id = ? WHERE id = ?";
                        try (PreparedStatement winnerStatement = con.prepareStatement(updateWinnerSql)) {
                            winnerStatement.setInt(1, winnerId);
                            winnerStatement.setLong(2, matchId);
                            winnerStatement.executeUpdate();
                        }

                        updatedMatch.setWinnerId(winnerId);
                        // Thêm lịch sử trận đấu cho cả hai người chơi vào bảng `match_history`
                        insertMatchHistory(player1Id, matchId, winnerId == player1Id ? "Win" : "Lose", player1Score);
                        insertMatchHistory(player2Id, matchId, winnerId == player2Id ? "Win" : "Lose", player2Score);
                    }

                    // Tạo đối tượng Match để trả về

                    // Thêm các trường khác nếu cần
                } else {
                    System.out.println("Không tìm thấy trận đấu với ID: " + matchId);
                    return null; // Hoặc ném ngoại lệ
                }
            }

            // Cam kết giao dịch
            con.commit();


        } catch (SQLException e) {
            // Nếu có lỗi, hoàn tác giao dịch
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true); // Bật lại chế độ tự động cam kết
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return updatedMatch; // Trả về đối tượng Match đã cập nhật
    }


    public Match getMatchById(Long matchId) {
        String query = "SELECT * FROM `match` WHERE id = ?";
        Match match = null;

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setLong(1, matchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                match = new Match();
                match.setId(rs.getLong("id"));

                int score1 = rs.getInt("player1_score");
                int score2 = rs.getInt("player2_score");
                match.setScore1(score1);
                match.setScore2(score2);

                // So sánh điểm và thiết lập winner_id
                if (score1 > score2) {
                    match.setWinnerId(rs.getInt("player1_id"));
                } else if (score2 > score1) {
                    match.setWinnerId(rs.getInt("player2_id"));
                } else {
                    match.setWinnerId(null);  // Hoà, không có người thắng
                }

                // Cập nhật winner_id trong CSDL nếu cần
                Long currentWinnerId = rs.getLong("winner_id");
                if (match.getWinnerId() != null && !match.getWinnerId().equals(currentWinnerId)) {
                    updateWinnerId(matchId, match.getWinnerId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return match;
    }

    public Match updateAndGetMatchById(Long matchId, Integer winnerId) {
        String selectQuery = "SELECT * FROM `match` WHERE id = ?";
        String updateQuery = "UPDATE `match` SET winner_id = ?, updated_at = ? WHERE id = ?";
        Match match = null;

        try {
            // Bắt đầu transaction
            con.setAutoCommit(false);

            // Cập nhật winner_id và updated_at nếu cần
            if (winnerId != null) {
                try (PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, winnerId);
                    updateStmt.setTimestamp(2, Timestamp.from(Instant.now())); // Cập nhật updated_at
                    updateStmt.setLong(3, matchId);
                    updateStmt.executeUpdate();
                }
            }

            // Lấy thông tin của match
            try (PreparedStatement selectStmt = con.prepareStatement(selectQuery)) {
                selectStmt.setLong(1, matchId);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    match = new Match();
                    match.setId(rs.getLong("id"));
                    match.setScore1(rs.getInt("player1_score"));
                    match.setScore2(rs.getInt("player2_score"));
                    match.setWinnerId(rs.getObject("winner_id") != null ? rs.getInt("winner_id") : null);

                }
            }

            // Commit transaction
            con.commit();
        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true); // Bật lại chế độ tự động commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return match;
    }

    private void updateWinnerId(Long matchId, Integer winnerId) throws SQLException {
        String updateQuery = "UPDATE `match` SET winner_id = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(updateQuery)) {
            stmt.setInt(1, winnerId);
            stmt.setTimestamp(2, Timestamp.from(Instant.now()));
            stmt.setLong(3, matchId);
            stmt.executeUpdate();
        }
    }
    private void insertMatchHistory(int userId, Long matchId, String result, int pointsEarned) {
        String historySql = "INSERT INTO `match_history` (user_id, match_id, result, points_earned, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement historyStmt = con.prepareStatement(historySql)) {
            historyStmt.setInt(1, userId);
            historyStmt.setLong(2, matchId);
            historyStmt.setString(3, result);
            historyStmt.setInt(4, pointsEarned);
            historyStmt.setDate(5, new Date(System.currentTimeMillis()));
            historyStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long create(
            int playerId1, int playerId2) {
        String sql = "INSERT INTO `match` ( player1_id, player2_id, player1_score, player2_score, winner_id, created_at, updated_at) "
                + "VALUES ( ?, ?, 0, 0, NULL, ?, ?)";

        Long matchId = -1L;
        try (PreparedStatement pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Set parameters
            pstmt.setLong(1, playerId1);
            pstmt.setLong(2, playerId2);
            pstmt.setDate(3, Date.valueOf(LocalDate.now()));
            pstmt.setDate(4, Date.valueOf(LocalDate.now()));

            // Execute update
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Get generated match ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        matchId = generatedKeys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matchId;
    }

    public List<MatchHistory> getHistory(int userId) {
        List<MatchHistory> historyList = new ArrayList<>();
        String sql = "SELECT * FROM `match` WHERE player1_id = ? OR player2_id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Long matchId = rs.getLong("id");
                Long player1Id = rs.getLong("player1_id");
                Long player2Id = rs.getLong("player2_id");
                Long player1Score = rs.getLong("player1_score");
                Long player2Score = rs.getLong("player2_score");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

                String result;
                int pointsEarned;

                // Determine the result and pointsEarned
                if ((userId == player1Id && player1Score > player2Score) ||
                        (userId == player2Id && player2Score > player1Score)) {
                    result = "win";
                    pointsEarned = 1;
                } else if (player1Score.equals(player2Score)) {
                    result = "draw";
                    pointsEarned = 0;
                } else {
                    result = "lose";
                    pointsEarned = -1;
                }

                MatchHistory matchHistory = new MatchHistory((long) userId, matchId, result, pointsEarned, createdAt);
                historyList.add(matchHistory);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return historyList;
    }

    public List<MatchHistory> getHistoryOpponent(int userId, int opponentId) {
        List<MatchHistory> historyList = new ArrayList<>();
        String sql = "SELECT * FROM `match` WHERE (player1_id = ? AND player2_id = ?) OR (player1_id = ? AND player2_id = ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            // Set parameters for userId and opponentId in both possible player positions
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, opponentId);
            preparedStatement.setInt(3, opponentId);
            preparedStatement.setInt(4, userId);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Long matchId = rs.getLong("id");
                Long player1Id = rs.getLong("player1_id");
                Long player2Id = rs.getLong("player2_id");
                Long player1Score = rs.getLong("player1_score");
                Long player2Score = rs.getLong("player2_score");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

                String result;
                int pointsEarned;

                // Determine the result and pointsEarned based on the scores and userId
                if ((userId == player1Id && player1Score > player2Score) ||
                        (userId == player2Id && player2Score > player1Score)) {
                    result = "win";
                    pointsEarned = 1;
                } else if (player1Score.equals(player2Score)) {
                    result = "draw";
                    pointsEarned = 0;
                } else {
                    result = "lose";
                    pointsEarned = -1;
                }

                // Add match history record for the user
                MatchHistory matchHistory = new MatchHistory((long) userId, matchId, result, pointsEarned, createdAt);
                historyList.add(matchHistory);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return historyList;
    }
    public List<Card> getListRandomCard(int num){
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM card ORDER BY RAND() LIMIT ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, num);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String image = rs.getString("image");
                Card card = new Card(id, image);
                cards.add(card);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<Card> doubleCards = new ArrayList<>(cards);
        doubleCards.addAll(cards);
        Collections.shuffle(doubleCards);
        return doubleCards;
    }
}
