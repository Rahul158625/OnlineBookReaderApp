package com.bookreader;

import java.sql.*;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/bookreader", "root", "Rahul@1586");
    }


    }



