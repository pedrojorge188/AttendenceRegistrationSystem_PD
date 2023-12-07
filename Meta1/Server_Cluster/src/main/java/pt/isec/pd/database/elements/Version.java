package pt.isec.pd.database.elements;

import pt.isec.pd.Threads.HeartbeatHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Version {

    public static int getVersion(Connection connection){
        try{

            String sql = "SELECT version_serial FROM version WHERE id = 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            return  statement.executeQuery().getInt(1);

        }catch (SQLException e){
            System.err.println("[ERROR] Database Manager ->" + e.getMessage());
            return -1;
        }
    }

    public static void updateVersion(Connection connection){
        try{

            String sql = "UPDATE version SET version_serial = "+(getVersion(connection)+1)+" WHERE id = 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            HeartbeatHandler.sendHb();

        }catch (SQLException e){
            System.err.println("[ERROR] Database Manager ->" + e.getMessage());
        }
    }

}
