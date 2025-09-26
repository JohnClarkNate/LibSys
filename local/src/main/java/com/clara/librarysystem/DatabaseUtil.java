package com.clara.librarysystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";  // Replace with your actual database name
    private static final String DB_USER = "root";  // Default MySQL username in XAMPP is 'root'
    private static final String DB_PASSWORD = "";  // Default password in XAMPP is empty

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
