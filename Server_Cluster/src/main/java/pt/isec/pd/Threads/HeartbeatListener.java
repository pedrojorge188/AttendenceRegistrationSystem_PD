package pt.isec.pd.Threads;

import pt.isec.pd.data.HeartBeatInfo;
import pt.isec.pd.helpers.MULTICAST;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class HeartbeatListener extends Thread {
    private static int dbVersion;

    public HeartbeatListener(int dbVersion) {
        this.dbVersion = dbVersion;
    }

    public void run() {
        try {
            while (true) {
                try (MulticastSocket multicastSocket = new MulticastSocket(MULTICAST.PORT)) {
                    InetAddress group = InetAddress.getByName(MULTICAST.ADDR);
                    multicastSocket.joinGroup(group);
                    try {
                        multicastSocket.setSoTimeout(30000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while (true) {
                        try {
                            byte[] receiveData = new byte[1024];
                            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                            multicastSocket.receive(receivePacket);

                            byte[] serializedObject = receivePacket.getData();
                            ByteArrayInputStream bais = new ByteArrayInputStream(serializedObject);
                            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                                HeartBeatInfo heartbeatInfo = (HeartBeatInfo) ois.readObject();

                                System.out.println("[SERVER] Received Backup with Info (" +
                                        "RMI_PORT:" + heartbeatInfo.getRmiRegistryPort()
                                        + "\tRMI_SERVICE_NAME:" + heartbeatInfo.getRmiServiceName()
                                        + "\tDATABASE_VERSION:" + heartbeatInfo.getDatabaseVersion()
                                        + "v ).");

                                // Your logic for handling the received heartbeat information
                                if (dbVersion != heartbeatInfo.getDatabaseVersion()) {
                                    dbVersion = heartbeatInfo.getDatabaseVersion();
                                    System.out.println("DATABASE VERSION UPDATE, BACKUP LOADING....");
                                    /*
                                     * Fazer backup
                                     */
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
