package pt.isec.pd.helpers;

import pt.isec.pd.Threads.HeartbeatHandler;
import pt.isec.pd.data.Event;

import java.io.File;
import java.sql.*;

public class DatabaseManager{
    private String dbAddr;
    private String dbName;
    private Connection connection;
    private DatabaseManager(){
    }

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

    public boolean changeUserAccount(String usernameOrEmail, String password, String newName) {
        try {

            String checkUserExistsSQL = "SELECT id FROM users WHERE username_email = ?";

            PreparedStatement checkStatement = connection.prepareStatement(checkUserExistsSQL);
            checkStatement.setString(1, newName);
            ResultSet resultSet = checkStatement.executeQuery();

            if (!resultSet.next())
                return false;

            String updateSQL = "UPDATE users SET username_email = ?, password = ? WHERE id = ?";

            PreparedStatement updateStatement = connection.prepareStatement(updateSQL);
            updateStatement.setString(1, usernameOrEmail);
            updateStatement.setString(2, password);
            updateStatement.setInt(3, resultSet.getInt("id"));

            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0)
                return true;
             else
                return false;

        } catch (SQLException e) {
            System.err.println("[ERROR] Database Manager -> " + e.getMessage());
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

    public boolean creatEvent(Event event){
        try {
            String sql = "INSERT INTO events (name, location, Start_time, end_time, date,user_email) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, event.getEvent_name());
            statement.setString(2, event.getEvent_location());
            statement.setString(3, event.getEvent_start_time());
            statement.setString(4, event.getEvent_end_time());
            statement.setString(5, event.getEvent_date());
            statement.setString(6, event.getUser_email());

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

    public boolean changeEvent(Event event) {
        try {

            String checkEventExist = "SELECT id FROM events WHERE name = ?";

            PreparedStatement checkStatement = connection.prepareStatement(checkEventExist);
            checkStatement.setString(1, event.getEvent_name());
            ResultSet resultSet = checkStatement.executeQuery();

            if (!resultSet.next())
                return false;

            String updateSQL = "UPDATE events SET name = ?, location = ? , Start_time = ?, end_time = ?, date = ? WHERE id = ?";

            PreparedStatement updateStatement = connection.prepareStatement(updateSQL);
            updateStatement.setString(1, event.getEvent_name());
            updateStatement.setString(2, event.getEvent_location());
            updateStatement.setString(3, event.getEvent_start_time());
            updateStatement.setString(4, event.getEvent_end_time());
            updateStatement.setString(5, event.getEvent_date());
            updateStatement.setInt(6, resultSet.getInt("id"));

            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0)
                return true;
            else
                return false;

        } catch (SQLException e) {
            System.err.println("[ERROR] Database Manager -> " + e.getMessage());
            return false;
        }
    }

    public boolean deleteEvent(Event event) {
        try {
            String checkEventExist = "SELECT id FROM events WHERE name = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkEventExist);
            checkStatement.setString(1, event.getEvent_name());

            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                int eventId = resultSet.getInt("id");

                String deleteSQL = "DELETE FROM events WHERE id = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL);
                deleteStatement.setInt(1, eventId);

                int rowsAffected = deleteStatement.executeUpdate();

                if (rowsAffected > 0) {
                    return true;
                }
            }

            return false;
        } catch (SQLException e) {
            System.err.println("[ERROR] Database Manager -> " + e.getMessage());
            return false;
        }
    }

    public boolean generateCode(Event event) {
        return false;
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


