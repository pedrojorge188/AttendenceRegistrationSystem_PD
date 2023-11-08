package pt.isec.pd.database;

import pt.isec.pd.Threads.HeartbeatHandler;
import pt.isec.pd.data.Event;
import pt.isec.pd.database.elements.Create;
import pt.isec.pd.database.elements.Version;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.*;

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

        String dbURL = "jdbc:sqlite:" + dbAddr  + dbName;
        if((new File(this.dbAddr + this.dbName)).exists()){

            try{
                connection = DriverManager.getConnection(dbURL);
                System.out.println("[SERVER] Database connection made!");
            }catch (Exception e){
                System.err.println("[ERROR - DATABASE]" );e.printStackTrace();
            }

        }else{
            Create.action(connection,dbURL);
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
            Version.updateVersion(connection);
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
            Version.updateVersion(connection);
            if (rowsAffected > 0)
                return true;
            else
                return false;

        } catch (SQLException e) {
            System.err.println("[ERROR] Database Manager -> " + e.getMessage());
            return false;
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
            Version.updateVersion(connection);
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
            Version.updateVersion(connection);
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
                Version.updateVersion(connection);
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
            Version.updateVersion(connection);
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
                    " FROM users u, events e, users_events lig WHERE lig.fk_event = e.id and lig.fk_user = u.id and username_email = ?";
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

    public boolean sendCSVFile(String path, Socket socket) {
        byte[] fileChunk = new byte[5000];
        int nbytes;
        int totalBytes = 0;
        int nChunks = 0;
        try {
            String requestedCanonicalFilePath = new File(path).getCanonicalPath();
            try (InputStream requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath)) {

                OutputStream out = socket.getOutputStream();

                do {
                    nbytes = requestedFileInputStream.read(fileChunk);

                    if (nbytes > -1) {
                        out.write(fileChunk, 0, nbytes);
                        out.flush();
                        totalBytes += nbytes;
                        nChunks++;
                    }
                } while (nbytes > 0);


            }
            if(new File(path).exists())
                new File(path).delete();

            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean generateCode(Event event) {
        int code = generateRandomCode();
        int eventId;
        try {
            String checkEventExist = "SELECT id FROM events WHERE name = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkEventExist);
            checkStatement.setString(1, event.getEvent_name());

            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next())
                eventId = resultSet.getInt("id");
            else
                return false;

            String checkExistingCodeSql = "SELECT code FROM code_register WHERE fk_event = ?";
            checkStatement = connection.prepareStatement(checkExistingCodeSql);
            checkStatement.setInt(1, eventId);
            resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                int existingCode = resultSet.getInt("code");
                String deleteExistingCodeSql = "DELETE FROM code_register WHERE fk_event = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteExistingCodeSql);
                deleteStatement.setInt(1, eventId);
                deleteStatement.executeUpdate();
            }

            String sql = "INSERT INTO code_register (code,time_minutes,fk_event) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, code);
            statement.setInt(2, Integer.parseInt(event.getEvent_end_time()));
            statement.setInt(3, eventId);

            int rowsInserted = statement.executeUpdate();
            Version.updateVersion(connection);

            if (rowsInserted > 0) {
                event.setAttend_code(code);
                return true;
            }else
                return false;

        } catch (SQLException e) {
            System.err.println("[ERROR] Database Manager -> " + e.getMessage());
            return false;
        }
    }

    private int generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
        }

        String checkCodeExists = "SELECT id FROM code_register WHERE code = ?";
        try {
            PreparedStatement checkStatement = checkStatement = connection.prepareStatement(checkCodeExists);
            checkStatement.setInt(1, Integer.parseInt(code.toString()));

            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next())
                generateRandomCode();

        } catch (SQLException e) {
            System.err.println("[ERROR] Database Manager -> " + e.getMessage());
            return 0;
        }

        return Integer.parseInt(code.toString());
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
            String sql = "SELECT name, location, events.date , Start_time, end_time FROM events";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                eventNames.add(resultSet.getString(1) + "\t"+ resultSet.getString(2) + "\t"+ resultSet.getString(3)
                        +"\t"+ resultSet.getString(4)+ "\t"+ resultSet.getString(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventNames;
    }
    public List<String> getAttendance(Event event) {
        int eventId;
        List<String> attendance = new ArrayList<>();
        try {
            // Primeiro, obter o ID do evento com o nome desejado
            String eventIdQuery = "SELECT id FROM events WHERE name = ?";
            PreparedStatement eventIdStatement = connection.prepareStatement(eventIdQuery);
            eventIdStatement.setString(1, event.getEvent_name());
            ResultSet eventIdResult = eventIdStatement.executeQuery();

            if (eventIdResult.next()) {
                eventId = eventIdResult.getInt("id");

                String sql = "SELECT users.name, users.username_email " +
                        "FROM users_events " +
                        "INNER JOIN users ON users_events.fk_user = users.id " +
                        "WHERE fk_event = ? AND attendance = 1";

                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, eventId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("username_email");
                    attendance.add(name + "\t" + email);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendance;
    }

    public boolean insertAttendance(Event event) {
        int eventId, userId;
        try {
            String checkEventAndUserExist = "SELECT e.id AS event_id, u.id AS user_id " +
                    "FROM events e " +
                    "INNER JOIN users u ON e.name = ? AND u.username_email = ?";

            PreparedStatement checkStatement = connection.prepareStatement(checkEventAndUserExist);
            checkStatement.setString(1, event.getEvent_name());
            checkStatement.setString(2, event.getUser_email());

            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                eventId = resultSet.getInt("event_id");
                userId = resultSet.getInt("user_id");
            } else {
                return false;
            }

            String sql = "SELECT attendance FROM users_events WHERE fk_user = ? and fk_event = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, eventId);

            ResultSet attendanceResultSet = statement.executeQuery();

            if (attendanceResultSet.next()) {
                int attendance = attendanceResultSet.getInt("attendance");
                if (attendance == 1) {
                    return false;
                } else if (attendance == 0) {
                    sql = "UPDATE users_events SET attendance = 1 WHERE fk_user = ? and fk_event = ?";
                    statement = connection.prepareStatement(sql);
                    statement.setInt(1, userId);
                    statement.setInt(2, eventId);
                }
            } else {
                sql = "INSERT INTO users_events (fk_user, fk_event, attendance) VALUES (?, ?, 1)";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, userId);
                statement.setInt(2, eventId);
            }

            int rowsInserted = statement.executeUpdate();
            Version.updateVersion(connection);

            if (rowsInserted > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Database Manager -> " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAttendance(Event event) {
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
            checkStatement.setString(1, event.getUser_email());

            resultSet = checkStatement.executeQuery();

            if (resultSet.next())
                userId = resultSet.getInt("id");
            else
                return false;

            String sql = "UPDATE users_events SET attendance = 0 WHERE fk_user = ? and fk_event = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, eventId);

            int rowsInserted = statement.executeUpdate();
            Version.updateVersion(connection);

            if (rowsInserted > 0) {
                return true;
            } else
                return false;

        } catch (SQLException e) {
            System.err.println("[ERROR] Database Manager -> " + e.getMessage());
            return false;
        }
    }

    public List<String> getUserAttendance(Event event) {
        List<String> eventsList = new ArrayList<>();
        try {
            // Primeiro, obtenha o ID do usuário com o email desejado
            String userIdQuery = "SELECT id FROM users WHERE username_email = ?";
            PreparedStatement userIdStatement = connection.prepareStatement(userIdQuery);
            userIdStatement.setString(1, event.getUser_email());
            ResultSet userIdResult = userIdStatement.executeQuery();

            if (userIdResult.next()) {
                int userId = userIdResult.getInt("id");

                String sql = "SELECT events.name, events.start_time, events.end_time, events.date " +
                        "FROM users_events " +
                        "INNER JOIN events ON users_events.fk_event = events.id " +
                        "WHERE fk_user = ? AND attendance = 1";


                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String startTime = resultSet.getString("start_time");
                    String endTime = resultSet.getString("end_time");
                    String date = resultSet.getString("date");
                    eventsList.add(name + "\t" + startTime + "\t" + endTime + "\t" + date);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventsList;
    }
}

