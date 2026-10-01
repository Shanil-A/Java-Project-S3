package oldbookstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Sale-related database operations.
 * Handles recording book sales, stock checks, and profit calculation.
 */
public class SaleDAO {

    // ==================== ADD SALE ====================

    /**
     * Records a sale and decreases the book's stock.
     * Uses a transaction to ensure both operations succeed together.
     */
    public void addSale(Sale sale) throws SQLException {
        String insertSql = "INSERT INTO sales (book_id, quantity, sold_to, selling_price, total_amount, profit, sale_date) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        String updateStockSql = "UPDATE books SET stock = stock - ? WHERE book_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        try {
            // Use a transaction so both operations succeed or fail together
            conn.setAutoCommit(false);

            // Step 1: Insert the sale record
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, sale.getBookId());
            insertStmt.setInt(2, sale.getQuantity());
            insertStmt.setString(3, sale.getSoldTo());
            insertStmt.setDouble(4, sale.getSellingPrice());
            insertStmt.setDouble(5, sale.getTotalAmount());
            insertStmt.setDouble(6, sale.getProfit());
            insertStmt.setString(7, sale.getSaleDate());
            insertStmt.executeUpdate();
            insertStmt.close();

            // Step 2: Decrease the book's stock
            PreparedStatement updateStmt = conn.prepareStatement(updateStockSql);
            updateStmt.setInt(1, sale.getQuantity());
            updateStmt.setInt(2, sale.getBookId());
            updateStmt.executeUpdate();
            updateStmt.close();

            // Commit both changes
            conn.commit();

        } catch (SQLException e) {
            // If something goes wrong, undo both changes
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    // ==================== STOCK CHECK ====================

    /** Returns the current stock of a book. */
    public int getBookStock(int bookId) throws SQLException {
        String sql = "SELECT stock FROM books WHERE book_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("stock");
            }

            rs.close();
        }

        return 0;
    }

    // ==================== SALES HISTORY ====================

    /** Returns all sale records with book titles. */
    public List<Object[]> getSaleHistory() throws SQLException {
        String sql = "SELECT s.sale_id, b.title, s.quantity, s.sold_to, "
                   + "s.selling_price, s.total_amount, s.profit, s.sale_date "
                   + "FROM sales s JOIN books b ON s.book_id = b.book_id "
                   + "ORDER BY s.sale_id DESC";

        List<Object[]> history = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("sale_id"),
                    rs.getString("title"),
                    rs.getInt("quantity"),
                    rs.getString("sold_to"),
                    rs.getDouble("selling_price"),
                    rs.getDouble("total_amount"),
                    rs.getDouble("profit"),
                    rs.getString("sale_date")
                };
                history.add(row);
            }
        }

        return history;
    }

    // ==================== DASHBOARD QUERIES ====================

    /** Returns the total amount from all sales. */
    public double getTotalSalesAmount() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total FROM sales";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        }

        return 0;
    }

    /** Returns the total profit from all sales. */
    public double getTotalProfit() throws SQLException {
        String sql = "SELECT COALESCE(SUM(profit), 0) AS total FROM sales";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        }

        return 0;
    }
}
