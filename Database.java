import java.sql.*;

/**
 * Database.java
 * Created by Arvind Kumar S
 * Handles SQLite database connection and table setup for Product & Buyer Management.
 */
public class Database {

    private static final String DB_URL = "jdbc:sqlite:inventory.db";

    // Static block to auto-initialize the database and tables on first use
    static {
        initialize();
    }

    /**
     * Initialize the database schema (products, buyers).
     */
    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Create products table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS products (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        category TEXT,
                        price REAL NOT NULL,
                        quantity INTEGER NOT NULL,
                        description TEXT
                    )
                    """);

            // Create buyers table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS buyers (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        email TEXT,
                        phone TEXT,
                        address
