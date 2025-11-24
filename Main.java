import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CustomerController customerController = new CustomerController();
        AdminController adminController = new AdminController();

        boolean running = true;

        while (running) {
            System.out.println("\n### Huvudmeny ###");
            System.out.println("1. Kund");
            System.out.println("2. Admin");
            System.out.println("3. Visa alla produkter");
            System.out.println("4. Avsluta");
            System.out.print("Ange ditt val: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Rensa bufferten

            switch (choice) {
                case 1: // Kundmeny
                    customerMenu(scanner, customerController);
                    break;

                case 2: // Adminmeny
                    System.out.print("Ange admin-användarnamn: ");
                    String adminUsername = scanner.nextLine();
                    System.out.print("Ange admin-lösenord: ");
                    String adminPassword = scanner.nextLine();

                    if (adminController.login(adminUsername, adminPassword)) {
                        System.out.println("Inloggad som admin!");
                        adminController.adminMenu(scanner);
                    } else {
                        System.out.println("Ogiltiga admin-uppgifter. Försök igen.");
                    }
                    break;

                case 3: // Visa alla produkter
                    ProductDAO productDAO = new ProductDAO();
                    productDAO.getAllProducts();
                    break;

                case 4: // Avsluta
                    System.out.println("Avslutar programmet...");
                    running = false;
                    break;

                default:
                    System.out.println("Ogiltigt val. Försök igen.");
            }
        }

        scanner.close();
    }

    private static void customerMenu(Scanner scanner, CustomerController customerController) {
        boolean customerRunning = true;

        while (customerRunning) {
            System.out.println("\n### Kundmeny ###");
            System.out.println("1. Logga in");
            System.out.println("2. Registrera ny kund");
            System.out.println("3. Tillbaka till huvudmenyn");
            System.out.print("Ange ditt val: ");

            int customerChoice = scanner.nextInt();
            scanner.nextLine(); // Rensa bufferten

            switch (customerChoice) {
                case 1: // Logga in
                    System.out.print("Ange din e-post: ");
                    String email = scanner.nextLine();

                    if (customerController.isCustomerEmailValid(email)) {
                        System.out.println("Inloggad som kund!");
                        customerController.customerMenu(scanner, email);
                    } else {
                        System.out.println("Ogiltig e-post. Kontrollera och försök igen.");
                    }
                    break;

                case 2: // Registrera ny kund
                    customerController.registerCustomer(scanner);
                    break;

                case 3: // Tillbaka
                    System.out.println("Återvänder till huvudmenyn...");
                    customerRunning = false;
                    break;

                default:
                    System.out.println("Ogiltigt val. Försök igen.");
            }
        }
    }
}
