package pt.isec.pd.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteBackupService extends Remote {
    byte[] getDatabase() throws RemoteException,IOException;
}
