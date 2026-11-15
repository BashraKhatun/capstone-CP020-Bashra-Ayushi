
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

public class DashboardBridge {

    private static SalesManager manager;
    private static SalesAnalyzer analyzer;

    public static void main(String[] args) throws Exception {

        manager = new SalesManager();
        analyzer = new SalesAnalyzer(manager);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in)
        );

        String command;

        while ((command = reader.readLine()) != null) {

            try {
                handleCommand(command);
            } catch (Exception e) {
                sendError(e.getMessage());
            }

        }
    }

    // Process commands received from Python
    private static void handleCommand(String command) {

        String[] parts = command.split("\\|", -1);

        switch (parts[0]) {

            case "PING":
                send("{\"status\":\"READY\"}");
                break;

            case "EXIT":
                send("{\"status\":\"CLOSED\"}");
                break;

            case "ADD":
                addRecord(parts);
                break;

            case "LIST":
                listRecords();
                break;

            case "SEARCH":
                searchProduct(parts);
                break;

            case "SUMMARY":
                showSummary();
                break;

            case "DAILY":
                showDailyRevenue(parts);
                break;

            case "WEEKLY":
                showWeeklyRevenue(parts);
                break;

            case "TOP_REVENUE":
                showTopProducts(true);
                break;

            case "TOP_QUANTITY":
                showTopProducts(false);
                break;

            case "CATEGORY":
                showCategoryAnalysis();
                break;

            case "COMPARE":
                compareWeeks(parts);
                break;

            default:
                sendError("Unknown command.");
        }
    }

    // Decode text received from Python
    private static String decode(String value) {
        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    // Add a new sales record
    private static void addRecord(String[] parts) {

        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid ADD command.");
        }

        String product = decode(parts[1]);
        String category = decode(parts[2]);

        int quantity = Integer.parseInt(parts[3]);
        double price = Double.parseDouble(parts[4]);
        LocalDate date = LocalDate.parse(parts[5]);

        SalesRecord record = new SalesRecord(
                product, category, quantity, price, date
        );

        manager.addSalesRecord(record);

        send("{\"status\":\"SUCCESS\",\"message\":\"Sales record added successfully.\"}");
    }

    // Return all sales records
    private static void listRecords() {

        List<SalesRecord> records = manager.getSalesRecords();

        StringBuilder json = new StringBuilder();

        json.append("{\"status\":\"SUCCESS\",\"records\":[");

        for (int i = 0; i < records.size(); i++) {

            SalesRecord r = records.get(i);

            if (i > 0) json.append(",");

            json.append("{")
                    .append("\"product\":\"").append(escape(r.getProductName())).append("\",")
                    .append("\"category\":\"").append(escape(r.getCategory())).append("\",")
                    .append("\"quantity\":").append(r.getQuantity()).append(",")
                    .append("\"unitPrice\":").append(r.getUnitPrice()).append(",")
                    .append("\"date\":\"").append(r.getSaleDate()).append("\",")
                    .append("\"revenue\":").append(r.getRevenue())
                    .append("}");
        }

        json.append("]}");

        send(json.toString());
    }

    // Search for a product
    private static void searchProduct(String[] parts) {

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid SEARCH command.");
        }

        String search = decode(parts[1]).trim();

        if (search.isEmpty()) {
            throw new IllegalArgumentException("Enter a product name.");
        }

        List<SalesRecord> matches = new ArrayList<>();

        for (SalesRecord r : manager.getSalesRecords()) {
            if (r.getProductName().equalsIgnoreCase(search)) {
                matches.add(r);
            }
        }

        StringBuilder json = new StringBuilder();

        json.append("{\"status\":\"SUCCESS\",\"records\":[");

        for (int i = 0; i < matches.size(); i++) {

            SalesRecord r = matches.get(i);

            if (i > 0) json.append(",");

            json.append("{")
                    .append("\"product\":\"").append(escape(r.getProductName())).append("\",")
                    .append("\"category\":\"").append(escape(r.getCategory())).append("\",")
                    .append("\"quantity\":").append(r.getQuantity()).append(",")
                    .append("\"unitPrice\":").append(r.getUnitPrice()).append(",")
                    .append("\"date\":\"").append(r.getSaleDate()).append("\",")
                    .append("\"revenue\":").append(r.getRevenue())
                    .append("}");
        }

        json.append("]}");

        send(json.toString());
    }

    // Overall sales summary
    private static void showSummary() {

        List<SalesRecord> records = manager.getSalesRecords();

        double revenue = 0;
        int quantity = 0;

        Set<String> categories = new HashSet<>();

        for (SalesRecord r : records) {

            revenue += r.getRevenue();
            quantity += r.getQuantity();
            categories.add(r.getCategory());
        }

        send(
                "{\"status\":\"SUCCESS\"," +
                        "\"totalRevenue\":" + revenue + "," +
                        "\"totalRecords\":" + records.size() + "," +
                        "\"totalQuantity\":" + quantity + "," +
                        "\"totalCategories\":" + categories.size() +
                        "}"
        );
    }

    // Daily revenue
    private static void showDailyRevenue(String[] parts) {

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid DAILY command.");
        }

        LocalDate date = LocalDate.parse(parts[1]);

        double revenue = analyzer.calculateDailyRevenue(date);

        send(
                "{\"status\":\"SUCCESS\"," +
                        "\"date\":\"" + date + "\"," +
                        "\"revenue\":" + revenue +
                        "}"
        );
    }

    // Weekly revenue
    private static void showWeeklyRevenue(String[] parts) {

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid WEEKLY command.");
        }

        LocalDate date = LocalDate.parse(parts[1]);

        double revenue = analyzer.calculateWeeklyRevenue(date);

        LocalDate monday = date.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        );

        LocalDate sunday = monday.plusDays(6);

        send(
                "{\"status\":\"SUCCESS\"," +
                        "\"weekStart\":\"" + monday + "\"," +
                        "\"weekEnd\":\"" + sunday + "\"," +
                        "\"revenue\":" + revenue +
                        "}"
        );
    }

    // Top 5 products by revenue or quantity
    private static void showTopProducts(boolean byRevenue) {

        Map<String, ProductStats> products = new HashMap<>();

        for (SalesRecord r : manager.getSalesRecords()) {

            String name = r.getProductName();

            ProductStats stats = products.getOrDefault(
                    name, new ProductStats()
            );

            stats.quantity += r.getQuantity();
            stats.revenue += r.getRevenue();

            products.put(name, stats);
        }

        List<Map.Entry<String, ProductStats>> sorted =
                new ArrayList<>(products.entrySet());

        if (byRevenue) {
            sorted.sort(
                    Comparator.comparingDouble(
                            (Map.Entry<String, ProductStats> e) -> e.getValue().revenue
                    ).reversed().thenComparing(Map.Entry::getKey)
            );
        } else {
            sorted.sort(
                    Comparator.comparingInt(
                            (Map.Entry<String, ProductStats> e) -> e.getValue().quantity
                    ).reversed().thenComparing(Map.Entry::getKey)
            );
        }

        StringBuilder json = new StringBuilder();

        json.append("{\"status\":\"SUCCESS\",\"products\":[");

        int limit = Math.min(5, sorted.size());

        for (int i = 0; i < limit; i++) {

            Map.Entry<String, ProductStats> entry = sorted.get(i);

            if (i > 0) json.append(",");

            json.append("{")
                    .append("\"product\":\"").append(escape(entry.getKey())).append("\",")
                    .append("\"quantity\":").append(entry.getValue().quantity).append(",")
                    .append("\"revenue\":").append(entry.getValue().revenue)
                    .append("}");
        }

        json.append("]}");

        send(json.toString());
    }

    // Category-wise revenue analysis
    private static void showCategoryAnalysis() {

        Map<String, Double> categories = new HashMap<>();

        double totalRevenue = 0;

        for (SalesRecord r : manager.getSalesRecords()) {

            categories.put(
                    r.getCategory(),
                    categories.getOrDefault(r.getCategory(), 0.0) + r.getRevenue()
            );

            totalRevenue += r.getRevenue();
        }

        StringBuilder json = new StringBuilder();

        json.append("{\"status\":\"SUCCESS\",\"totalRevenue\":")
                .append(totalRevenue)
                .append(",\"categories\":[");

        List<Map.Entry<String, Double>> sorted =
                new ArrayList<>(categories.entrySet());

        sorted.sort(
                Comparator.comparingDouble(
                        (Map.Entry<String, Double> e) -> e.getValue()
                ).reversed()
        );

        for (int i = 0; i < sorted.size(); i++) {

            Map.Entry<String, Double> entry = sorted.get(i);

            double percentage = totalRevenue == 0
                    ? 0
                    : entry.getValue() / totalRevenue * 100;

            if (i > 0) json.append(",");

            json.append("{")
                    .append("\"category\":\"").append(escape(entry.getKey())).append("\",")
                    .append("\"revenue\":").append(entry.getValue()).append(",")
                    .append("\"percentage\":").append(percentage)
                    .append("}");
        }

        json.append("]}");

        send(json.toString());
    }

    // Compare two weeks
    private static void compareWeeks(String[] parts) {

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid COMPARE command.");
        }

        LocalDate date1 = LocalDate.parse(parts[1]);
        LocalDate date2 = LocalDate.parse(parts[2]);

        double week1 = analyzer.calculateWeeklyRevenue(date1);
        double week2 = analyzer.calculateWeeklyRevenue(date2);

        double difference = week2 - week1;

        String status;
        String percentage;

        if (week1 == 0) {

            status = week2 == 0 ? "NO_CHANGE" : "NO_BASELINE";
            percentage = "null";

        } else {

            double change = (difference / week1) * 100;

            status = difference > 0
                    ? "INCREASED"
                    : difference < 0
                    ? "DECREASED"
                    : "NO_CHANGE";

            percentage = String.valueOf(change);
        }

        send(
                "{\"status\":\"SUCCESS\"," +
                        "\"week1Revenue\":" + week1 + "," +
                        "\"week2Revenue\":" + week2 + "," +
                        "\"difference\":" + difference + "," +
                        "\"percentageChange\":" + percentage + "," +
                        "\"performance\":\"" + status + "\"" +
                        "}"
        );
    }

    // Escape special characters in JSON text
    private static String escape(String value) {

        if (value == null) return "";

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Send a successful response
    private static void send(String message) {
        System.out.println(message);
        System.out.flush();
    }

    // Send an error response
    private static void sendError(String message) {

        if (message == null) {
            message = "An unexpected error occurred.";
        }

        send(
                "{\"status\":\"ERROR\",\"message\":\"" +
                        escape(message) + "\"}"
        );
    }

    // Stores combined product statistics
    private static class ProductStats {
        int quantity = 0;
        double revenue = 0;
    }
}
