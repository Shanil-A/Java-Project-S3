package oldbookstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Purchase-related database operations.
 * Handles recording book purchases and updating stock.
 */
public class PurchaseDAO {

    // ==================== ADD PURCHASE ====================

    /**
     * Records a purchase and increases the book's stock.
     * Uses a transaction to ensure both operations succeed together.
     */
    public void addPurchase(Purchase purchase) throws SQLException {
        String insertSql = "INSERT INTO purchases (book_id, quantity, bought_from, buying_price, total_amount, purchase_date) "
                         + "VALUES (?, ?, ?, ?, ?, ?)";

        String updateStockSql = "UPDATE books SET stock = stock + ? WHERE book_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        try {
            // Use a transaction so both operations succeed or fail together
            conn.setAutoCommit(false);

            // Step 1: Insert the purchase record
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, purchase.getBookId());
            insertStmt.setInt(2, purchase.getQuantity());
            insertStmt.setString(3, purchase.getBoughtFrom());
            insertStmt.setDouble(4, purchase.getBuyingPrice());
            insertStmt.setDouble(5, purchase.getTotalAmount());
            insertStmt.setString(6, purchase.getPurchaseDate());
            insertStmt.executeUpdate();
            insertStmt.close();

            // Step 2: Increase the book's stock
            PreparedStatement updateStmt = conn.prepareStatement(updateStockSql);
            updateStmt.setInt(1, purchase.getQuantity());
            updateStmt.setInt(2, purchase.getBookId());
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

    // ==================== PURCHASE HISTORY ====================

    /** Returns all purchase records with book titles. */
    public List<Object[]> getPurchaseHistory() throws SQLException {
        String sql = "SELECT p.purchase_id, b.title, p.quantity, p.bought_from, "
                   + "p.buying_price, p.total_amount, p.purchase_date "
                   + "FROM purchases p JOIN books b ON p.book_id = b.book_id "
                   + "ORDER BY p.purchase_id DESC";

        List<Object[]> history = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("purchase_id"),
                    rs.getString("title"),
                    rs.getInt("quantity"),
                    rs.getString("bought_from"),
                    rs.getDouble("buying_price"),
                    rs.getDouble("total_amount"),
                    rs.getString("purchase_date")
                };
                history.add(row);
            }
        }

        return history;
    }

    // ==================== DASHBOARD QUERY ====================

    /** Returns the total amount spent on all purchases. */
    public double getTotalPurchaseAmount() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total FROM purchases";

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
