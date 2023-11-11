package pt.isec.pd;

import pt.isec.pd.Threads.ClientHandler;
import pt.isec.pd.Threads.HeartbeatHandler;
import pt.isec.pd.database.DatabaseManager;
import pt.isec.pd.database.IRemoteBackupService;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main_Server implements IRemoteBackupService {
    public static final int MAX_CHUNK_SIZE = 10000;
    @Override
    public byte[] getDatabase(long offset) throws RemoteException,IOException {
        String requestedCanonicalFilePath = null;
        byte[] fileChunk = new byte[MAX_CHUNK_SIZE];
        int nbytes;
        String fileName = DatabaseManager.getInstance().getDbName();
        File localDirectory = new File(DatabaseManager.getInstance().getDbAddr());
        fileName = fileName.trim();
        try {
            requestedCanonicalFilePath = new File(localDirectory + File.separator + fileName).getCanonicalPath();

            if (!requestedCanonicalFilePath.startsWith(localDirectory.getCanonicalPath() + File.separator)) {
                System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
                System.out.println("A directoria de base nao corresponde a " + localDirectory.getCanonicalPath() + "!");
                return null;
            }
            try (FileInputStream requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath)) {
                requestedFileInputStream.skip(offset);
                nbytes = requestedFileInputStream.read(fileChunk);
                if (nbytes == -1) { //EOF
                    return null;
                }
                if (nbytes < fileChunk.length) {
                    return Arrays.copyOf(fileChunk,nbytes);
                }
            }
            return fileChunk;
        } catch (FileNotFoundException e) { // Subclasse de IOException
            System.out.println("Ocorreu a excepcao {" + e + "} ao tentar abrir o ficheiro!");
            throw new FileNotFoundException(fileName);
        } catch (IOException e) {
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);
            throw new IOException(fileName,e.getCause());
        }
    }
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