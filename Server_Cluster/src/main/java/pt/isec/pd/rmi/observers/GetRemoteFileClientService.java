package pt.isec.pd.rmi.observers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GetRemoteFileClientService
        extends UnicastRemoteObject
        implements GetRemoteFileClientInterface
{

    FileOutputStream fout = null;

    public GetRemoteFileClientService() throws RemoteException {
        super();
    }
    public synchronized void setFout(FileOutputStream fout) {
        this.fout = fout;
    }

    @Override
    public void writeFileChunk(byte[] fileChunk, int nbytes) throws RemoteException, IOException {
        if(fout==null)throw new NullPointerException("FileOutputStream is null");
        fout.write(fileChunk,0,nbytes);
    }


}