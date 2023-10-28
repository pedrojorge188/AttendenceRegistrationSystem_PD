package pt.isec.pd.Threads;

import pt.isec.pd.data.HeartBeatInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class HeartbeatListener extends Thread {
    private final MulticastSocket multicastSocket;
    private static int dbVersion;

    public HeartbeatListener(MulticastSocket ms, int dbVersion) {
        this.multicastSocket = ms;
        this.dbVersion = dbVersion;

        try {
            multicastSocket.setSoTimeout(30000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                try {
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    multicastSocket.receive(receivePacket);

                    byte[] serializedObject = receivePacket.getData();
                    ByteArrayInputStream bais = new ByteArrayInputStream(serializedObject);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    HeartBeatInfo heartbeatInfo = (HeartBeatInfo) ois.readObject();

                    System.out.println("[SERVER] Receive Backup with Info (" +
                            "RMI_PORT:"+heartbeatInfo.getRmiRegistryPort()
                            + "\tRMI_SERVICE_NAME:"+heartbeatInfo.getRmiServiceName()
                            + "\tDATABASE_VERSION:"+heartbeatInfo.getDatabaseVersion()
                            + "v ).");

                    if(dbVersion != heartbeatInfo.getDatabaseVersion()){
                        dbVersion = heartbeatInfo.getDatabaseVersion();
                        System.out.println("DATABASE VERSION UPDATE, BACKUP LOADING....");
                        /*
                         * Fazer backup
                         */
                    }

                } catch (java.net.SocketTimeoutException e) {
                    System.out.println("[BACKUP SERVER] Any heartbeat sent by the main server for 30 seconds. Exiting...");
                    System.exit(0);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
