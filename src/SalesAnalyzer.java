
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.ArrayList;

public class SalesAnalyzer {

    private SalesManager manager;

    // Constructor
    public SalesAnalyzer(SalesManager manager) {

        if (manager == null) {
            throw new IllegalArgumentException("SalesManager cannot be null.");
        }

        this.manager = manager;
    }

    // Calculate sales for a particular date
    public double calculateDailyRevenue(LocalDate date) {

        double totalRevenue = 0;

        List<SalesRecord> records = manager.getSalesRecords();

        for (SalesRecord record : records) {

            if (record.getSaleDate().equals(date)) {

                totalRevenue += record.getRevenue();
            }
        }

        return totalRevenue;
    }

    // Calculate weekly revenue
    public double calculateWeeklyRevenue(LocalDate selectedDate) {

        LocalDate weekStart = selectedDate.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        );

        LocalDate weekEnd = weekStart.plusDays(6);

        double totalRevenue = 0;

        List<SalesRecord> records = manager.getSalesRecords();

        for (SalesRecord record : records) {

            LocalDate saleDate = record.getSaleDate();

            if (!saleDate.isBefore(weekStart)
                    && !saleDate.isAfter(weekEnd)) {

                totalRevenue += record.getRevenue();
            }
        }

        return totalRevenue;
    }


    // Display top 5 products by revenue
    public void displayTopProductsByRevenue() {

        Map<String, Double> productRevenue = new HashMap<>();

        List<SalesRecord> records = manager.getSalesRecords();

        // Calculate total revenue for each product
        for (SalesRecord record : records) {

            String product = record.getProductName();

            productRevenue.put(
                    product,
                    productRevenue.getOrDefault(product, 0.0)
                            + record.getRevenue()
            );
        }

        // Convert map entries into a list for sorting
        List<Map.Entry<String, Double>> sortedProducts =
                new ArrayList<>(productRevenue.entrySet());

        // Sort products by revenue in descending order
        sortedProducts.sort(
                Map.Entry.<String, Double>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry.comparingByKey())
        );

        System.out.println("\n===== TOP 5 PRODUCTS BY REVENUE =====");

        int limit = Math.min(5, sortedProducts.size());

        for (int i = 0; i < limit; i++) {

            Map.Entry<String, Double> entry = sortedProducts.get(i);

            System.out.printf(
                    "%d. %s - Rs. %.2f%n",
                    i + 1,
                    entry.getKey(),
                    entry.getValue()
            );
        }

        if (limit == 0) {
            System.out.println("No sales records available.");
        }
    }


    // Display top 5 products by quantity sold
    public void displayTopProductsByQuantity() {

        Map<String, Integer> productQuantity = new HashMap<>();

        List<SalesRecord> records = manager.getSalesRecords();

        // Calculate total quantity for each product
        for (SalesRecord record : records) {

            String product = record.getProductName();

            productQuantity.put(
                    product,
                    productQuantity.getOrDefault(product, 0)
                            + record.getQuantity()
            );
        }

        // Convert entries into a list
        List<Map.Entry<String, Integer>> sortedProducts =
                new ArrayList<>(productQuantity.entrySet());

        // Sort by quantity in descending order
        sortedProducts.sort(
                Map.Entry.<String, Integer>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry.comparingByKey())
        );

        System.out.println("\n===== TOP 5 PRODUCTS BY QUANTITY =====");

        int limit = Math.min(5, sortedProducts.size());

        for (int i = 0; i < limit; i++) {

            Map.Entry<String, Integer> entry = sortedProducts.get(i);

            System.out.printf(
                    "%d. %s - %d units%n",
                    i + 1,
                    entry.getKey(),
                    entry.getValue()
            );
        }

        if (limit == 0) {
            System.out.println("No sales records available.");
        }
    }


    // Analyze category-wise sales contribution
    public void displayCategoryAnalysis() {

        Map<String, Double> categoryRevenue = new LinkedHashMap<>();

        List<SalesRecord> records = manager.getSalesRecords();

        double totalRevenue = 0;

        // Calculate revenue for each category
        for (SalesRecord record : records) {

            String category = record.getCategory();

            double revenue = record.getRevenue();

            categoryRevenue.put(
                    category,
                    categoryRevenue.getOrDefault(category, 0.0) + revenue
            );

            totalRevenue += revenue;
        }

        System.out.println("\n===== CATEGORY-WISE SALES ANALYSIS =====");

        if (totalRevenue == 0) {
            System.out.println("No sales revenue available.");
            return;
        }

        // Display category revenue and contribution
        for (Map.Entry<String, Double> entry : categoryRevenue.entrySet()) {

            String category = entry.getKey();

            double revenue = entry.getValue();

            double percentage = (revenue / totalRevenue) * 100;

            int barLength = (int) (percentage / 2);

            System.out.printf(
                    "%n%s: Rs. %.2f (%.2f%%)%n",
                    category,
                    revenue,
                    percentage
            );

            System.out.println("*".repeat(barLength));
        }

        System.out.printf(
                "%nTotal Revenue: Rs. %.2f%n",
                totalRevenue
        );
    }


    // Compare sales between two weeks
    public void compareTwoWeeks(LocalDate firstWeekDate,
                                LocalDate secondWeekDate) {

        double firstWeekRevenue =
                calculateWeeklyRevenue(firstWeekDate);

        double secondWeekRevenue =
                calculateWeeklyRevenue(secondWeekDate);

        double difference = secondWeekRevenue - firstWeekRevenue;

        System.out.println("\n===== TWO-WEEK SALES COMPARISON =====");

        System.out.printf(
                "Week 1 Revenue: Rs. %.2f%n",
                firstWeekRevenue
        );

        System.out.printf(
                "Week 2 Revenue: Rs. %.2f%n",
                secondWeekRevenue
        );

        System.out.printf(
                "Revenue Difference: Rs. %.2f%n",
                difference
        );

        // Handle division by zero
        if (firstWeekRevenue == 0) {

            if (secondWeekRevenue > 0) {
                System.out.println(
                        "Sales increased from zero revenue in Week 1."
                );
            } else {
                System.out.println(
                        "No revenue recorded in either week."
                );
            }

            return;
        }

        double percentageChange =
                (difference / firstWeekRevenue) * 100;

        System.out.printf(
                "Percentage Change: %.2f%%%n",
                percentageChange
        );

        if (percentageChange > 0) {

            System.out.println("Sales Performance: Increased");

        } else if (percentageChange < 0) {

            System.out.println("Sales Performance: Decreased");

        } else {

            System.out.println("Sales Performance: Unchanged");
        }
    }

}
