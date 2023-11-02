package pt.isec.pd.helpers;

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


