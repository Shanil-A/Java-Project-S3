package oldbookstore;

/**
 * Represents a single book in the bookstore.
 * Demonstrates encapsulation with private fields, getters, and setters.
 */
public class Book {

    private int bookId;
    private String title;
    private String author;
    private String category;
    private String isbn;
    private double buyingPrice;
    private double sellingPrice;
    private int stock;

    // ==================== CONSTRUCTORS ====================

    /** Default constructor */
    public Book() {
    }

    /** Constructor for adding a new book (no ID or stock needed) */
    public Book(String title, String author, String category, String isbn,
                double buyingPrice, double sellingPrice) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.stock = 0;
    }

    /** Full constructor (used when reading from database) */
    public Book(int bookId, String title, String author, String category, String isbn,
                double buyingPrice, double sellingPrice, int stock) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.stock = stock;
    }

    // ==================== GETTERS AND SETTERS ====================

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public double getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    /** Returns book info for display in combo boxes */
    public String toString() {
        return title + " (Stock: " + stock + ")";
    }
}
