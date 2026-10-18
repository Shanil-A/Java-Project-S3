package oldbookstore;

/**
 * Represents a user in the application.
 * Encapsulates user credentials and metadata.
 */
public class User {

    private int userId;
    private String username;
    private String passwordHash;
    private String salt;
    private String createdAt;

    // ==================== CONSTRUCTORS ====================

    /** Default constructor */
    public User() {
    }

    /** Constructor for user creation */
    public User(String username, String passwordHash, String salt, String createdAt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.createdAt = createdAt;
    }

    /** Full constructor (when retrieved from database) */
    public User(int userId, String username, String passwordHash, String salt, String createdAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.createdAt = createdAt;
    }

    // ==================== GETTERS AND SETTERS ====================

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return username;
    }
}
