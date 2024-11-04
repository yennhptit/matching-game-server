package org.example.matchinggameserver.model;

public class Card {

    private int id;

    private String image;

    public Card() {}

    public Card(int id, String image) {
        this.id = id;
        this.image = image;
    }

    public String saveCard(String image){
        String sql = String.format("INSERT INTO Cards (image) VALUES ('%s')", image);
        return sql;
    }


    @Override
    public String toString() {
        return "(" + id + ", " + image + ")";
    }
}
