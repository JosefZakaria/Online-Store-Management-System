import java.util.Scanner;

public class CustomerController {
    private final CustomerDAO customerDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;

    public CustomerController() {
        this.customerDAO = new CustomerDAO();
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
    }

    public void customerMenu(Scanner scanner, String email) {
        boolean running = true;
        int customerId = customerDAO.getCustomerIdByEmail(email);

        if (customerId == -1) {
            System.out.println("Ingen kund hittades med den angivna e-posten.");
            return;
        }

        while (running) {
            System.out.println("\n### Kund Meny ###");
            System.out.println("1. Visa tillgängliga produkter");
            System.out.println("2. Skapa ny order");
            System.out.println("3. Lägg till produkter i en order");
            System.out.println("4. Visa dina ordrar");
            System.out.println("5. Ta bort en obekräftad order");
            System.out.println("6. Avsluta");
            System.out.print("Ange ditt val: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Rensa bufferten

            switch (choice) {
                case 1:
                    // Visa alla produkter
                    productDAO.getAllProducts();
                    break;

                case 2:
                    // Skapa en ny order
                    int newOrderId = orderDAO.addOrder(customerId, false);
                    if (newOrderId != -1) {
                        System.out.println("Ny order skapad med ID: " + newOrderId);
                    } else {
                        System.out.println("Misslyckades med att skapa en ny order.");
                    }
                    break;

                case 3:
                    // Lägg till flera produkter i order
                    int orderId = orderDAO.getUnconfirmedOrderId(customerId);
                    if (orderId == -1) {
                        System.out.println("Ingen obekräftad order hittades. Skapa en ny order först.");
                        break;
                    }

                    boolean addingProducts = true;

                    while (addingProducts) {
                        System.out.print("\nAnge produkt-ID (eller skriv -1 för att avsluta): ");
                        int productId = scanner.nextInt();
                        if (productId == -1) {
                            addingProducts = false;
                            break;
                        }

                        System.out.print("Ange antal för produkt-ID " + productId + ": ");
                        int quantity = scanner.nextInt();
                        scanner.nextLine(); // Rensa bufferten

                        boolean stockAvailable = productDAO.checkStock(productId, quantity);
                        if (stockAvailable) {
                            orderDAO.addProductToOrder(orderId, productId, quantity);
                            orderDAO.updateOrderTotalInJava(orderId);
                            System.out.println("Produkten har lagts till i order.");
                        } else {
                            System.out.println("Produkten finns inte i tillräckligt antal i lager.");
                        }
                    }

                    System.out.println("Alla produkter har lagts till i order-ID: " + orderId);
                    break;

                case 4:
                    // Visa kundens ordrar
                    orderDAO.viewOrdersByCustomer(customerId);
                    break;

                    // Ta bort en obekräftad order
                case 5:
                    System.out.print("Ange order-ID som ska tas bort: ");
                    int orderToDelete = scanner.nextInt();
                    scanner.nextLine(); // Rensa bufferten

                    boolean deleted = orderDAO.deleteUnconfirmedOrder(customerId, orderToDelete);
                    if (deleted) {
                        System.out.println("Ordern med ID " + orderToDelete + " har tagits bort.");
                    } else {
                        System.out.println("Misslyckades med att ta bort ordern. Kontrollera att ordern är obekräftad och tillhör dig.");
                    }
                    break;



                case 6:
                    System.out.println("Avslutar kundmenyn...");
                    running = false;
                    break;

                default:
                    System.out.println("Ogiltigt val. Försök igen.");
            }
        }
    }

    public void registerCustomer(Scanner scanner) {
        System.out.println("\n### Registrera ny kund ###");
        System.out.print("Förnamn: ");
        String firstName = scanner.nextLine();
        System.out.print("Efternamn: ");
        String lastName = scanner.nextLine();
        System.out.print("E-post: ");
        String email = scanner.nextLine();
        System.out.print("Adress: ");
        String address = scanner.nextLine();
        System.out.print("Stad: ");
        String city = scanner.nextLine();
        System.out.print("Land: ");
        String country = scanner.nextLine();
        System.out.print("Telefon: ");
        String phone = scanner.nextLine();

        int customerId = customerDAO.addCustomer(firstName, lastName, email, address, city, country, phone);
        if (customerId != -1) {
            System.out.println("Kund registrerad med ID: " + customerId);
        } else {
            System.out.println("Misslyckades med att registrera kunden.");
        }
    }
    public boolean isCustomerEmailValid(String email) {
        int customerId = customerDAO.getCustomerIdByEmail(email);
        return customerId != -1;
    }

}
