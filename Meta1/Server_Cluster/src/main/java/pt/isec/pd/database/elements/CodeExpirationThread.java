package pt.isec.pd.database.elements;

import pt.isec.pd.Main_Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CodeExpirationThread extends Thread {
    private final Connection connection;

    public CodeExpirationThread(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        while(true){
            try {
                String selectSql = "SELECT id, fk_event, code, time_minutes FROM code_register";
                try (PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                     ResultSet resultSet = selectStatement.executeQuery()) {

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        int fkEvent = resultSet.getInt("fk_event");
                        int code = resultSet.getInt("code");
                        int timeMinutes = resultSet.getInt("time_minutes");

                        int updatedTimeMinutes = timeMinutes - 1;
                        updateCodeTime(id, updatedTimeMinutes);

                        if (updatedTimeMinutes == 0) {
                            deleteExpiredCode(id);
                            System.out.println("Expired code removed: " + code);
                        }
                    }
                }
               Thread.sleep(60000);
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCodeTime(int id, int updatedTimeMinutes) throws SQLException {
        String updateSql = "UPDATE code_register SET time_minutes = ? WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
            updateStatement.setInt(1, updatedTimeMinutes);
            updateStatement.setInt(2, id);
            updateStatement.executeUpdate();;
        }
    }

    private void deleteExpiredCode(int id) throws SQLException {
        String deleteSql = "DELETE FROM code_register WHERE id = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
            deleteStatement.setInt(1, id);
            deleteStatement.executeUpdate();
        }
    }
}
