package pt.isec.pd.Threads;

import pt.isec.pd.data.HeartBeatInfo;
import pt.isec.pd.rmi.GetRemoteFileObserver;
import pt.isec.pd.rmi.GetRemoteFileObserverInterface;
import pt.isec.pd.rmi.GetRemoteFileServiceInterface;
import pt.isec.pd.helpers.MULTICAST;

import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collections;

import static pt.isec.pd.Threads.HeartbeatHandler.selectNetworkInterface;


public class HeartbeatListener extends Thread{
    private static int dbVersion;
    GetRemoteFileServiceInterface remoteFileService;
    GetRemoteFileObserverInterface service = null;
    private File backupDir;
    private String backupFileName;

    public HeartbeatListener(int dbVersion,File backupDir,String backupFileName) {
        this.dbVersion = dbVersion;
        try {
            service = new GetRemoteFileObserver();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        this.backupDir=backupDir;
        this.backupFileName=backupFileName;
    }

    public void addBackup(String fileName, String rmiIp, int rmiPort, String rmiName) {
        if (backupDir == null) {
            return;
        }

        String localFilePath;
        try {
            localFilePath = new File(backupDir.getPath() + File.separator + fileName).getCanonicalPath();
        } catch (IOException ex) {
            System.out.println("Erro ao obter o caminho do arquivo: " + ex);
            return;
        }

        try (FileOutputStream localFileOutputStream = new FileOutputStream(localFilePath)) {
            remoteFileService = (GetRemoteFileServiceInterface) Naming.lookup("rmi://" + rmiIp+":"+rmiPort + "/" + rmiName);
            remoteFileService.addObserver(service);

            long offset = 0;
            byte[] b;
            while ((b = remoteFileService.getDatabase(offset)) != null) {
                localFileOutputStream.write(b);
                offset += b.length;
            }

        } catch (RemoteException e) {
            System.out.println("Erro remoto - " + e);
        } catch (NotBoundException e) {
            System.out.println("Servico remoto desconhecido - " + e);
        } catch (IOException e) {
            System.out.println("Erro E/S - " + e);
        } catch (Exception e) {
            System.out.println("Erro - " + e);
        }
    }



    public void run() {
        try (MulticastSocket multicastSocket = new MulticastSocket(MULTICAST.PORT)) {
            InetAddress group = InetAddress.getByName(MULTICAST.ADDR);
            NetworkInterface nif;
            nif = selectNetworkInterface();

            multicastSocket.joinGroup(new InetSocketAddress(group, MULTICAST.PORT), nif);
            multicastSocket.setSoTimeout(30000); // timeout 30 segundos

            byte[] receiveData = new byte[1024];
            boolean firstTime=true;
            while (true) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    multicastSocket.receive(receivePacket);

                    byte[] serializedObject = receivePacket.getData();

                    try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedObject);
                         ObjectInputStream ois = new ObjectInputStream(bais)) {
                        HeartBeatInfo heartbeatInfo = (HeartBeatInfo) ois.readObject();

                        System.out.println("[SERVER] Received Heartbeat with Info (" +
                                "RMI_PORT:" + heartbeatInfo.getRmiRegistryPort() +
                                "\tRMI_SERVICE_NAME:" + heartbeatInfo.getRmiServiceName() +
                                "\tDATABASE_VERSION:" + heartbeatInfo.getDatabaseVersion() + "v ).");
                        if(!firstTime) {
                            if (heartbeatInfo.getDatabaseVersion() != dbVersion) {
                                return;
                            }
                        }else{
                            dbVersion= heartbeatInfo.getDatabaseVersion();
                            addBackup(backupFileName ,heartbeatInfo.getRmiIp() , heartbeatInfo.getRmiRegistryPort(), heartbeatInfo.getRmiServiceName());
                            firstTime=false;
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("[SERVER] No heartbeat received for 30 seconds. Exiting...");
                    System.exit(0);
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
