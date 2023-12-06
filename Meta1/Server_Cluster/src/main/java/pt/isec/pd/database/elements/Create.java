package pt.isec.pd.database.elements;

import java.sql.*;

public class Create {
    public static void action(Connection connection, String dbURL){
        try {
            connection = DriverManager.getConnection(dbURL);
            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS events (" +
                    "name TEXT, " +
                    "date DATE, " +
                    "start_time REAL, " +
                    "end_time REAL, " +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "user_email TEXT, " +
                    "location TEXT)");

            statement.execute("CREATE TABLE IF NOT EXISTS code_register (" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "fk_event INTEGER NOT NULL, " +
                    "code INTEGER, " +
                    "time_minutes INT, " +
                    "FOREIGN KEY (fk_event) REFERENCES events(id))");

            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "username_email TEXT, " +
                    "password TEXT, " +
                    "role TEXT, " +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "student_id INTEGER)");

            statement.execute("CREATE TABLE IF NOT EXISTS users_dg_tmp (" +
                    "username_email TEXT, " +
                    "password TEXT, " +
                    "role TEXT, " +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "student_id INTEGER)");

            statement.execute("CREATE TABLE IF NOT EXISTS users_events (" +
                    "fk_user INTEGER NOT NULL, " +
                    "fk_event INTEGER NOT NULL, " +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "attendance INTEGER, " +
                    "FOREIGN KEY (fk_user) REFERENCES users(id), " +
                    "FOREIGN KEY (fk_event) REFERENCES events(id))");

            statement.execute("CREATE TABLE IF NOT EXISTS version (" +
                    "version_serial INTEGER, " +
                    "id INTEGER NOT NULL PRIMARY KEY)");

        }catch (SQLException e){
            System.err.println("[ERROR - DATABASE]" );e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(dbURL);
            System.out.println("[SERVER] New database created!");

            String sql = "INSERT INTO version (version_serial) VALUES (0)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            sql = "INSERT INTO users (username_email, name, role, password) VALUES ('admin@admin','admin','admin','admin')";
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (Exception e) {
            System.err.println("[ERROR - DATABASE]");
            e.printStackTrace();
        }
    }
}
