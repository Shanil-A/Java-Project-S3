package oldbookstore;

import java.sql.Connection;
import java.sql.DriverManager;
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
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
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

            // Create purchases table
            String createPurchasesTable = "CREATE TABLE IF NOT EXISTS purchases ("
                    + "purchase_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "book_id INTEGER NOT NULL, "
                    + "quantity INTEGER NOT NULL, "
                    + "bought_from TEXT NOT NULL, "
                    + "buying_price REAL NOT NULL, "
                    + "total_amount REAL NOT NULL, "
                    + "purchase_date TEXT NOT NULL, "
                    + "FOREIGN KEY (book_id) REFERENCES books(book_id)"
                    + ")";
            stmt.execute(createPurchasesTable);

            // Create sales table
            String createSalesTable = "CREATE TABLE IF NOT EXISTS sales ("
                    + "sale_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "book_id INTEGER NOT NULL, "
                    + "quantity INTEGER NOT NULL, "
                    + "sold_to TEXT NOT NULL, "
                    + "selling_price REAL NOT NULL, "
                    + "total_amount REAL NOT NULL, "
                    + "profit REAL NOT NULL, "
                    + "sale_date TEXT NOT NULL, "
                    + "FOREIGN KEY (book_id) REFERENCES books(book_id)"
                    + ")";
            stmt.execute(createSalesTable);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
