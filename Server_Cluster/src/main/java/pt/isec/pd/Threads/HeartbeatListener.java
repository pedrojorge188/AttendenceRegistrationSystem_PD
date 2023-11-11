package pt.isec.pd.Threads;

import pt.isec.pd.data.HeartBeatInfo;
import pt.isec.pd.helpers.MULTICAST;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;


public class HeartbeatListener extends Thread {
    private static int dbVersion;

    public HeartbeatListener(int dbVersion) {
        this.dbVersion = dbVersion;
    }

    public void run() {
        try (MulticastSocket multicastSocket = new MulticastSocket(MULTICAST.PORT)) {
            InetAddress group = InetAddress.getByName(MULTICAST.ADDR);
            multicastSocket.joinGroup(group);

            byte[] receiveData = new byte[1024];

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
