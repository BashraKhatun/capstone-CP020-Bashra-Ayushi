
import java.util.Scanner;
import java.time.LocalDate;

public class Menu {

    private final Scanner scanner;
    private final SalesManager manager;
    private final SalesAnalyzer analyzer;

    // Constructor
    public Menu(SalesManager manager, SalesAnalyzer analyzer) {

        if (manager == null || analyzer == null) {
            throw new IllegalArgumentException(
                    "SalesManager and SalesAnalyzer cannot be null."
            );
        }

        this.manager = manager;
        this.analyzer = analyzer;
        scanner = new Scanner(System.in);
    }

    // Display menu
    public void displayMenu() {

        System.out.println("\n=====================================");
        System.out.println("          WELCOME TO SALESSCOPE");
        System.out.println("       RETAIL SALES ANALYTICS SYSTEM");
        System.out.println("=====================================");

        System.out.println("1. Add Sales Record");
        System.out.println("2. View All Sales Records");
        System.out.println("3. Search Product");
        System.out.println("4. Daily Sales Analysis");
        System.out.println("5. Weekly Sales Analysis");
        System.out.println("6. Top 5 Products by Revenue");
        System.out.println("7. Top 5 Products by Quantity");
        System.out.println("8. Category-wise Sales Analysis");
        System.out.println("9. Two-Week Sales Comparison");
        System.out.println("10. Exit");

        System.out.println("-------------------------------------");
        System.out.print("Enter your choice: ");
    }

    // Run application
    public void start() {

        boolean running = true;

        while (running) {

            displayMenu();

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    addSalesRecord();
                    break;

                case "2":
                    manager.displayAllRecords();
                    break;

                case "3":
                    searchProduct();
                    break;

                case "4":
                    dailySales();
                    break;

                case "5":
                    weeklySales();
                    break;

                case "6":
                    analyzer.displayTopProductsByRevenue();
                    break;

                case "7":
                    analyzer.displayTopProductsByQuantity();
                    break;

                case "8":
                    analyzer.displayCategoryAnalysis();
                    break;

                case "9":
                    compareWeeks();
                    break;

                case "10":
                    running = false;
                    System.out.println("\nThank you for using SalesScope!");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        }
    }

    // Add sales record
    private void addSalesRecord() {

        String product = readNonBlank("Enter product name: ");

        String category = readNonBlank("Enter category: ");

        int quantity = readPositiveInt("Enter quantity: ");

        double price = readPositiveDouble("Enter unit price: ");

        LocalDate date = readDate("Enter sale date (YYYY-MM-DD): ");

        SalesRecord record = new SalesRecord(
                product, category, quantity, price, date
        );

        manager.addSalesRecord(record);

        System.out.println("Sales record added successfully!");
    }

    // Search product
    private void searchProduct() {

        System.out.print("Enter product name to search: ");

        String product = scanner.nextLine();

        manager.searchProduct(product);
    }

    // Daily sales analysis
    private void dailySales() {

        LocalDate date = readDate("Enter date (YYYY-MM-DD): ");

        double revenue = analyzer.calculateDailyRevenue(date);

        System.out.printf(
                "Total revenue on %s: Rs. %.2f%n",
                date, revenue
        );
    }

    // Weekly sales analysis
    private void weeklySales() {

        LocalDate date = readDate(
                "Enter any date in the required week (YYYY-MM-DD): "
        );

        double revenue = analyzer.calculateWeeklyRevenue(date);

        System.out.printf(
                "Weekly revenue: Rs. %.2f%n",
                revenue
        );
    }

    // Compare two weeks
    private void compareWeeks() {

        LocalDate firstDate = readDate(
                "Enter a date from Week 1 (YYYY-MM-DD): "
        );

        LocalDate secondDate = readDate(
                "Enter a date from Week 2 (YYYY-MM-DD): "
        );

        analyzer.compareTwoWeeks(firstDate, secondDate);
    }

    // Read non-empty text
    private String readNonBlank(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    // Read a positive integer
    private int readPositiveInt(String message) {
        while (true) {
            System.out.print(message);

            try {
                int value = Integer.parseInt(scanner.nextLine().trim());

                if (value > 0) {
                    return value;
                }

                System.out.println("Quantity must be greater than zero.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Enter a whole number.");
            }
        }
    }

    // Read a positive, finite price
    private double readPositiveDouble(String message) {
        while (true) {
            System.out.print(message);

            try {
                double value = Double.parseDouble(scanner.nextLine().trim());

                if (Double.isFinite(value) && value > 0) {
                    return value;
                }

                System.out.println("Price must be a positive, finite number.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid price. Enter a valid number.");
            }
        }
    }

    // Read a valid date
    private LocalDate readDate(String message) {
        while (true) {
            System.out.print(message);

            try {
                return LocalDate.parse(scanner.nextLine().trim());

            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Invalid date. Use YYYY-MM-DD format.");
            }
        }
    }
}
