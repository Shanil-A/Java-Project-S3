package oldbookstore;

/**
 * Represents a single sale transaction (selling books to a buyer).
 */
public class Sale {

    private int saleId;
    private int bookId;
    private int quantity;
    private String soldTo;
    private double sellingPrice;
    private double totalAmount;
    private double profit;
    private String saleDate;

    // ==================== CONSTRUCTORS ====================

    /** Default constructor */
    public Sale() {
    }

    /** Constructor for creating a new sale */
    public Sale(int bookId, int quantity, String soldTo, double sellingPrice,
                double totalAmount, double profit, String saleDate) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.soldTo = soldTo;
        this.sellingPrice = sellingPrice;
        this.totalAmount = totalAmount;
        this.profit = profit;
        this.saleDate = saleDate;
    }

    /** Full constructor (used when reading from database) */
    public Sale(int saleId, int bookId, int quantity, String soldTo, double sellingPrice,
                double totalAmount, double profit, String saleDate) {
        this.saleId = saleId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.soldTo = soldTo;
        this.sellingPrice = sellingPrice;
        this.totalAmount = totalAmount;
        this.profit = profit;
        this.saleDate = saleDate;
    }

    // ==================== GETTERS AND SETTERS ====================

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
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

    public String getSoldTo() {
        return soldTo;
    }

    public void setSoldTo(String soldTo) {
        this.soldTo = soldTo;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }
}
