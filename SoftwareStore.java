package SoftwareStore;

import java.sql.Date;
import java.util.Scanner;
import java.util.Vector;

public class SoftwareStore {

    static Scanner keyboard = new Scanner(System.in);

    static database _database = new database();

    static Vector<?> products = new Vector<Object>();

    static int global_product = 0;

    static String login;
    static String password;
    static String group;
    static String process;
    static int member_id;
    static String product_id;
    static String quantity;
    static String approvalstatusid;
    static String approvalstatus;
    static String rating;
    static String setRating;
    static String ratingvalue;
    static String comment;
    static String commentid;
    static String transaction_id;
    static String status;
    static String statusid;

    public static void main(String[] args) {


        System.out.println("----------------------------------------------------------");
        System.out.println();
        System.out.println("            ONLINE SOFTWARE STORE DATABASE");
        System.out.println();
        System.out.println("----------------------------------------------------------");
        System.out.println();
        System.out.println("PLEASE ENTER YOUR LOGIN, PASSWORD, GROUP (CLIENT OR OWNER)");
        System.out.println();

        System.out.print("LOGIN:");
        login = keyboard.nextLine();
        System.out.println();

        System.out.print("PASSWORD:");
        password = keyboard.nextLine();
        System.out.println();

        System.out.print("GROUP (CLIENT OR OWNER):");
        group = keyboard.nextLine();
        System.out.println("----------------------------------------------------------");

        String[] credential = new String[3];
        credential = _database.getPasswordGroupId(login);
        if (password.toUpperCase().equals(credential[0].toUpperCase())) {
            if (group.toUpperCase().equals(credential[1].toUpperCase())) {

                member_id = Integer.parseInt(credential[2]);

                if (group.toUpperCase().equals("CLIENT")) {
                    System.out.println();
                    System.out.println("1. NEW ORDER");
                    System.out.println("2. VIEW MY ORDERS");
                    System.out.println("3. PRODUCT RATING");
                    System.out.println("4. COMMENTS");
                    System.out.println("5. EXIT");
                    System.out.print("SELECT ONE OF THE ACTIONS:");
                    process = keyboard.nextLine();

                    if (process.equals("1")) {
                        process(process);
                    }

                    else if (process.equals("2")) {
                        System.out.println();
                        System.out.println("SELECT ORDER STATUS");
                        System.out.println("1. APPROVED");
                        System.out.println("2. PENDING");
                        System.out.println("3. REJECTED");
                        System.out.print("ENTER STATUS NUMBER (1 OR 2 OR 3):");

                        approvalstatusid = keyboard.nextLine();
                        if (approvalstatusid.equals("1")) {
                            approvalstatus = "APPROVED";
                            getMyOrders(approvalstatus);
                            System.exit(0);
                        }
                        else if (approvalstatusid.equals("2")) {
                            approvalstatus = "PENDING";
                            getMyOrders(approvalstatus);
                            System.exit(0);
                        }
                        else if (approvalstatusid.equals("3")) {
                            approvalstatus = "REJECTED";
                            getMyOrders(approvalstatus);
                            System.exit(0);
                        }
                        else {
                            System.exit(0);
                        }
                    }
                    else if (process.equals("3")) {
                        System.out.println();
                        System.out.println("1. VIEW MY PRODUCT RATINGS");
                        System.out.println("2. SET RATING FOR THE PRODUCT");
                        System.out.print("SELECT ACTION:");

                        rating = keyboard.nextLine();
                        if (rating.equals("1")) {
                            getMyRatings();
                            System.exit(0);
                        }
                        else if (rating.equals("2")) {
                            setRating();
                            System.exit(0);
                        }
                    }
                    else if (process.equals("4")) {
                        System.out.println();
                        System.out.println("1. VIEW MY COMMENTS");
                        System.out.println("2. ADD NEW COMMENT");
                        System.out.print("SELECT ACTION:");
                        commentid = keyboard.nextLine();
                        if (commentid.equals("1")) {
                            getMyComments();
                            System.exit(0);
                        }
                        else if (commentid.equals("2")) {
                            addComment();
                            System.exit(0);
                        }
                        else {
                            System.exit(0);
                        }

                    }
                    else if (process.equals("5")) {
                        System.out.println("THANK YOU! GOODBYE!");
                        System.exit(0);
                    }
                    else {
                        System.exit(0);
                    }

                }
                else if (group.toUpperCase().equals("OWNER")) {
                    System.out.println();
                    System.out.println("1. APPROVE OR REJECT PENDING ORDERS");
                    System.out.println("2. EXIT");
                    System.out.print("SELECT ACTION:");
                    process = keyboard.nextLine();

                    if (process.equals("1")) {
                        setApproveReject();
                    }
                    else if (process.equals("2")) {
                        System.exit(0);
                    }
                    else {
                        System.exit(0);
                    }
                }
                else {
                    System.exit(0);
                }

            }
            else {
                System.out.println("Wrong group");
                System.exit(0);
            }
        }
        else {
            System.out.println("Password mismatch");
            System.exit(0);
        }

    }

    //product list and call addOrder method
    private static void process(String process) {

        if (isDigit(process)) {

            products = _database.getProductNameList();

            System.out.println("----------------------------------------------------------");
            System.out.println("PLEASE SELECT PRODUCT:");

            for (int i = 0; i < products.size(); i++) {

                System.out.println(i + 1 + "  " + products.elementAt(i));

            }

            System.out.print("ENTER PRODUCT ID:");
            product_id = keyboard.nextLine();

            System.out.println("----------------------------------------------------------");

            System.out.print("ENTER PRODUCT LICENSE QUANTITY:");
            quantity = keyboard.nextLine();
            System.out.println();
            if (addNewOrder()) {
                System.out.println("NEW ORDER SUCCESSFULLY ADDED TO THE DATABASE");
            }
            System.out.println("----------------------------------------------------------");

        }
    }
    //check is variable digit or not
    private static boolean isDigit(String process) {

        @SuppressWarnings("unused")
        int i = 0;

        try {

            i = Integer.parseInt(process);
            return true;

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    //add new order
    private static boolean addNewOrder() {

        boolean b = false;

        try {

            long millis=System.currentTimeMillis();
            Date now = new Date(millis);

            int prod_id = Integer.parseInt(product_id);
            int quant = Integer.parseInt(quantity);
            approvalstatus = "PENDING";
            _database.addOrder(member_id, prod_id, quant, approvalstatus, now);
            b = true;
        }
        catch (Exception e) {
            b = false;
        }

        return b;
    }

    //get list of orders
    private static void getMyOrders(String approvalstatus) {

        Vector<?> v1 = new Vector<Object>();
        Vector<?> v2 = new Vector<Object>();
        Vector<?> v3 = new Vector<Object>();

        try {
            v1 = (Vector<?>) _database.getMyOrders(member_id, approvalstatus).elementAt(0);
            v2 = (Vector<?>) _database.getMyOrders(member_id, approvalstatus).elementAt(1);
            v3 = (Vector<?>) _database.getMyOrders(member_id, approvalstatus).elementAt(2);
            System.out.println("----------------------------------------------------------");
            System.out.println("TRANSACTION_ID | PRODUCT NAME | STATUS");
            for (int i = 0; i < v1.size(); i++) {

                System.out.println(v1.elementAt(i) + " | " + v2.elementAt(i) + " | " + v3.elementAt(i));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    //get list of ratings
    private static void getMyRatings() {

        Vector<?> v1 = new Vector<Object>();
        Vector<?> v2 = new Vector<Object>();

        try {
            v1 = (Vector<?>) _database.getMyRatings(member_id).elementAt(0);
            v2 = (Vector<?>) _database.getMyRatings(member_id).elementAt(1);
            System.out.println("----------------------------------------------------------");
            System.out.println("PRODUCT NAME | RATING");
            for (int i = 0; i < v1.size(); i++) {

                System.out.println(v1.elementAt(i) + " | " + v2.elementAt(i));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setRating() {
        System.out.println("----------------------------------------------------------");
        System.out.println("SELECT THE PRODUCT TO RATE");
        Vector<?> v1 = new Vector<Object>();
        Vector<?> v2 = new Vector<Object>();
        System.out.println();

        try {
            v1 = _database.getProductNameList();
            for (int i = 0; i < v1.size(); i++) {
                System.out.println(i + 1 + ". " + v1.elementAt(i));
            }
            System.out.println("----------------------------------------------------------");

            System.out.print("SELECT PRODUCT NUMBER:");
            setRating = keyboard.nextLine();
            v2 = _database.getRatingList();
            System.out.println();
            for (int i  = 0; i < v2.size(); i++) {
                System.out.println(i + 1 + ". " + v2.elementAt(i));
            }
            System.out.println("----------------------------------------------------------");

            System.out.print("SELECT RATING FROM THE LIST:");
            ratingvalue = keyboard.nextLine();

            if (isDigit(ratingvalue)) {
                int ratingvalueint = Integer.parseInt(ratingvalue);
                int prod_id = Integer.parseInt(setRating);
                _database.setRating(member_id, prod_id, ratingvalueint);
                System.out.println();
                System.out.println("RATING SUCCESSFULLY SAVED");
                System.exit(0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //add new comment to ordeer
    private static void addComment() {
        System.out.println("----------------------------------------------------------");
        System.out.println();
        products = _database.getProductNameList();
        for (int i = 0; i < products.size(); i++) {
            System.out.println(i + 1 + ". " + products.elementAt(i));
        }
        System.out.println("----------------------------------------------------------");
        System.out.print("SELECT PRODUCT TO ADD COMMENT:");
        product_id = keyboard.nextLine();
        System.out.print("ENTER COMMENT:");
        comment = keyboard.nextLine();
        int prod_id = Integer.parseInt(product_id);
        _database.addComment(member_id, prod_id, comment);
        System.out.println("COMMENT SUCCESSFULLY ADDED");
    }

    //get list of comments
    private static void getMyComments() {

        Vector<?> v1 = new Vector<Object>();
        Vector<?> v2 = new Vector<Object>();
        Vector<?> v3 = new Vector<Object>();

        try {
            v1 = (Vector<?>) _database.getMyComments(member_id).elementAt(0);
            v2 = (Vector<?>) _database.getMyComments(member_id).elementAt(1);
            v3 = (Vector<?>) _database.getMyComments(member_id).elementAt(2);
            System.out.println("----------------------------------------------------------");
            System.out.println("PRODUCT_ID | PRODUCT NAME | COMMENTS");
            for (int i = 0; i < v1.size(); i++) {

                System.out.println(v1.elementAt(i) + " | " + v2.elementAt(i) + " | " + v3.elementAt(i));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //set approve or reject of the new order
    @SuppressWarnings("static-access")
    private static void setApproveReject() {
        System.out.println();
        Vector<?> v1 = new Vector<Object>();
        Vector<?> v2 = new Vector<Object>();
        Vector<?> v3 = new Vector<Object>();

        try {
            v1 = (Vector<?>) _database.getPendingOrders().elementAt(0);
            v2 = (Vector<?>) _database.getPendingOrders().elementAt(1);
            v3 = (Vector<?>) _database.getPendingOrders().elementAt(2);
            System.out.println("----------------------------------------------------------");

            System.out.println("TRANSACTION_ID | PRODUCT NAME | STATUS");
            for (int i = 0; i < v1.size(); i++) {

                System.out.println(v1.elementAt(i) + " | " + v2.elementAt(i) + " | " + v3.elementAt(i));
            }
            System.out.println("----------------------------------------------------------");

            System.out.print("ENTER TRANSACTION ID TO CHANGE STATUS OF THE PRODUCT:");
            transaction_id = keyboard.nextLine();
            System.out.println("SELECT STATUS:");
            System.out.println("1. APPROVED");
            System.out.println("2. REJECTED");
            statusid = keyboard.nextLine();
            if (statusid.equals("1")) {

                int t_q = 0;
                int t_p = 0;
                int p_s = 0;
                t_q = _database.getTransactionStock(Integer.parseInt(transaction_id));
                t_p = _database.getTransactionProductId(Integer.parseInt(transaction_id));
                p_s = _database.getStock(t_p);
                if (t_q <= p_s) {
                    status = "APPROVED";
                    _database.updateStatus(status, Integer.parseInt(transaction_id));
                    System.out.println("SUCCESSFULLY APPROVED");
                    System.exit(0);
                }
                else {
                    System.out.println("QUANTITY OF THE PRODUCT LICENSE ORDERED BY CLIENT IS MORE THAN IN AVAILABLE STOCK IN WAREHOUSE");
                }
            }
            else if (statusid.equals("2")) {
                status = "REJECTED";
                _database.updateStatus(status, Integer.parseInt(transaction_id));
                System.out.println("SUCCESSFULLY REJECTED");
                System.exit(0);
            }
            else {
                System.exit(0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}

