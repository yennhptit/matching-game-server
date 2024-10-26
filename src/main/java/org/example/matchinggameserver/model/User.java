package org.example.matchinggameserver.model;

public class User {
    private int ID;
    private String username;
    private String password;
    private int numberOfGame;
    private int numberOfWin;
    private int numberOfDraw;
    private boolean isOnline;
    private boolean isPlaying;
    private int star;
    private int rank;



    public User(int ID, String username, String password, int numberOfGame, int numberOfWin, int numberOfDraw, boolean isOnline, boolean isPlaying, int star, int rank) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.numberOfGame = numberOfGame;
        this.numberOfWin = numberOfWin;
        this.numberOfDraw = numberOfDraw;
        this.isOnline = isOnline;
        this.isPlaying = isPlaying;
        this.star = star;
        this.rank = rank;
    }

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(int ID, String username, boolean isOnline, boolean isPlaying) {
        this.ID = ID;
        this.username = username;
        this.isOnline = isOnline;
        this.isPlaying = isPlaying;
    }

    public User(int ID, String username, int numberOfGame, int numberOfWin, int numberOfDraw, boolean isOnline, boolean isPlaying, int star, int rank) {
        this.ID = ID;
        this.username = username;
        this.numberOfGame = numberOfGame;
        this.numberOfWin = numberOfWin;
        this.numberOfDraw = numberOfDraw;
        this.isOnline = isOnline;
        this.isPlaying = isPlaying;
        this.star = star;
        this.rank = rank;
    }
    public User(int ID, String username, int star, boolean isOnline) {
        this.ID = ID;
        this.username = username;
        this.star = star;
        this.isOnline = isOnline;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNumberOfGame() {
        return numberOfGame;
    }

    public void setNumberOfGame(int numberOfGame) {
        this.numberOfGame = numberOfGame;
    }

    public int getNumberOfWin() {
        return numberOfWin;
    }

    public void setNumberOfWin(int numberOfWin) {
        this.numberOfWin = numberOfWin;
    }

    public int getNumberOfDraw() {
        return numberOfDraw;
    }

    public void setNumberOfDraw(int numberOfDraw) {
        this.numberOfDraw = numberOfDraw;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", numberOfGame=" + numberOfGame +
                ", numberOfWin=" + numberOfWin +
                ", numberOfDraw=" + numberOfDraw +
                ", isOnline=" + isOnline +
                ", isPlaying=" + isPlaying +
                ", star=" + star +
                ", rank=" + rank +
                '}';
    }
}
