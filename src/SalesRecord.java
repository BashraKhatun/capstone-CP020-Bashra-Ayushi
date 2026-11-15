
import java.time.LocalDate;

public class SalesRecord {

    // Private attributes
    private String productName;
    private String category;
    private int quantity;
    private double unitPrice;
    private LocalDate saleDate;

    // Constructor
    public SalesRecord(String productName, String category,
                       int quantity, double unitPrice,
                       LocalDate saleDate) {

        setProductName(productName);
        setCategory(category);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
        setSaleDate(saleDate);
    }

    // Calculate total revenue
    public double getRevenue() {
        return quantity * unitPrice;
    }

    // Getters
    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    // Setters with validation
    public void setProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        this.productName = productName.trim();
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        this.category = category.trim();
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        if (!Double.isFinite(unitPrice) || unitPrice <= 0) {
            throw new IllegalArgumentException("Unit price must be a positive finite number.");
        }
        this.unitPrice = unitPrice;
    }

    public void setSaleDate(LocalDate saleDate) {
        if (saleDate == null) {
            throw new IllegalArgumentException("Sale date cannot be empty.");
        }
        this.saleDate = saleDate;
    }
}
