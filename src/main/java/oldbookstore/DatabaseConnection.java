package oldbookstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the SQLite database connection and table initialization.
 * The database file is stored in a "database" folder next to the application.
 */
public class DatabaseConnection {

    // SQLite database file path
    private static String DB_URL = "jdbc:sqlite:database/oldbookstore.db";

    /** Allows changing the database URL (used for testing). */
    public static void setDatabaseUrl(String url) {
        DB_URL = url;
    }

    // ==================== GET CONNECTION ====================

    /**
     * Returns a connection to the SQLite database.
     * Foreign-key enforcement is enabled on every connection.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        // Enable foreign-key enforcement for this connection
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    // ==================== DATABASE INITIALIZATION ====================

    /**
     * Creates the database directory and all required tables if they do not exist.
     * Called once when the application starts.
     */
    public static void initializeDatabase() {
        // Create the database directory if it doesn't exist
        java.io.File dbDir = new java.io.File("database");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create books table
            String createBooksTable = "CREATE TABLE IF NOT EXISTS books ("
                    + "book_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "title TEXT NOT NULL, "
                    + "author TEXT NOT NULL, "
                    + "category TEXT NOT NULL, "
                    + "isbn TEXT, "
                    + "buying_price REAL NOT NULL, "
                    + "selling_price REAL NOT NULL, "
                    + "stock INTEGER NOT NULL DEFAULT 0"
                    + ")";
            stmt.execute(createBooksTable);

            // Create purchases table with foreign key referencing books
            String createPurchasesTable = "CREATE TABLE IF NOT EXISTS purchases ("
                    + "purchase_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "book_id INTEGER NOT NULL, "
                    + "quantity INTEGER NOT NULL, "
                    + "bought_from TEXT NOT NULL, "
                    + "buying_price REAL NOT NULL, "
                    + "total_amount REAL NOT NULL, "
                    + "purchase_date TEXT NOT NULL, "
                    + "FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE RESTRICT"
                    + ")";
            stmt.execute(createPurchasesTable);

            // Create sales table with foreign key referencing books
            String createSalesTable = "CREATE TABLE IF NOT EXISTS sales ("
                    + "sale_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "book_id INTEGER NOT NULL, "
                    + "quantity INTEGER NOT NULL, "
                    + "sold_to TEXT NOT NULL, "
                    + "selling_price REAL NOT NULL, "
                    + "total_amount REAL NOT NULL, "
                    + "profit REAL NOT NULL, "
                    + "sale_date TEXT NOT NULL, "
                    + "FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE RESTRICT"
                    + ")";
            stmt.execute(createSalesTable);

            // Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "username TEXT UNIQUE NOT NULL, "
                    + "password_hash TEXT NOT NULL, "
                    + "salt TEXT NOT NULL, "
                    + "created_at TEXT NOT NULL"
                    + ")";
            stmt.execute(createUsersTable);

            // Ensure default admin user exists (username: Admin, password: admin)
            boolean adminExists = false;
            try (ResultSet rs = stmt.executeQuery("SELECT 1 FROM users WHERE LOWER(username) = 'admin'")) {
                if (rs.next()) {
                    adminExists = true;
                }
            }
            if (!adminExists) {
                String salt = PasswordUtil.generateSalt();
                String hash = PasswordUtil.hashPassword("admin", salt);
                String createdAt = java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String insertSql = "INSERT INTO users (username, password_hash, salt, created_at) "
                        + "VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, "Admin");
                    pstmt.setString(2, hash);
                    pstmt.setString(3, salt);
                    pstmt.setString(4, createdAt);
                    pstmt.executeUpdate();
                }
                System.out.println("Default admin user created (Admin / admin).");
            }

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
