package pt.isec.pd.rmi.services;

import pt.isec.pd.rmi.observers.GetRemoteFileClientInterface;

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GetRemoteFileServiceInterface extends Remote {
    byte[] getDatabase(long offset) throws RemoteException,IOException;
    void getFile(File dir, String fileName, GetRemoteFileClientInterface cliRef) throws RemoteException, IOException;
    void addObserver(GetRemoteFileObserverInterface observer) throws RemoteException;
    void removeObserver(GetRemoteFileObserverInterface observer) throws RemoteException;
}
