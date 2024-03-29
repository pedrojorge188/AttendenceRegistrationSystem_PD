package pt.isec.pd;

import pt.isec.pd.Threads.ClientHandler;
import pt.isec.pd.Threads.HeartbeatHandler;
import pt.isec.pd.database.DatabaseManager;
import pt.isec.pd.rmi.GetRemoteFileService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main_Server  {
    public static void main(String[] args) {

        if (args.length != 4) {
            System.err.println("[REQUIRE] Java Main_Server <Port TCP> " +
                    "<SQLite Directory> " +
                    "<Name of RMI service> <" +
                    "Port of Registry RMI>");
            System.exit(1);
        }

        List<Thread> clientThreads = new ArrayList<>();

        int portTCP = Integer.parseInt(args[0]);
        String dbDirectory = args[1];
        String rmiServiceName = args[2];
        int rmiRegistryPort = Integer.parseInt(args[3]);
        GetRemoteFileService.createRMIService(rmiServiceName,rmiRegistryPort);
        try {
            DatabaseManager.getInstance();
            DatabaseManager.getInstance().setValues(dbDirectory, "ARSdatabase.sqlite");
            DatabaseManager.getInstance().connect();
        }catch (Exception e){
            System.err.println("DATABASE FAIL!");
        }
        try (ServerSocket serverSocket = new ServerSocket(portTCP)) {

            System.out.println("[Main] Server Ready at port : " + portTCP);

            //NOTA ! databaseVersion tem de ser uma variavel
            Thread heartbeatThread = new HeartbeatHandler(rmiRegistryPort, rmiServiceName, 1);
            heartbeatThread.start();

            while (true) {

                    Socket clientSocket = serverSocket.accept();

                    Thread clientThread = new Thread(new ClientHandler(clientSocket));
                    clientThreads.add(clientThread);
                    clientThread.start();

                    clientThreads.removeIf(thread -> !thread.isAlive());

            }

        } catch (Exception e) {
            e.printStackTrace();

        }finally{
            for (Thread i : clientThreads){
                try {
                    if (i.isAlive()) {
                        ((ClientHandler) i).getClientSocket().close();
                        ((ClientHandler) i).getInStream().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            DatabaseManager.getInstance().disconnect();
        }
    }


}