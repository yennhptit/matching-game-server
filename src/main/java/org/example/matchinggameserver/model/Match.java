package org.example.matchinggameserver.model;


import org.example.matchinggameserver.dao.GameDAO;

import java.util.List;

public class Match {
    private Long id;
    private User player1;
    private User player2;
    private Integer winnerId;
    private List<Card> board1;
    private List<Card> board2;
    private int score1;
    private int score2;
    private GameDAO gameDAO;

    public Match(
            User player1, User player2) {
        gameDAO = new GameDAO();
        this.player1 = player1;
        this.player2 = player2;
        this.board1 = gameDAO.getListRandomCard(10);
        this.board2 = gameDAO.getListRandomCard(10);
        this.score1 = 0;
        this.score2 = 0;
        this.id = null;
    }

    public Match() {
    }

    public String toStringPlayer1() {
        StringBuilder sb = new StringBuilder();
        sb.append("get-card,").append(id);
        sb.append(",").append(player1.getID());
        sb.append(",").append(player2.getID());

        for (Card card : board1) {
            sb.append(",").append(card.toString());
        }

        return sb.toString();
    }

    public String toStringPlayer2() {
        StringBuilder sb = new StringBuilder();
        sb.append("get-card,").append(id);
        sb.append(",").append(player2.getID());
        sb.append(",").append(player1.getID());

        for (Card card : board2) {
            sb.append(",").append(card.toString());
        }

        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public List<Card> getBoard1() {
        return board1;
    }

    public void setBoard1(List<Card> board1) {
        this.board1 = board1;
    }

    public List<Card> getBoard2() {
        return board2;
    }

    public void setBoard2(List<Card> board2) {
        this.board2 = board2;
    }

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }
}
