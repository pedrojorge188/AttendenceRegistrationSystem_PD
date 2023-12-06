package pt.isec.pd.rmi;

import pt.isec.pd.database.DatabaseManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetRemoteFileService extends UnicastRemoteObject implements GetRemoteFileServiceInterface {

    public static final int MAX_CHUNK_SIZE = 10000;
    final List<GetRemoteFileObserverInterface> observers;

    public GetRemoteFileService() throws RemoteException {
        super();
        observers = new ArrayList<>();
    }

    private static void handleException(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }

    public static void createRMIService(String rmiName, int rmiPort ){
        try {
            LocateRegistry.createRegistry(rmiPort);

            String rmiUrl = "rmi://localhost:" + rmiPort + "/" + rmiName;
            GetRemoteFileService backupService = new GetRemoteFileService();
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
    public void addObserver(GetRemoteFileObserverInterface observer) throws RemoteException {
        synchronized (observers) {
            if (observers.contains(observer)) return;
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(GetRemoteFileObserverInterface observer) throws RemoteException {
        synchronized (observers){
            observers.remove(observer);
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

            //notifica todos os observers
            this.notifyObservers("Database tranfer in action");
            return fileChunk;

        } catch (FileNotFoundException e) {
            System.out.println("Ocorreu a excepcao {" + e + "} ao tentar abrir o ficheiro!");
            throw new FileNotFoundException(fileName);

        } catch (IOException e) {
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);
            throw new IOException(fileName,e.getCause());
        }

    }

    protected void notifyObservers(String msg){
        List<GetRemoteFileObserverInterface> observersToRemove = new ArrayList<>();
        synchronized (observers){
            for(GetRemoteFileObserverInterface observer: observers){
                try{
                    observer.notifyNewOperationConcluded(msg);
                }catch (RemoteException e){
                    observersToRemove.add(observer);
                }
            }
            observers.removeAll(observersToRemove);
        }
    }



}
