package SoftwareStore;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Properties;
import java.util.Vector;

import javax.swing.JOptionPane;

public class database {

    Settings _settings = Settings.getInstance();

    static Connection _connection = null;
    static Statement _statement = null;
    static PreparedStatement _preparestatement = null;
    private volatile static database _instance;

    public static synchronized database getInstance()
    {
        if (_instance == null)
        {
            _instance = new database();
        }

        return _instance;
    }

    //create new constructor database
    database()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch(ClassNotFoundException e)
        {
            JOptionPane.showMessageDialog(null, e.toString());
            System.out.println(e.getMessage());
            return;
        }

        try
        {
            Properties connInfo = new Properties();
            connInfo.put("", "");
            connInfo.put("","");
            connInfo.put("charSet", "cp1251");

            String USER = _settings.getUserName();
            String PASS = _settings.getPassword();

            _connection = DriverManager.getConnection(_settings.getStoreDatabaseName(), USER, PASS);

            _statement = _connection.createStatement();
            _statement.setQueryTimeout(30);
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, "Error during connection to PostgreSQL");
            System.exit(0);
            System.out.println(e.getMessage());

            try
            {
                if (_connection != null)
                {
                    _connection.close();
                }
            }
            catch(SQLException e2)
            {
                JOptionPane.showMessageDialog(null, e2.toString());
            }

            return;
        }
    }

    //get list of products from product table of database
    public Vector<String> getProductNameList() {
        Vector<String> v = new Vector<String>();
        String SQL = "";
        try {

            SQL = "SELECT productname FROM Product ORDER BY product_id";

            ResultSet rs = _statement.executeQuery(SQL);
            while (rs.next()) {
                v.addElement(rs.getString(1));
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error in getProductName");
            System.exit(0);
            e.printStackTrace();
            return null;
        }
        return v;
    }

    //get group, password and id of username from database member table
    public String[] getPasswordGroupId(String login) {
        String[] str = new String[3];

        String SQL = "SELECT member_id, password, user_group FROM Member WHERE username = '" + login + "'";

        try {
            ResultSet rs = _statement.executeQuery(SQL);
            while (rs.next()) {
                str[0] = rs.getString("password");
                str[1] = rs.getString("user_group");
                str[2] = Integer.toString(rs.getInt("member_id"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return str;
    }

    //add new order to the database transactions table
    public boolean addOrder(
            int MEMBER_ID,
            int PRODUCT_ID,
            int QUANTITY,
            String STATUS,
            Date TRANSACTION_DATE) {

        boolean isAdded = false;

        try {
            _preparestatement = _connection.prepareStatement("call public.new_order(?,?,?,?,?)");
            _preparestatement.setInt(1, MEMBER_ID);
            _preparestatement.setInt(2, PRODUCT_ID);
            _preparestatement.setInt(3, QUANTITY);
            _preparestatement.setString(4, STATUS);
            _preparestatement.setDate(5, TRANSACTION_DATE);
            _preparestatement.execute();

            isAdded = true;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return isAdded;
    }

    //get list of orders from database
    @SuppressWarnings("rawtypes")
    public Vector<Vector<Comparable>> getMyOrders (int member_id, String approvalstatus) {
        Vector<Vector<Comparable>> v = new Vector<Vector<Comparable>>();
        Vector<Comparable> v1 = new Vector<Comparable>();
        Vector<Comparable> v2 = new Vector<Comparable>();
        Vector<Comparable> v3 = new Vector<Comparable>();
        String SQL = "select t.transaction_id, p.productname, t.approvalstatus from "
                + "transactions t inner join product p on t.product_id = p.product_id "
                + "and t.member_id = " + member_id + " and t.approvalstatus = '" + approvalstatus + "'";
        try {
            ResultSet rs = _statement.executeQuery(SQL);
            while (rs.next()) {
                v1.addElement(rs.getInt(1));
                v2.addElement(rs.getString(2));
                v3.addElement(rs.getString(3));
                v.addElement(v1);
                v.addElement(v2);
                v.addElement(v3);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return v;
    }

    //get list of ratings from database
    @SuppressWarnings("rawtypes")
    public Vector<Vector<Comparable>> getMyRatings (int member_id) {
        Vector<Vector<Comparable>> v = new Vector<Vector<Comparable>>();
        Vector<Comparable> v1 = new Vector<Comparable>();
        Vector<Comparable> v2 = new Vector<Comparable>();
        String SQL = "select p.productname, rt.description from rating r "
                + "inner join ratingtype rt on r.value = rt.valueid "
                + "inner join product p on r.product_id = p.product_id "
                + "and r.member_id = " + member_id;
        try {
            ResultSet rs = _statement.executeQuery(SQL);
            while (rs.next()) {
                v1.addElement(rs.getString(1));
                v2.addElement(rs.getString(2));
                v.addElement(v1);
                v.addElement(v2);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return v;
    }

    //get description of ratings from database ratingtype table
    public Vector<String> getRatingList() {
        Vector<String> v = new Vector<String>();
        try {
            ResultSet rs = _statement.executeQuery("SELECT description FROM ratingtype");
            while (rs.next()) {
                v.addElement(rs.getString("description"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return v;
    }

    public void setRating(
            int MEMBER_ID,
            int PRODUCT_ID,
            int VALUE) {


        try {
            _preparestatement = _connection.prepareStatement("call public.new_rating(?,?,?)");
            _preparestatement.setInt(1, MEMBER_ID);
            _preparestatement.setInt(2, PRODUCT_ID);
            _preparestatement.setInt(3, VALUE);
            _preparestatement.execute();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //add comments to the product in database
    public void addComment(
            int MEMBER_ID,
            int PRODUCT_ID,
            String COMMENT) {


        try {
            _preparestatement = _connection.prepareStatement("call public.new_comment(?,?,?)");
            _preparestatement.setInt(1, MEMBER_ID);
            _preparestatement.setInt(2, PRODUCT_ID);
            _preparestatement.setString(3, COMMENT);
            _preparestatement.execute();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //get comments of products from the database
    @SuppressWarnings("rawtypes")
    public Vector<Vector<Comparable>> getMyComments (int member_id) {
        Vector<Vector<Comparable>> v = new Vector<Vector<Comparable>>();
        Vector<Comparable> v1 = new Vector<Comparable>();
        Vector<Comparable> v2 = new Vector<Comparable>();
        Vector<Comparable> v3 = new Vector<Comparable>();
        String SQL = "select c.product_id, p.productname, c.message from comments c "
                + "inner join product p on c.product_id = p.product_id and member_id = " + member_id;
        try {
            ResultSet rs = _statement.executeQuery(SQL);
            while (rs.next()) {
                v1.addElement(rs.getInt(1));
                v2.addElement(rs.getString(2));
                v3.addElement(rs.getString(3));
                v.addElement(v1);
                v.addElement(v2);
                v.addElement(v3);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return v;
    }


    //get peding orders from database transactions, products tables
    @SuppressWarnings("rawtypes")
    public Vector<Vector<Comparable>> getPendingOrders () {
        Vector<Vector<Comparable>> v = new Vector<Vector<Comparable>>();
        Vector<Comparable> v1 = new Vector<Comparable>();
        Vector<Comparable> v2 = new Vector<Comparable>();
        Vector<Comparable> v3 = new Vector<Comparable>();
        String SQL = "select t.transaction_id, p.productname, t.approvalstatus from transactions t "
                + "inner join product p on t.product_id = p.product_id and t.approvalstatus = 'PENDING'";
        try {
            ResultSet rs = _statement.executeQuery(SQL);
            while (rs.next()) {
                v1.addElement(rs.getInt(1));
                v2.addElement(rs.getString(2));
                v3.addElement(rs.getString(3));
                v.addElement(v1);
                v.addElement(v2);
                v.addElement(v3);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return v;
    }

    //get stock from database product table
    public int getStock(int product_id) {
        int stock = 0;
        try {
            ResultSet rs = _statement.executeQuery("select stock from product where product_id = " + product_id);
            while (rs.next()) {
                stock = rs.getInt("stock");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return stock;
    }


    //get stock from transactions table
    public int getTransactionStock(int transaction_id) {
        int quantity = 0;
        try {
            ResultSet rs = _statement.executeQuery("select quantity from transactions where transaction_id = " + transaction_id);
            while (rs.next()) {
                quantity = rs.getInt("quantity");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return quantity;

    }

    //get product id from transactions table
    public int getTransactionProductId(int transaction_id) {
        int product_id = 0;
        try {
            ResultSet rs = _statement.executeQuery("select product_id from transactions where transaction_id = " + transaction_id);
            while (rs.next()) {
                product_id = rs.getInt("product_id");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return product_id;
    }

    //update status of order from peding to approve or reject
    public static void updateStatus(String STATUS, int transaction_id) {
        try {
            String SQL = "UPDATE Transactions SET approvalstatus = '" + STATUS + "' WHERE transaction_id = " + transaction_id;
            _statement.executeUpdate(SQL);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

