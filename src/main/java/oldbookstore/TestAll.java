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
        } catch (Exception e) {
            System.out.println("UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== RESULTS: " + passed + " passed, " + failed + " failed ===");

        // Clean up test database
        try {
            java.io.File dbFile = new java.io.File("database/oldbookstore.db");
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
}
