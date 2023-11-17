package pt.isec.pd.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteBackupService extends Remote {
    byte[] getDatabase(long offset) throws RemoteException,IOException;
    void callBack(String msg) throws RemoteException;
}
