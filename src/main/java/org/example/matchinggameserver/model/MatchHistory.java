package org.example.matchinggameserver.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class MatchHistory {
    private Long userId;
    private Long matchId;
    private String result;
    private Integer pointsEarned;
    private LocalDateTime createdAt;  // Thay đổi thành kiểu dữ liệu LocalDateTime

    public MatchHistory() {}

    public MatchHistory(Long userId, Long matchId, String result, Integer pointsEarned, LocalDateTime createdAt) {
        this.userId = userId;
        this.matchId = matchId;
        this.result = result;
        this.pointsEarned = pointsEarned;
        this.createdAt = createdAt;
    }

    public MatchHistory(Long userId, Long matchId, String result, Integer pointsEarned) {
        this.userId = userId;
        this.matchId = matchId;
        this.result = result;
        this.pointsEarned = pointsEarned;
        this.createdAt = LocalDateTime.now(); // Gán thời gian hiện tại
    }

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash( userId, matchId, result, pointsEarned, createdAt);
    }

    @Override
    public String toString() {
        return "(" +matchId  +
                "," + userId +
                "," + result +
                "," + pointsEarned +
                "," + createdAt +")";
    }
}