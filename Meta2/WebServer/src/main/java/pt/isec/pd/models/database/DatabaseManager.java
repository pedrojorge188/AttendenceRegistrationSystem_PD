package pt.isec.pd.models.database;

import pt.isec.pd.models.Event;
import pt.isec.pd.models.database.elements.CodeExpirationThread;
import pt.isec.pd.models.database.elements.Create;
import pt.isec.pd.models.database.elements.Version;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.*;

public class DatabaseManager{
    private static Boolean mutex = false;
    private String dbAddr;
    private String dbName;
    private CodeExpirationThread codeExpirationThread;
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
            try {
                Create.action(connection,dbURL);
                connection = DriverManager.getConnection(dbURL);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        codeExpirationThread = new CodeExpirationThread(connection);
        codeExpirationThread.start();
    }

    public synchronized String userExists(String username, String password) {
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

    public synchronized boolean userCreate(String name, int student_id, String username_email ,String password){

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


    public synchronized boolean changeUserAccount(String usernameOrEmail, String password, String newName) {
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

    public synchronized void disconnect(){
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean creatEvent(Event event){
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

    public synchronized boolean changeEvent(Event event) {
        if(!mutex) {
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
        return false;
    }

    public synchronized boolean deleteEvent(Event event) {
        if(!mutex){
            try {
                String checkEventExist = "SELECT id FROM events WHERE name = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkEventExist);
                checkStatement.setString(1, event.getEvent_name());

                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next()) {
                    int eventId = resultSet.getInt("id");

                    String checkUserEventExist = "SELECT COUNT(*) AS count FROM users_events WHERE fk_event = ?";
                    PreparedStatement checkUserEventStatement = connection.prepareStatement(checkUserEventExist);
                    checkUserEventStatement.setInt(1, eventId);

                    ResultSet userEventResultSet = checkUserEventStatement.executeQuery();
                    if (userEventResultSet.next()) {
                        int count = userEventResultSet.getInt("count");

                        if (count > 0) {
                            return false;
                        }
                    }

                    String deleteSQL = "DELETE FROM events WHERE id = ?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL);
                    deleteStatement.setInt(1, eventId);

                    int rowsAffected = deleteStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        Version.updateVersion(connection);
                        return true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public synchronized  boolean assocUserEvent(Event event) {
        if(!mutex) {
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
        return false;
    }
    public synchronized boolean csvAttendEvents(Event event, String defaultFileName) {
        File csvFile = new File(defaultFileName);

        try(FileWriter csvWriter = new FileWriter(csvFile,false)){

            String sql = "SELECT e.name, e.location, e.date, e.start_time, e.end_time, u.name, u.student_id, u.username_email" +
                    " FROM users u, events e, users_events lig WHERE lig.fk_event = e.id and lig.fk_user = u.id and e.name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, event.getEvent_name());
            ResultSet dataSet = statement.executeQuery();
            if(!dataSet.next())
                return false;

            csvWriter.write("\"Designação \";\"" + dataSet.getString(1) + "\n");
            csvWriter.write("\"Local \";\"" + dataSet.getString(2) + "\n");
            csvWriter.write("\"Data \";\"" + dataSet.getString(3) + "\n");
            csvWriter.write("\"Hora ínicio \";\"" + dataSet.getString(4) + "\n");
            csvWriter.write("\"Hora fim \";\"" + dataSet.getString(5) + "\n");
            csvWriter.write("\n");
            csvWriter.write("\"Nome\";\"Número identificação\";\"Email\"");csvWriter.write("\n");
            do {
                String uName = dataSet.getString(6);
                String uID = dataSet.getString(7);
                String uEmail = dataSet.getString(8);

                csvWriter.write("\""+uName + "\";\"" + uID + "\";\"" + uEmail + "\"\n" );
            } while (dataSet.next());

            return true;

        }catch (SQLException e){
            System.err.println("[ERROR] Database Manager -> " + e.getMessage());
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean verifyCode(Event event) {
        boolean codeExists = false;
        try {
            String sql = "SELECT COUNT(*) FROM code_register WHERE code = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, event.getAttend_code());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        codeExists = count > 0;
                    }
                }
            }

            if (codeExists) {
                if(registerUserEventRelationship(event)){
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return codeExists;
    }

    private synchronized boolean registerUserEventRelationship(Event event) throws SQLException {
        if(!mutex){
            int userId = getUserIdByUsernameOrEmail(event.getUser_email());
            int eventId = determineFkEventFromCodeRegister(event);
            if(userId == -1|| userId == -1)
                return false;

            if (userId != -1) {
                String insertSql = "INSERT INTO users_events (fk_event, fk_user, attendance) VALUES (?, ?, 1)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setInt(1, eventId); // Assuming getFk_event() retrieves the event ID
                    insertStatement.setInt(2, userId);

                    insertStatement.executeUpdate();
                }
            }

            return true;
        }
        return false;
    }

    private synchronized int getUserIdByUsernameOrEmail(String username) throws SQLException {

        String selectSql = "SELECT id FROM users WHERE username_email = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
            selectStatement.setString(1, username);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return -1;
    }

    private synchronized int determineFkEventFromCodeRegister(Event event) throws SQLException {

        String codeRegisterSql = "SELECT fk_event FROM code_register WHERE code = ?";
        try (PreparedStatement codeRegisterStatement = connection.prepareStatement(codeRegisterSql)) {
            codeRegisterStatement.setInt(1, event.getAttend_code());

            try (ResultSet codeRegisterResultSet = codeRegisterStatement.executeQuery()) {
                if (codeRegisterResultSet.next()) {
                    return codeRegisterResultSet.getInt("fk_event");
                }
            }
        }

        return -1;
    }

    public synchronized boolean csvUserEvents(Event event, String defaultFileName){
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

    public synchronized boolean sendCSVFile(String path, Socket socket) {
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

    public synchronized boolean generateCode(Event event) {
        if(!mutex) {

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
                } else
                    return false;

            } catch (SQLException e) {
                System.err.println("[ERROR] Database Manager -> " + e.getMessage());
                return false;
            }
        }
        return false;
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

    public synchronized String getDbAddr() {
        return dbAddr;
    }

    public synchronized String getDbName() {
        return dbName;
    }

    public synchronized Connection getConnection() {
        return connection;
    }
    public synchronized List<String> getAllEvents(){
        List<String> eventNames = new ArrayList<>();
        try {
            String sql = "SELECT * FROM events";
            PreparedStatement statement = connection.prepareStatement(sql);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    eventNames.add(resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                            + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5));
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return eventNames;

    }

    public synchronized List<String> getCreatedEvents(Event event) {
        List<String> eventNames = new ArrayList<>();
        try {
            String sql = "SELECT name, location, events.date, Start_time, end_time FROM events";
            String whereClause = "";

            if (event.getEvent_name() != null && event.getEvent_start_time() != null
                    && !event.getEvent_name().isEmpty() && !event.getEvent_start_time().isEmpty()) {
                whereClause = " WHERE name = ? AND start_time = ?";
            } else if (event.getEvent_name() != null && event.getEvent_end_time() != null
                    && !event.getEvent_name().isEmpty() && (event.getEvent_end_time().isEmpty() || event.getEvent_end_time().equals(""))) {
                whereClause = " WHERE name = ?";
            }

            sql += whereClause;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if (!whereClause.isEmpty()) {
                    int parameterIndex = 1;
                    if (event.getEvent_name() != null && event.getEvent_start_time() != null
                            && !event.getEvent_name().isEmpty() && !event.getEvent_start_time().isEmpty()) {
                        statement.setString(parameterIndex++, event.getEvent_name());
                        statement.setString(parameterIndex, event.getEvent_start_time());
                    } else if (event.getEvent_name() != null && event.getEvent_end_time() != null
                            && !event.getEvent_name().isEmpty() && (event.getEvent_end_time().isEmpty() || event.getEvent_end_time().equals(""))) {
                        statement.setString(parameterIndex, event.getEvent_name());
                    }
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        eventNames.add(resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                                + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventNames;
    }

    public synchronized List<String> getAttendance(Event event) {
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

    public synchronized List<String> getAllAttendances() {
        List<String> allAttendances = new ArrayList<>();
        try {
            String sql = "SELECT users.name, users.username_email " +
                    "FROM users_events " +
                    "INNER JOIN users ON users_events.fk_user = users.id";

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("username_email");
                allAttendances.add(name + "\t" + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allAttendances;
    }


    public synchronized boolean insertAttendance(Event event) {
        if(!mutex) {

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
        return false;
    }

    public synchronized boolean deleteAttendance(Event event) {
        if(!mutex){
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
        return false;
    }

    public synchronized List<String> getUserAttendance(Event event) {
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