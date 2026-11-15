
import java.util.ArrayList;
import java.util.List;

public class SalesManager {

    // Store multiple sales records
    private final ArrayList<SalesRecord> salesRecords;

    // Constructor
    public SalesManager() {
        salesRecords = new ArrayList<>();
    }

    // Add a new sales record
    public void addSalesRecord(SalesRecord record) {

        if (record == null) {
            throw new IllegalArgumentException("Sales record cannot be null.");
        }

        salesRecords.add(record);

    }

    // Display all sales records
    public void displayAllRecords() {

        if (salesRecords.isEmpty()) {
            System.out.println("No sales records available.");
            return;
        }

        System.out.println("\n===== ALL SALES RECORDS =====");

        for (int i = 0; i < salesRecords.size(); i++) {

            SalesRecord record = salesRecords.get(i);

            System.out.println("\nRecord " + (i + 1));
            System.out.println("Product: " + record.getProductName());
            System.out.println("Category: " + record.getCategory());
            System.out.println("Quantity: " + record.getQuantity());
            System.out.println("Unit Price: Rs. " + record.getUnitPrice());
            System.out.println("Sale Date: " + record.getSaleDate());
            System.out.println("Revenue: Rs. " + record.getRevenue());
        }
    }

    // Search sales records by product name
    public void searchProduct(String productName) {

        boolean found = false;

        if (productName == null || productName.trim().isEmpty()) {
            System.out.println("Please enter a valid product name.");
            return;
        }

        for (SalesRecord record : salesRecords) {

            if (record.getProductName().equalsIgnoreCase(productName.trim())) {

                System.out.println("\nProduct Found!");
                System.out.println("Product: " + record.getProductName());
                System.out.println("Category: " + record.getCategory());
                System.out.println("Quantity: " + record.getQuantity());
                System.out.println("Revenue: Rs. " + record.getRevenue());

                found = true;
            }
        }

        if (!found) {
            System.out.println("Product not found.");
        }
    }

    // Get total number of sales records
    public int getTotalRecords() {
        return salesRecords.size();
    }

    // Provide sales records for analysis
    public List<SalesRecord> getSalesRecords() {
        return new ArrayList<>(salesRecords);
    }
}
