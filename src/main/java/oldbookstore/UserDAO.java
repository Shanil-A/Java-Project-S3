package oldbookstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object for user authentication and account management.
 * Handles user registration, credentials verification, and persistence.
 */
public class UserDAO {

    /**
     * Registers a new user with a hashed password and salt.
     *
     * @param username the desired username
     * @param password the plain text password to hash and store
     * @return true if registration succeeded, false if the username already exists
     * @throws SQLException if a database error occurs
     */
    public boolean registerUser(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        username = username.trim();

        if (usernameExists(username)) {
            return false;
        }

        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(password, salt);
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String sql = "INSERT INTO users (username, password_hash, salt, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, salt);
            pstmt.setString(4, createdAt);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Authenticates a user with the given username and password.
     *
     * @param username the username to check
     * @param password the plain text password to verify
     * @return the authenticated User object if credentials are valid, or null otherwise
     * @throws SQLException if a database error occurs
     */
    public User authenticate(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty() || password == null) {
            return null;
        }

        username = username.trim();
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String salt = rs.getString("salt");
                    String storedHash = rs.getString("password_hash");

                    if (PasswordUtil.verifyPassword(password, salt, storedHash)) {
                        return new User(
                                rs.getInt("user_id"),
                                rs.getString("username"),
                                storedHash,
                                salt,
                                rs.getString("created_at")
                        );
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks whether a username is already taken (case-insensitive).
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean usernameExists(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Returns the total count of registered users.
     *
     * @return total registered users count
     * @throws SQLException if a database error occurs
     */
    public int getUserCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Retrieves a user by their username (case-insensitive).
     *
     * @param username the username to search for
     * @return User object if found, or null
     * @throws SQLException if a database error occurs
     */
    public User getUserByUsername(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("salt"),
                            rs.getString("created_at")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Seeds the default admin account with username "Admin" and password "admin"
     * if it does not already exist.
     *
     * @return true if seeded, false if admin already exists
     * @throws SQLException if a database error occurs
     */
    public boolean seedDefaultAdmin() throws SQLException {
        if (!usernameExists("admin")) {
            return registerUser("Admin", "admin");
        }
        return false;
    }

    /**
     * Updates the password for a user.
     *
     * @param username the username whose password to update
     * @param newPassword the new plain text password
     * @return true if updated successfully
     * @throws SQLException if a database error occurs
     */
    public boolean updatePassword(String username, String newPassword) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty.");
        }

        String salt = PasswordUtil.generateSalt();
        String newHash = PasswordUtil.hashPassword(newPassword, salt);

        String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newHash);
            pstmt.setString(2, salt);
            pstmt.setString(3, username.trim());

            return pstmt.executeUpdate() > 0;
        }
    }
}
