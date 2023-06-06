import java.sql.*;
import java.util.*;

public class CarSharing {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String databaseFileName = "default";
        for (int i = 0; i < args.length; i++) {
            if ("-databaseFileName".equals(args[i]) && i + 1 < args.length) {
                databaseFileName = args[i + 1];
                break;
            }
        }
        String url = "jdbc:h2:./src/carsharing/db/" + databaseFileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(true);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS COMPANY (" +
                        "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                        "NAME VARCHAR UNIQUE NOT NULL);");

                // now, create CAR table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CAR (" +
                        "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                        "NAME VARCHAR UNIQUE NOT NULL, " +
                        "COMPANY_ID INT NOT NULL, " +
                        "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID));");

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CUSTOMER (" +
                        "ID INT AUTO_INCREMENT PRIMARY KEY," +
                        "NAME VARCHAR UNIQUE NOT NULL," +
                        "RENTED_CAR_ID INT," +
                        "FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID))");

            }

            while (true) {
                System.out.println("1. Log in as a manager\n" +
                        "2. Log in as a customer\n" +
                        "3. Create a customer\n" +
                        "0. Exit");
                String option = scanner.nextLine();
                switch (option) {
                    case "1":
                        managerMenu(conn);
                        break;
                    case "2":
                        customerLogin(conn);
                        break;
                    case "3":
                        createCustomer(conn);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Unknown option: " + option);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void managerMenu(Connection conn) throws SQLException {
        while (true) {
            System.out.println("1. Company list\n" +
                    "2. Create a company\n" +
                    "0. Back");
            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    listCompanies(conn);
                    break;
                case "2":
                    createCompany(conn);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Unknown option: " + option);
            }
        }
    }

    private static void listCompanies(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM COMPANY ORDER BY ID");
            if (!rs.next()) {
                System.out.println("The company list is empty!");
            } else {
                System.out.println("Choose the company:");
                int i = 1;
                do {
                    System.out.println(i++ + ". " + rs.getString("NAME"));
                } while (rs.next());

                System.out.println("0. Back");

                int option = Integer.parseInt(scanner.nextLine());

                if (option == 0) {
                    return;
                }

                ResultSet rs2 = stmt.executeQuery("SELECT * FROM COMPANY ORDER BY ID LIMIT 1 OFFSET " + (option - 1));
                if (rs2.next()) {
                    int companyId = rs2.getInt("ID");
                    companyMenu(conn, companyId);
                }
            }
        }
    }


    private static void companyMenu(Connection conn, int companyId) throws SQLException {
        while (true) {
            System.out.println("'Company name' company\n" +
                    "1. Car list\n" +
                    "2. Create a car\n" +
                    "0. Back");
            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    listCars(conn, companyId);
                    break;
                case "2":
                    createCar(conn, companyId);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Unknown option: " + option);
            }
        }
    }

    private static void listCars(Connection conn, int companyId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CAR WHERE COMPANY_ID = ? ORDER BY ID")) {
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                System.out.println("The car list is empty!");
            } else {
                System.out.println("Car list:");
                int i = 1;
                do {
                    System.out.println(i++ + ". " + rs.getString("NAME"));
                } while (rs.next());
            }
        }
    }

    private static void createCar(Connection conn, int companyId) throws SQLException {
        System.out.println("Enter the car name:");
        String name = scanner.nextLine();
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO CAR (NAME, COMPANY_ID) VALUES (?, ?)")) {
            stmt.setString(1, name);
            stmt.setInt(2, companyId);
            stmt.executeUpdate();
            System.out.println("The car was added!");
        }
    }

    private static void createCompany(Connection conn) throws SQLException {
        System.out.println("Enter the company name:");
        String name = scanner.nextLine();
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO COMPANY (NAME) VALUES (?)")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            System.out.println("The company was created!");
        }
    }

    private static void mainMenu(Connection conn) throws SQLException {
        System.out.println("1. Log in as a manager");
        System.out.println("2. Log in as a customer");
        System.out.println("3. Create a customer");
        System.out.println("0. Exit");

        int option = Integer.parseInt(scanner.nextLine());
        switch (option) {
            case 1:
                managerMenu(conn);
                break;
            case 2:
                customerLogin(conn);
                break;
            case 3:
                createCustomer(conn);
                break;
            case 0:
                // exit the application
                break;
        }
    }

    private static void createCustomer(Connection conn) throws SQLException {
        System.out.println("Enter the customer name:");
        String name = scanner.nextLine();

        PreparedStatement preparedStatement = conn.prepareStatement(
                "INSERT INTO CUSTOMER (NAME) VALUES (?)");
        preparedStatement.setString(1, name);
        preparedStatement.executeUpdate();

        System.out.println("The customer was added!");
    }

    private static void customerLogin(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM CUSTOMER ORDER BY ID");

        if (!rs.next()) {
            System.out.println("The customer list is empty!");
        } else {
            System.out.println("Choose a customer:");
            int i = 1;
            List<Integer> customerIds = new ArrayList<>();
            do {
                System.out.println(i++ + ". " + rs.getString("NAME"));
                customerIds.add(rs.getInt("ID"));
            } while (rs.next());

            System.out.println("0. Back");

            int option = Integer.parseInt(scanner.nextLine());

            if (option == 0) {
                return;
            }

            int customerId = customerIds.get(option - 1);
            customerMenu(conn, customerId);
        }
    }

    private static void customerMenu(Connection conn, int customerId) throws SQLException {
        while (true) {  // add a while loop here
            System.out.println("1. Rent a car");
            System.out.println("2. Return a rented car");
            System.out.println("3. My rented car");
            System.out.println("0. Back");

            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 1:
                    rentCar(conn, customerId);
                    break;
                case 2:
                    returnCar(conn, customerId);
                    break;
                case 3:
                    viewRentedCar(conn, customerId);
                    break;
                case 0:
                    return;  // terminate the loop and go back to the previous menu
                default:
                    System.out.println("Unknown option: " + option);
            }
        }
    }


    private static void rentCar(Connection conn, int customerId) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID = " + customerId);

        if (rs.next() && rs.getInt("RENTED_CAR_ID") != 0) {
            System.out.println("You've already rented a car!");
            return;
        }

        rs = stmt.executeQuery("SELECT * FROM COMPANY ORDER BY ID");

        if (!rs.next()) {
            System.out.println("The company list is empty!");
        } else {
            System.out.println("Choose a company:");
            int i = 1;
            List<Integer> companyIds = new ArrayList<>();
            do {
                System.out.println(i++ + ". " + rs.getString("NAME"));
                companyIds.add(rs.getInt("ID"));
            } while (rs.next());

            System.out.println("0. Back");

            int option = Integer.parseInt(scanner.nextLine());

            if (option == 0) {
                return;
            }

            int companyId = companyIds.get(option - 1);
            rs = stmt.executeQuery("SELECT * FROM CAR WHERE COMPANY_ID = " + companyId + " AND ID NOT IN (SELECT RENTED_CAR_ID FROM CUSTOMER WHERE RENTED_CAR_ID IS NOT NULL) ORDER BY ID");

            if (!rs.next()) {
                System.out.println("No available cars in the 'Company name' company.");
            } else {
                System.out.println("Choose a car:");
                i = 1;
                List<Integer> carIds = new ArrayList<>();
                List<String> carNames = new ArrayList<>();  // store car names
                do {
                    String carName = rs.getString("NAME");
                    System.out.println(i++ + ". " + carName);
                    carIds.add(rs.getInt("ID"));
                    carNames.add(carName);  // add car name to the list
                } while (rs.next());

                System.out.println("0. Back");

                option = Integer.parseInt(scanner.nextLine());

                if (option == 0) {
                    return;
                }

                int carId = carIds.get(option - 1);
                PreparedStatement preparedStatement = conn.prepareStatement(
                        "UPDATE CUSTOMER SET RENTED_CAR_ID = ? WHERE ID = ?");
                preparedStatement.setInt(1, carId);
                preparedStatement.setInt(2, customerId);
                preparedStatement.executeUpdate();

                String rentedCarName = carNames.get(option - 1);  // get the rented car name
                System.out.println("You rented '" + rentedCarName + "'");
            }
        }
    }


    private static void returnCar(Connection conn, int customerId) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID = " + customerId);

        if (rs.next() && rs.getInt("RENTED_CAR_ID") != 0) {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "UPDATE CUSTOMER SET RENTED_CAR_ID = NULL WHERE ID = ?");
            preparedStatement.setInt(1, customerId);
            preparedStatement.executeUpdate();

            System.out.println("You've returned a rented car!");
        } else {
            System.out.println("You didn't rent a car!");
        }
    }



    private static void viewRentedCar(Connection conn, int customerId) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID = " + customerId);

        if (rs.next() && rs.getInt("RENTED_CAR_ID") != 0) {
            int carId = rs.getInt("RENTED_CAR_ID");
            rs = stmt.executeQuery("SELECT NAME FROM CAR WHERE ID = " + carId);
            if (rs.next()) {
                String carName = rs.getString("NAME");
                rs = stmt.executeQuery("SELECT NAME FROM COMPANY WHERE ID = (SELECT COMPANY_ID FROM CAR WHERE ID = " + carId + ")");
                if (rs.next()) {
                    String companyName = rs.getString("NAME");

                    System.out.println("Your rented car:");
                    System.out.println(carName);
                    System.out.println("Company:");
                    System.out.println(companyName);
                }
            }
        } else {
            System.out.println("You didn't rent a car!");
        }
    }



}
