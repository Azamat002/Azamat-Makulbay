package SoftwareStore;

import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

public class Settings
{
    private volatile static Settings _instance;
    private boolean _isValid = false;
    private String _database;
    private String user;
    private String password;

    public static synchronized Settings getInstance()
    {
        if (_instance == null)
        {
            _instance = new Settings();
        }

        return _instance;
    }
    //declare Settings constructor
    private Settings()
    {
        try
        {
            InputStream stream = getClass().getResourceAsStream("settings/settings.ini");

            Properties prop = new Properties();
            prop.load(stream);

            _database = prop.getProperty("PostgresDB");

            if (_database.isEmpty())
            {
                JOptionPane.showMessageDialog(null,"Can not load database properties");
                return;
            }

            user = prop.getProperty("username");

            if (user.isEmpty())
            {
                JOptionPane.showMessageDialog(null,"Can not load username");
                return;
            }

            password = prop.getProperty("password");

            if (password.isEmpty())
            {
                JOptionPane.showMessageDialog(null,"Can not load password");
                return;
            }

        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            return;
        }

        _isValid = true;
    }

    //check is settings.ini file loaded properly or not
    public boolean isValid()
    {
        return _isValid;
    }


    // get database path from settings.ini file
    public final String getStoreDatabaseName()
    {
        return _database;
    }

    //get username from settings.ini
    public final String getUserName()
    {
        return user;
    }

    //get password from settings.ini
    public final String getPassword()
    {
        return password;
    }
}





