package pt.isec.pd.rmi.observers;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GetRemoteFileClientInterface extends Remote {
    void writeFileChunk(byte [] fileChunk, int nbytes) throws RemoteException, IOException;
}
