package pt.isec.pd.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GetRemoteFileObserver extends UnicastRemoteObject implements GetRemoteFileObserverInterface{
    public GetRemoteFileObserver()throws RemoteException {
    }

    @Override
    public void notifyNewOperationConcluded(String description) throws RemoteException {
        System.out.println(description);
        System.out.println();
    }
}
