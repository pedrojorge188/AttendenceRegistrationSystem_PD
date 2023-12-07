package pt.isec.pd.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GetRemoteFileServiceInterface extends Remote {
    byte[] getDatabase(long offset) throws RemoteException,IOException;
    void addObserver(GetRemoteFileObserverInterface observer) throws RemoteException;
    void removeObserver(GetRemoteFileObserverInterface observer) throws RemoteException;
}
