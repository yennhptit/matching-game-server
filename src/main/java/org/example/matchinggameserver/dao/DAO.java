package org.example.matchinggameserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DAO {
    protected Connection con;

    public DAO() {
        final String DATABASE_NAME = "memory_game"; // Your database name
        final String jdbcURL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?useSSL=false"; // Change localhost if needed
        final String JDBC_USER = "root";  // Your MySQL Workbench username
        final String JDBC_PASSWORD = "123456789"; // Your MySQL Workbench password
        String url = "jdbc:mysql://localhost:3306/memory_game?user=root&password=123456789&allowPublicKeyRetrieval=true&useSSL=false";

        try {
            // Load MySQL driver (updated for MySQL 8.x)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
//            con = DriverManager.getConnection(jdbcURL, JDBC_USER, JDBC_PASSWORD);
            con = DriverManager.getConnection(url);

            System.out.println("Connection successful!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection to database failed");
        }
    }
}
