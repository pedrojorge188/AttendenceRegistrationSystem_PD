package pt.isec.pd.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;

public class RemoteBackupService extends UnicastRemoteObject implements IRemoteBackupService{
    public static final int MAX_CHUNK_SIZE = 10000;
    public RemoteBackupService() throws RemoteException {
        super();

    }
    private static void handleException(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
    public static void createRMIService(String rmiName, int rmiPort ){
        try {
           // System.setProperty("java.rmi.server.hostname", "IP ADDRESS");

            LocateRegistry.createRegistry(rmiPort);

            String rmiUrl = "rmi://localhost:" + rmiPort + "/" + rmiName;
            RemoteBackupService backupService = new RemoteBackupService();
            Naming.bind(rmiUrl, backupService);

            System.out.println("RMI Service '" + rmiName + "' is running at " + rmiUrl);
        } catch (RemoteException e) {
            handleException("RemoteException", e);
        } catch (AlreadyBoundException e) {
            handleException("Service already bound", e);
        } catch (MalformedURLException e) {
            handleException("MalformedURLException", e);
        } catch (Exception e) {
            handleException("Exception", e);
        }
    }

    @Override
    public byte[] getDatabase(long offset) throws RemoteException, IOException {
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

    @Override
    public void callBack(String msg) throws RemoteException {
        System.out.println("[CALLBACK] -> BACKUP with msg: " + msg);
    }
}
