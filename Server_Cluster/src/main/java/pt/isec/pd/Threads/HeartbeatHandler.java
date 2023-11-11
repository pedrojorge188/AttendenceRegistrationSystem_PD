package pt.isec.pd.Threads;

import pt.isec.pd.data.HeartBeatInfo;
import pt.isec.pd.database.DatabaseManager;
import pt.isec.pd.database.elements.Version;
import pt.isec.pd.helpers.MULTICAST;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class HeartbeatHandler extends Thread {
    private static String multicastAddress = MULTICAST.ADDR;
    private static int multicastPort = MULTICAST.PORT;
    private static int rmiRegistryPort;
    private static String rmiServiceName;
    private static int databaseVersion;

    public HeartbeatHandler(int rmiRegistryPort, String rmiServiceName, int databaseVersion) {
        HeartbeatHandler.rmiRegistryPort = rmiRegistryPort;
        HeartbeatHandler.rmiServiceName = rmiServiceName;
        HeartbeatHandler.databaseVersion = databaseVersion;
    }

    public static void sendHb() {
        try (MulticastSocket multicastSocket = new MulticastSocket()) {

            InetAddress group = InetAddress.getByName(multicastAddress);
            multicastSocket.joinGroup(group);

            HeartBeatInfo heartbeatInfo = new HeartBeatInfo(
                    rmiRegistryPort,
                    rmiServiceName,
                    Version.getVersion(DatabaseManager.getInstance().getConnection())
            );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(heartbeatInfo);
            }
            byte[] serializedObject = baos.toByteArray();

            DatagramPacket packet = new DatagramPacket(serializedObject, serializedObject.length, group, multicastPort);
            multicastSocket.send(packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            sendHb();
            try {
                Thread.sleep(10000); // Adjust the sleep duration as needed
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
