package com.portfolio.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages SQLite database connection.
 * Demonstrates: JDBC API, Singleton pattern, Database connection (Unit 5)
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:portfolio.db";
    private static DatabaseManager instance;
    private Connection connection;

    // Singleton - only one DB connection at a time
    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("[DB] Connected to SQLite database: portfolio.db");
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("[DB ERROR] SQLite JDBC driver not found: " + e.getMessage());
            System.err.println("Make sure sqlite-jdbc.jar is in the lib/ folder.");
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to connect: " + e.getMessage());
            System.exit(1);
        }
    }

    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        // Assets table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS assets (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                symbol TEXT NOT NULL,
                name TEXT NOT NULL,
                asset_type TEXT NOT NULL,
                quantity REAL NOT NULL,
                buy_price REAL NOT NULL,
                buy_date TEXT NOT NULL,
                currency TEXT DEFAULT 'INR',
                extra1 TEXT,
                extra2 TEXT,
                extra3 TEXT
            )
        """);

        // Price history table for tracking current prices
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS price_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                asset_id INTEGER NOT NULL,
                price REAL NOT NULL,
                recorded_date TEXT NOT NULL,
                FOREIGN KEY (asset_id) REFERENCES assets(id)
            )
        """);

        stmt.close();
        System.out.println("[DB] Tables initialized successfully.");
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Connection lost: " + e.getMessage());
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to close connection: " + e.getMessage());
        }
    }
}
