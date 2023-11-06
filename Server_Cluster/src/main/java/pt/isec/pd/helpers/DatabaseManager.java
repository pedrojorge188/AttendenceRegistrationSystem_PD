package pt.isec.pd.helpers;

import pt.isec.pd.Threads.HeartbeatHandler;
import pt.isec.pd.data.Event;
import pt.isec.pd.data.InfoStatus;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            updateVersion();
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
            checkStatement.setString(1, event.getEvent_identify());
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
            updateVersion();
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

                deleteSQL = "DELETE FROM users_events WHERE fk_event = ?";
                deleteStatement = connection.prepareStatement(deleteSQL);
                deleteStatement.setInt(1, eventId);
                deleteStatement.executeUpdate();
                updateVersion();
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

    public boolean assocUserEvent(Event event) {
        int eventId, userId;
        try {
            String checkEventExist = "SELECT id FROM events WHERE name = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkEventExist);
            checkStatement.setString(1, event.getEvent_name());

            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next())
                eventId = resultSet.getInt("id");
            else
                return false;

            checkEventExist = "SELECT id FROM users WHERE username_email = ?";
            checkStatement = connection.prepareStatement(checkEventExist);
            checkStatement.setString(1, event.getEvent_identify());

            resultSet = checkStatement.executeQuery();

            if (resultSet.next())
                userId = resultSet.getInt("id");
            else
                return false;

            String sql = "INSERT INTO users_events (fk_user,fk_event,attendance) VALUES (?, ?, 0)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, eventId);

            int rowsInserted = statement.executeUpdate();
            updateVersion();
            if (rowsInserted > 0)
                return true;
            else
                return false;

        } catch (SQLException e) {
            System.err.println("[ERROR] Database Manager -> " + e.getMessage());
            return false;
        }
    }

    public boolean csvUserEvents(Event event, String defaultFileName){
        File csvFile = new File(defaultFileName);
        int user_id;

        try(FileWriter csvWriter = new FileWriter(csvFile,false)){

            String sql = "SELECT u.name, u.student_id, u.username_email, e.name, e.location, e.user_email, e.date, e.Start_time" +
                    " FROM users u, events e WHERE u.id = u.id and username_email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, event.getUser_email());
            ResultSet dataSet = statement.executeQuery();
            if(!dataSet.next())
                return false;

            csvWriter.write("\"Nome\";\"Número identificação\";\"Email\"");
            csvWriter.write("\n\n");
            csvWriter.write("\"" + dataSet.getString(1) + "\";\"" +
                            dataSet.getInt(2) + "\";\"" +
                            dataSet.getString(3) + "\"");
            csvWriter.write("\n\n");
            csvWriter.write("\"Designação\";\"Local\";\"Data\";\"Hora ínicio\"");
            csvWriter.write("\n");

            do {
                String eventName = dataSet.getString(4);
                String eventLocation = dataSet.getString(5);
                String eventDate = dataSet.getString(6);
                String eventStartTime = dataSet.getString(7);

                csvWriter.write(eventName + "\";\"" + eventLocation + "\";\"" + eventDate + "\";\"" + eventStartTime + "\"");
                csvWriter.write("\n");
            } while (dataSet.next());

            return true;

        }catch (SQLException e){
            System.err.println("[ERROR] Database Manager -> " + e.getMessage());
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendFile(String path, Socket socket) {
        byte[] fileChunk = new byte[5000];
        int nbytes;
        int totalBytes = 0;
        int nChunks = 0;
        try {
            String requestedCanonicalFilePath = new File(path).getCanonicalPath();
            try (InputStream requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath)) {
                System.out.println("Ficheiro " + requestedCanonicalFilePath + " aberto para leitura.");
                OutputStream out = socket.getOutputStream();

                do {
                    nbytes = requestedFileInputStream.read(fileChunk);
                    System.out.println(nbytes);

                    if (nbytes > 0) {
                        out.write(fileChunk, 0, nbytes);
                        out.flush();
                        totalBytes += nbytes;
                        nChunks++;
                    }
                } while (nbytes > 0);

                System.out.format("(CSV File Sent)(%d bytes)\r\n", nChunks);
                File csvFile = new File(path);
                csvFile.delete();
            }

            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public List<String> getCreatedEvents() {

        List<String> eventNames = new ArrayList<>();
        try {
            String sql = "SELECT name FROM events";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String eventName = resultSet.getString("name");
                eventNames.add(eventName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventNames;
    }

}


