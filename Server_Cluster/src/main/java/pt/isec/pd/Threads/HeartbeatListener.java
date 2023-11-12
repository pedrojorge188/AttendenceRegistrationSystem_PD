package pt.isec.pd.Threads;

import pt.isec.pd.data.HeartBeatInfo;
import pt.isec.pd.database.IRemoteBackupService;
import pt.isec.pd.helpers.MULTICAST;

import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class HeartbeatListener extends Thread {
    private static int dbVersion;
    private File backupDir;
    private final String rmiIp = "127.0.0.1";
    private String backupFileName;
    public HeartbeatListener(int dbVersion,File backupDir,String backupFileName) {
        this.dbVersion = dbVersion;
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
            IRemoteBackupService remoteBackupService = (IRemoteBackupService) Naming.lookup("rmi://" + rmiIp+":"+rmiPort + "/" + rmiName);
            System.out.println("Ficheiro " + localFilePath + " criado.");

            long offset = 0;
            byte[] b;
            while ((b = remoteBackupService.getDatabase(offset)) != null) {
                localFileOutputStream.write(b);
                offset += b.length;
            }

            System.out.println("Transferencia do ficheiro " + fileName + " concluida.");

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
            multicastSocket.joinGroup(group);

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
                            addBackup(backupFileName ,rmiIp , heartbeatInfo.getRmiRegistryPort(), heartbeatInfo.getRmiServiceName());
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
