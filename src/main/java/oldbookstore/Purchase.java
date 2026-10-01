package oldbookstore;

/**
 * Represents a single purchase transaction (buying books from a seller).
 */
public class Purchase {

    private int purchaseId;
    private int bookId;
    private int quantity;
    private String boughtFrom;
    private double buyingPrice;
    private double totalAmount;
    private String purchaseDate;

    // ==================== CONSTRUCTORS ====================

    /** Default constructor */
    public Purchase() {
    }

    /** Constructor for creating a new purchase */
    public Purchase(int bookId, int quantity, String boughtFrom,
                    double buyingPrice, double totalAmount, String purchaseDate) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.boughtFrom = boughtFrom;
        this.buyingPrice = buyingPrice;
        this.totalAmount = totalAmount;
        this.purchaseDate = purchaseDate;
    }

    /** Full constructor (used when reading from database) */
    public Purchase(int purchaseId, int bookId, int quantity, String boughtFrom,
                    double buyingPrice, double totalAmount, String purchaseDate) {
        this.purchaseId = purchaseId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.boughtFrom = boughtFrom;
        this.buyingPrice = buyingPrice;
        this.totalAmount = totalAmount;
        this.purchaseDate = purchaseDate;
    }

    // ==================== GETTERS AND SETTERS ====================

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBoughtFrom() {
        return boughtFrom;
    }

    public void setBoughtFrom(String boughtFrom) {
        this.boughtFrom = boughtFrom;
    }

    public double getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
