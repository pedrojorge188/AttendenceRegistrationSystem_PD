package pt.isec.pd;


import pt.isec.pd.heartbeat.HeartbeatSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main_Server {

    private static class ClientHandler extends Thread {

        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {

            try {

                System.out.println("[Client "+this.getName()+"-] Connected (ip:"+clientSocket.getInetAddress()+" | port: "+clientSocket.getPort()
                +")");
                /*Data process*/

            } catch (Exception e) {
                e.printStackTrace();


            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

        if (args.length != 4) {
            System.err.println("[REQUIRE] Java Main_Server <Port TCP> <SQLite Directory> <Name of RMI service> <Port of Registry RMI>");
            System.exit(1);
        }

        List<Thread> clientThreads = new ArrayList<>();

        int portTCP = Integer.parseInt(args[0]);
        String dbDirectory = args[1];
        String rmiServiceName = args[2];
        int rmiRegistryPort = Integer.parseInt(args[3]);

        try (ServerSocket serverSocket = new ServerSocket(portTCP)) {

            System.out.println("[Main] Server Ready at port : " + portTCP);

            //NOTA ! databaseVersion tem de ser uma variavel
            Thread heartbeatThread = new HeartbeatSender(rmiRegistryPort, rmiServiceName, 1);
            heartbeatThread.start();

            while (true) {

                try (Socket clientSocket = serverSocket.accept()) {

                    Thread clientThread = new Thread(new ClientHandler(clientSocket));
                    clientThreads.add(clientThread);
                    clientThread.start();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}