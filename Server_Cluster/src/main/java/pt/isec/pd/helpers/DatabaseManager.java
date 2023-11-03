package pt.isec.pd.helpers;

import pt.isec.pd.Threads.HeartbeatHandler;

import java.io.File;
import java.sql.*;
import java.net.*;
public class DatabaseManager {

    private String dbAddr;
    private String dbName;
    private Connection connection;
    private DatabaseManager(){}

    public void setValues(String dbAddr, String dbName){
        this.dbAddr = dbAddr; this.dbName = dbName;
        System.out.println("__________________________________________________________________");
        System.out.println("Data base values started \n.Directory -> " +this.dbAddr +"  \n.Name -> "+ this.dbName );
        System.out.println("__________________________________________________________________");
    }


    private static DatabaseManager instance = null;
    public static DatabaseManager getInstance(){
        if(instance == null)
            instance = new DatabaseManager();
        return instance;
    }


    public void connect() {

        if (dbAddr == null || dbName == null) {
            System.err.println("Database address and name not set. Call setValues() first.");
            return;
        }

        if((new File(this.dbAddr + this.dbName)).exists()){
            try{
                connection = DriverManager.getConnection("jdbc:sqlite:" + dbAddr  + dbName);
                System.out.println("[SERVER] Database connection made!");
            }catch (Exception e){
                System.err.println("[ERROR - DATABASE]" );e.printStackTrace();
            }
        }


    }

    public String userExists(String username, String password) {
        try {

            String sql = "SELECT role FROM users WHERE username_email = ? and password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getString("role");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Database Manager -> "+ e.getMessage());
        }

        return "ANY";
    }

    public boolean userCreate(String name, int student_id, String username_email ,String password){

        if(!userExists(username_email,password).equals("ANY"))
            return false;

        try {
            String sql = "INSERT INTO users (username_email, password, role, name, student_id) VALUES (?, ?, 'normal', ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username_email);
            statement.setString(2, password);
            statement.setString(3, name);
            statement.setInt(4, student_id);
            int rowsInserted = statement.executeUpdate();
            updateVersion();
            if (rowsInserted > 0)
                return true;
            else
               return false;

        } catch (SQLException e) {
            System.err.println("[ERROR] Database Manager -> "+ e.getMessage());
            return false;
        }

    }

    public int getVersion(){
        try{

            String sql = "SELECT version_serial FROM version WHERE id = 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            return  statement.executeQuery().getInt(1);

        }catch (SQLException e){
            System.err.println("[ERROR] Database Manager ->" + e.getMessage());
            return -1;
        }
    }

    private void updateVersion(){
        try{

            String sql = "UPDATE version SET version_serial = "+(getVersion()+1)+" WHERE id = 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            HeartbeatHandler.sendHb();

        }catch (SQLException e){
            System.err.println("[ERROR] Database Manager ->" + e.getMessage());
        }
    }

    public void disconnect(){
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDbAddr() {
        return dbAddr;
    }

    public String getDbName() {
        return dbName;
    }

    public Connection getConnection() {
        return connection;
    }
}


