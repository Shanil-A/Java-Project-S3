package oldbookstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Book-related database operations.
 * Handles CREATE, READ, UPDATE, DELETE for books.
 */
public class BookDAO {

    // ==================== CREATE ====================

    /** Inserts a new book into the database. Stock starts at 0. */
    public void addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, category, isbn, buying_price, selling_price, stock) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 0)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getCategory());
            pstmt.setString(4, book.getIsbn());
            pstmt.setDouble(5, book.getBuyingPrice());
            pstmt.setDouble(6, book.getSellingPrice());

            pstmt.executeUpdate();
        }
    }

    // ==================== READ ====================

    /** Returns a list of all books from the database. */
    public List<Book> getAllBooks() throws SQLException {
        String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category"),
                    rs.getString("isbn"),
                    rs.getDouble("buying_price"),
                    rs.getDouble("selling_price"),
                    rs.getInt("stock")
                );
                books.add(book);
            }
        }

        return books;
    }

    /** Searches books by title using LIKE query. */
    public List<Book> searchBooks(String title) throws SQLException {
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        List<Book> books = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category"),
                    rs.getString("isbn"),
                    rs.getDouble("buying_price"),
                    rs.getDouble("selling_price"),
                    rs.getInt("stock")
                );
                books.add(book);
            }

            rs.close();
        }

        return books;
    }

    // ==================== UPDATE ====================

    /** Updates an existing book's information (not stock). */
    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, category = ?, isbn = ?, "
                   + "buying_price = ?, selling_price = ? WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getCategory());
            pstmt.setString(4, book.getIsbn());
            pstmt.setDouble(5, book.getBuyingPrice());
            pstmt.setDouble(6, book.getSellingPrice());
            pstmt.setInt(7, book.getBookId());

            pstmt.executeUpdate();
        }
    }

    // ==================== DELETE ====================

    /** Deletes a book if it has no purchase or sales history. */
    public void deleteBook(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        }
    }

    // ==================== TRANSACTION HISTORY CHECK ====================

    /** Checks if a book has any purchase or sale records. */
    public boolean hasTransactionHistory(int bookId) throws SQLException {
        String sql = "SELECT "
                   + "(SELECT COUNT(*) FROM purchases WHERE book_id = ?) + "
                   + "(SELECT COUNT(*) FROM sales WHERE book_id = ?) AS total";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            pstmt.setInt(2, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

            rs.close();
        }

        return false;
    }

    // ==================== DASHBOARD QUERIES ====================

    /** Returns the total number of different books. */
    public int getTotalBookCount() throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM books";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        }

        return 0;
    }

    /** Returns the total stock across all books. */
    public int getTotalStock() throws SQLException {
        String sql = "SELECT COALESCE(SUM(stock), 0) AS total FROM books";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        }

        return 0;
    }
}
