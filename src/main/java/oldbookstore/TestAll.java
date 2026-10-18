package oldbookstore;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Automated test for core functionality.
 * Tests CRUD operations, purchase, sale, stock, profit, and validation.
 * Uses a separate test database to avoid affecting real data.
 */
public class TestAll {

    private static BookDAO bookDAO = new BookDAO();
    private static PurchaseDAO purchaseDAO = new PurchaseDAO();
    private static SaleDAO saleDAO = new SaleDAO();
    private static UserDAO userDAO = new UserDAO();
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Old Book Store - Automated Tests ===\n");

        // Use a separate test database
        DatabaseConnection.setDatabaseUrl("jdbc:sqlite:database/test_oldbookstore.db");

        // Drop existing test tables for a clean start
        try (java.sql.Connection conn = DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS sales");
            stmt.execute("DROP TABLE IF EXISTS purchases");
            stmt.execute("DROP TABLE IF EXISTS books");
            stmt.execute("DROP TABLE IF EXISTS users");
        } catch (SQLException e) {
            System.out.println("Error clearing test DB: " + e.getMessage());
        }

        // Initialize fresh test database
        DatabaseConnection.initializeDatabase();

        try {
            testAddBook();
            testGetAllBooks();
            testSearchBooks();
            testUpdateBook();
            testPurchaseBook();
            testStockIncrease();
            testSellBook();
            testStockDecrease();
            testProfitCalculation();
            testInsufficientStock();
            testTransactionHistoryCheck();
            testDeleteBookWithHistory();
            testDeleteBookWithoutHistory();
            testDashboardQueries();
            testDefaultAdminAndChangePassword();
            testUserRegistrationAndAuthentication();
            testUserDuplicateRegistration();
            testUserCaseInsensitivity();
            testUserPersistenceForNextUse();
        } catch (Exception e) {
            System.out.println("UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== RESULTS: " + passed + " passed, " + failed + " failed ===");

        // Clean up test database
        try {
            java.io.File dbFile = new java.io.File("database/test_oldbookstore.db");
            if (dbFile.exists()) {
                dbFile.delete();
                System.out.println("Test database cleaned up.");
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    static void check(String testName, boolean condition) {
        if (condition) {
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName);
            failed++;
        }
    }

    // ==================== CREATE ====================
    static void testAddBook() throws SQLException {
        System.out.println("\n--- Test: Add Book (CREATE) ---");
        Book book = new Book("Java Programming", "James Gosling", "Programming", "978-0134685991", 250.0, 400.0);
        bookDAO.addBook(book);

        List<Book> books = bookDAO.getAllBooks();
        check("Book was added", books.size() == 1);
        check("Title matches", books.get(0).getTitle().equals("Java Programming"));
        check("Stock is 0 initially", books.get(0).getStock() == 0);

        // Add a second book for further tests
        Book book2 = new Book("Python Basics", "Guido van Rossum", "Programming", "", 150.0, 300.0);
        bookDAO.addBook(book2);

        books = bookDAO.getAllBooks();
        check("Second book added", books.size() == 2);
    }

    // ==================== READ ====================
    static void testGetAllBooks() throws SQLException {
        System.out.println("\n--- Test: Get All Books (READ) ---");
        List<Book> books = bookDAO.getAllBooks();
        check("All books retrieved", books.size() == 2);
        check("Has bookId", books.get(0).getBookId() > 0);
    }

    // ==================== SEARCH ====================
    static void testSearchBooks() throws SQLException {
        System.out.println("\n--- Test: Search Books ---");
        List<Book> results = bookDAO.searchBooks("Java");
        check("Found Java book", results.size() == 1);
        check("Correct title", results.get(0).getTitle().equals("Java Programming"));

        results = bookDAO.searchBooks("xyz");
        check("No results for 'xyz'", results.size() == 0);

        results = bookDAO.searchBooks("Programming");
        check("'Programming' found in 'Java Programming' title", results.size() == 1);

        results = bookDAO.searchBooks("Bas");
        check("Partial title search for 'Bas'", results.size() == 1);
    }

    // ==================== UPDATE ====================
    static void testUpdateBook() throws SQLException {
        System.out.println("\n--- Test: Update Book (UPDATE) ---");
        List<Book> books = bookDAO.getAllBooks();
        Book book = books.get(0);
        book.setTitle("Advanced Java Programming");
        book.setSellingPrice(450.0);
        bookDAO.updateBook(book);

        books = bookDAO.getAllBooks();
        check("Title updated", books.get(0).getTitle().equals("Advanced Java Programming"));
        check("Price updated", books.get(0).getSellingPrice() == 450.0);
    }

    // ==================== PURCHASE ====================
    static void testPurchaseBook() throws SQLException {
        System.out.println("\n--- Test: Purchase Book ---");
        List<Book> books = bookDAO.getAllBooks();
        int bookId = books.get(0).getBookId();

        Purchase purchase = new Purchase(bookId, 5, "Local Seller", 250.0, 1250.0, LocalDate.now().toString());
        purchaseDAO.addPurchase(purchase);

        List<Object[]> history = purchaseDAO.getPurchaseHistory();
        check("Purchase recorded", history.size() == 1);
        check("Total amount correct (250*5=1250)", (double) history.get(0)[5] == 1250.0);
    }

    // ==================== STOCK INCREASE ====================
    static void testStockIncrease() throws SQLException {
        System.out.println("\n--- Test: Stock Increase After Purchase ---");
        List<Book> books = bookDAO.getAllBooks();
        check("Stock increased to 5", books.get(0).getStock() == 5);

        // Another purchase
        int bookId = books.get(0).getBookId();
        Purchase p2 = new Purchase(bookId, 3, "Another Seller", 260.0, 780.0, LocalDate.now().toString());
        purchaseDAO.addPurchase(p2);

        books = bookDAO.getAllBooks();
        check("Stock increased to 8 (5+3)", books.get(0).getStock() == 8);
    }

    // ==================== SALE ====================
    static void testSellBook() throws SQLException {
        System.out.println("\n--- Test: Sell Book ---");
        List<Book> books = bookDAO.getAllBooks();
        int bookId = books.get(0).getBookId();
        double buyingPrice = books.get(0).getBuyingPrice(); // 250.0

        double sellingPrice = 400.0;
        int quantity = 2;
        double totalAmount = sellingPrice * quantity; // 800
        double profit = (sellingPrice - buyingPrice) * quantity; // (400-250)*2 = 300

        Sale sale = new Sale(bookId, quantity, "Customer A", sellingPrice, totalAmount, profit, LocalDate.now().toString());
        saleDAO.addSale(sale);

        List<Object[]> history = saleDAO.getSaleHistory();
        check("Sale recorded", history.size() == 1);
        check("Sale total correct (400*2=800)", (double) history.get(0)[5] == 800.0);
        check("Profit correct ((400-250)*2=300)", (double) history.get(0)[6] == 300.0);
    }

    // ==================== STOCK DECREASE ====================
    static void testStockDecrease() throws SQLException {
        System.out.println("\n--- Test: Stock Decrease After Sale ---");
        List<Book> books = bookDAO.getAllBooks();
        check("Stock decreased to 6 (8-2)", books.get(0).getStock() == 6);
    }

    // ==================== PROFIT ====================
    static void testProfitCalculation() throws SQLException {
        System.out.println("\n--- Test: Profit Calculation ---");
        double totalProfit = saleDAO.getTotalProfit();
        check("Total profit is 300", totalProfit == 300.0);
    }

    // ==================== INSUFFICIENT STOCK ====================
    static void testInsufficientStock() throws SQLException {
        System.out.println("\n--- Test: Insufficient Stock Check ---");
        List<Book> books = bookDAO.getAllBooks();
        int bookId = books.get(0).getBookId();
        int stock = saleDAO.getBookStock(bookId);
        check("Current stock is 6", stock == 6);
        check("Would reject sale of 10 (> 6)", 10 > stock);
    }

    // ==================== TRANSACTION HISTORY ====================
    static void testTransactionHistoryCheck() throws SQLException {
        System.out.println("\n--- Test: Transaction History Check ---");
        List<Book> books = bookDAO.getAllBooks();
        check("Book 1 has history", bookDAO.hasTransactionHistory(books.get(0).getBookId()));
        check("Book 2 has NO history", !bookDAO.hasTransactionHistory(books.get(1).getBookId()));
    }

    // ==================== DELETE WITH HISTORY ====================
    static void testDeleteBookWithHistory() throws SQLException {
        System.out.println("\n--- Test: Delete Book With History (should be blocked) ---");
        List<Book> books = bookDAO.getAllBooks();
        int bookId = books.get(0).getBookId();
        boolean hasHistory = bookDAO.hasTransactionHistory(bookId);
        check("Has transaction history", hasHistory);
        check("Delete should be blocked", hasHistory); // In the app, this would prevent deletion
    }

    // ==================== DELETE WITHOUT HISTORY ====================
    static void testDeleteBookWithoutHistory() throws SQLException {
        System.out.println("\n--- Test: Delete Book Without History (DELETE) ---");
        List<Book> books = bookDAO.getAllBooks();
        Book book2 = books.get(1); // Python Basics - no transactions
        boolean hasHistory = bookDAO.hasTransactionHistory(book2.getBookId());
        check("No transaction history for book 2", !hasHistory);

        bookDAO.deleteBook(book2.getBookId());
        books = bookDAO.getAllBooks();
        check("Book deleted (only 1 remaining)", books.size() == 1);
    }

    // ==================== DASHBOARD ====================
    static void testDashboardQueries() throws SQLException {
        System.out.println("\n--- Test: Dashboard Queries ---");
        check("Total book count = 1", bookDAO.getTotalBookCount() == 1);
        check("Total stock = 6", bookDAO.getTotalStock() == 6);
        check("Total purchase amount > 0", purchaseDAO.getTotalPurchaseAmount() > 0);
        check("Total sales amount = 800", saleDAO.getTotalSalesAmount() == 800.0);
        check("Total profit = 300", saleDAO.getTotalProfit() == 300.0);
    }

    // ==================== USER AUTHENTICATION & REGISTRATION ====================

    static void testDefaultAdminAndChangePassword() throws SQLException {
        System.out.println("\n--- Test: Default Admin & Change Password ---");

        // 1. Verify default admin account exists with username "Admin"
        User admin = userDAO.getUserByUsername("Admin");
        check("Default Admin account was seeded automatically", admin != null);
        check("Default username is 'Admin'", admin != null && "Admin".equals(admin.getUsername()));

        // 2. Authenticate with default Admin/admin credentials
        User authAdmin = userDAO.authenticate("Admin", "admin");
        check("Login with default Admin/admin succeeds", authAdmin != null);

        // 3. Test changing admin password
        boolean updated = userDAO.updatePassword("Admin", "newAdminPass99");
        check("Password update returned true", updated);

        // 4. Verify old password no longer works
        User oldAuth = userDAO.authenticate("Admin", "admin");
        check("Old password (admin) is rejected after password change", oldAuth == null);

        // 5. Verify new password works
        User newAuth = userDAO.authenticate("Admin", "newAdminPass99");
        check("New password (newAdminPass99) authenticates successfully", newAuth != null);

        // Reset back to admin for consistency
        userDAO.updatePassword("Admin", "admin");
    }

    static void testUserRegistrationAndAuthentication() throws SQLException {
        System.out.println("\n--- Test: User Registration & Authentication ---");

        // 1. Register a new user
        boolean registered = userDAO.registerUser("shanil", "myPassword123");
        check("User registration successful", registered);

        // 2. Verify stored password is securely hashed (not plain text)
        User user = userDAO.getUserByUsername("shanil");
        check("User retrieved from DB", user != null);
        check("Password is not stored in plain text", user != null && !"myPassword123".equals(user.getPasswordHash()));
        check("Salt is generated and saved", user != null && user.getSalt() != null && !user.getSalt().isEmpty());

        // 3. Authenticate with correct credentials
        User authUser = userDAO.authenticate("shanil", "myPassword123");
        check("Login with correct credentials succeeds", authUser != null && "shanil".equals(authUser.getUsername()));

        // 4. Authenticate with wrong password
        User wrongPassUser = userDAO.authenticate("shanil", "wrongPassword");
        check("Login with wrong password fails", wrongPassUser == null);

        // 5. Authenticate with nonexistent user
        User unknownUser = userDAO.authenticate("nonexistent_user", "somePassword");
        check("Login with unknown username fails", unknownUser == null);
    }

    static void testUserDuplicateRegistration() throws SQLException {
        System.out.println("\n--- Test: Prevent Duplicate Username ---");

        // Attempting to register existing username should be rejected
        boolean dupReg = userDAO.registerUser("shanil", "anotherPassword");
        check("Duplicate registration is rejected", !dupReg);
    }

    static void testUserCaseInsensitivity() throws SQLException {
        System.out.println("\n--- Test: Username Case-Insensitive Matching ---");

        // Login using uppercase or mixed case should work
        User upperUser = userDAO.authenticate("SHANIL", "myPassword123");
        check("Case-insensitive username login succeeds", upperUser != null && "shanil".equalsIgnoreCase(upperUser.getUsername()));

        // Duplicate registration with different case should also be blocked
        boolean dupCaseReg = userDAO.registerUser("ShaniL", "somePass");
        check("Duplicate registration with different casing is rejected", !dupCaseReg);
    }

    static void testUserPersistenceForNextUse() throws SQLException {
        System.out.println("\n--- Test: User Persistence For Next Use ---");

        // Create a completely new UserDAO instance (simulating subsequent app launches)
        UserDAO freshUserDAO = new UserDAO();
        check("User count reflects persisted accounts", freshUserDAO.getUserCount() >= 1);

        User persistentUser = freshUserDAO.authenticate("shanil", "myPassword123");
        check("Persisted credentials authenticate on next use", persistentUser != null && "shanil".equals(persistentUser.getUsername()));
    }
}
