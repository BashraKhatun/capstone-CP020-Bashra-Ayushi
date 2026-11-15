
public class Main {

    public static void main(String[] args) {

        // Create SalesManager
        SalesManager manager = new SalesManager();

        // Create SalesAnalyzer
        SalesAnalyzer analyzer = new SalesAnalyzer(manager);

        // Create and start Menu
        Menu menu = new Menu(manager, analyzer);

        menu.start();
    }
}
