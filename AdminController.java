import java.util.Scanner;


public class AdminController {
    private final AdminDAO adminDAO;
    private final SupplierDAO supplierDAO;
    private final ProductDAO productDAO;
    private final DiscountDAO discountDAO;
    private final OrderDAO orderDAO;

    public AdminController() {
        this.adminDAO = new AdminDAO();
        this.supplierDAO = new SupplierDAO();
        this.productDAO = new ProductDAO();
        this.discountDAO = new DiscountDAO();
        this.orderDAO = new OrderDAO();
    }

    public boolean login(String username, String password) {

        return adminDAO.getAdmin(username, password);
    }

    public void adminMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\n### Admin Meny ###");
            System.out.println("1. Lägg till leverantör");
            System.out.println("2. Visa alla leverantörer");
            System.out.println("3. Lägg till en produkt");
            System.out.println("4. Redigera produktens kvantitet");
            System.out.println("5. Ta bort en produkt");
            System.out.println("6. Visa alla produkter");
            System.out.println("7. Lägg till rabatt");
            System.out.println("8. Hantera rabatter på produkter");
            System.out.println("9. Visa rabatt-historik");
            System.out.println("10. Bekräfta ordrar");
            System.out.println("11. Visa topp-produkter per månad");
            System.out.println("12. Avsluta");
            System.out.print("Ange ditt val: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Rensa bufferten

            switch (choice) {
                case 1 -> addSupplier(scanner);
                case 2 -> viewAllSuppliers();
                case 3 -> addProduct(scanner);
                case 4 -> editProductQuantity(scanner);
                case 5 -> deleteProduct(scanner);
                case 6 -> viewAllProducts();
                case 7 -> addDiscount(scanner);
                case 8 -> manageDiscountsOnProduct(scanner);
                case 9 -> viewDiscountHistory();
                case 10 -> confirmOrders(scanner);
                case 11 -> viewTopProductsByMonth();
                case 12 -> {
                    System.out.println("Avslutar adminmenyn...");
                    running = false;
                }
                default -> System.out.println("Ogiltigt val. Försök igen.");
            }
        }
    }

    private void addSupplier(Scanner scanner) {
        System.out.println("\n### Lägg till leverantör ###");
        System.out.print("Namn: ");
        String name = scanner.nextLine();
        System.out.print("Telefon: ");
        String phone = scanner.nextLine();
        System.out.print("Adress: ");
        String address = scanner.nextLine();

        supplierDAO.addSupplier(name, phone, address);
    }

    private void viewAllSuppliers() {
        System.out.println("\n### Visa alla leverantörer ###");
        supplierDAO.getAllSuppliers();
    }

    private void addProduct(Scanner scanner) {
        System.out.println("\n### Lägg till produkt ###");
        System.out.print("Produktnamn: ");
        String name = scanner.nextLine();
        System.out.print("Produktkod: ");
        String code = scanner.nextLine();
        System.out.print("Pris: ");
        double price = scanner.nextDouble();
        System.out.print("Kvantitet: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Rensa bufferten

        System.out.println("\nTillgängliga leverantörer:");
        supplierDAO.getAllSuppliers();
        System.out.print("Ange leverantörens ID (eller 0 för att hoppa över): ");
        int supplierId = scanner.nextInt();
        scanner.nextLine(); // Rensa bufferten

        System.out.println("\nTillgängliga rabatter:");
        discountDAO.viewAllDiscounts();
        System.out.print("Ange rabattens ID (eller 0 för att hoppa över): ");
        int discountId = scanner.nextInt();
        scanner.nextLine(); // Rensa bufferten

        productDAO.addProduct(name, code, price, quantity, supplierId, discountId);
    }


    private void editProductQuantity(Scanner scanner) {
        System.out.println("\n### Redigera produktens kvantitet ###");
        System.out.print("Ange produktens ID: ");
        int productId = scanner.nextInt();
        System.out.print("Ange ny kvantitet: ");
        int newQuantity = scanner.nextInt();
        scanner.nextLine(); // Rensa bufferten

        productDAO.updateProductQuantity(productId, newQuantity);
    }

    private void deleteProduct(Scanner scanner) {
        System.out.println("\n### Ta bort en produkt ###");
        System.out.print("Ange produktens ID: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Rensa bufferten

        if (!productDAO.doesProductExist(productId)) {
            System.out.println("Produkten med ID " + productId + " existerar inte.");
            return;
        }

        productDAO.deleteProduct(productId);
    }


    private void viewAllProducts() {
        System.out.println("\n### Visa alla produkter ###");
        productDAO.getAllProducts();
    }

    private void addDiscount(Scanner scanner) {
        System.out.println("\n### Lägg till rabatt ###");
        System.out.print("Rabattkod: ");
        String code = scanner.nextLine();
        System.out.print("Procent: ");
        double percentage = scanner.nextDouble();
        scanner.nextLine(); // Rensa bufferten
        System.out.print("Anledning: ");
        String reason = scanner.nextLine();
        System.out.print("Startdatum (YYYY-MM-DD): ");
        String startDate = scanner.nextLine();
        System.out.print("Slutdatum (YYYY-MM-DD): ");
        String endDate = scanner.nextLine();

        discountDAO.addDiscount(code, percentage, reason, startDate, endDate);
    }

    private void manageDiscountsOnProduct(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\n### Hantera rabatter på produkter ###");
            System.out.println("1. Lägg till en rabatt på en produkt");
            System.out.println("2. Ta bort en rabatt från en produkt");
            System.out.println("3. Visa rabatterade produkter");
            System.out.println("4. Avsluta");
            System.out.print("Ange ditt val: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Rensa bufferten

            switch (choice) {
                case 1 -> {
                    System.out.println("\n### Lägg till rabatt på en produkt ###");
                    discountDAO.viewAllDiscounts();
                    System.out.print("Ange rabattens ID: ");
                    int discountId = scanner.nextInt();
                    productDAO.getAllProducts();
                    System.out.print("Ange produktens ID: ");
                    int productId = scanner.nextInt();
                    discountDAO.assignDiscountToProduct(discountId, productId);
                }
                case 2 -> {
                    System.out.println("\n### Ta bort rabatt från en produkt ###");

                    // Visa alla produkter med rabatter
                    System.out.println("Här är en lista över produkter med rabatter:");
                    productDAO.getProductsWithDiscounts();

                    System.out.print("Ange rabattens ID som ska tas bort: ");
                    int removeDiscountId = scanner.nextInt();
                    System.out.print("Ange produktens ID som ska tas bort från rabatten: ");
                    int removeProductId = scanner.nextInt();

                    discountDAO.removeDiscountFromProduct(removeDiscountId, removeProductId);
                }

                case 3 -> {
                    System.out.println("\n### Lista över rabatterade produkter ###");
                    productDAO.getDiscountedProducts();
                }
                case 4 -> {
                    System.out.println("Avslutar hantering av rabatter...");
                    running = false;
                }
                default -> System.out.println("Ogiltigt val. Försök igen.");
            }
        }
    }

    private void viewDiscountHistory() {
        System.out.println("\n### Visa rabatt-historik ###");
        discountDAO.getDiscountHistory();
    }

    private void confirmOrders(Scanner scanner) {
        System.out.println("\n### Bekräfta ordrar ###");
        orderDAO.confirmOrders();
    }

    private void viewTopProductsByMonth() {
        System.out.println("\n### Visa topp-produkter per månad ###");
        orderDAO.getTopProductsByMonth();
    }
}
