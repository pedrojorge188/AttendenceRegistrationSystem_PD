package pt.isec.pd.Threads;

import pt.isec.pd.data.HeartBeatInfo;
import pt.isec.pd.database.DatabaseManager;
import pt.isec.pd.database.elements.Version;
import pt.isec.pd.helpers.MULTICAST;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class HeartbeatHandler extends Thread {
    private static  String multicastAddress;
    private static  int multicastPort;
    private static  int rmiRegistryPort;
    private static  String rmiServiceName;
    private static  int databaseVersion;

    public HeartbeatHandler(int rmiRegistryPort, String rmiServiceName, int databaseVersion) {
        multicastAddress = MULTICAST.ADDR;
        multicastPort = MULTICAST.PORT;
        HeartbeatHandler.rmiRegistryPort = rmiRegistryPort;
        HeartbeatHandler.rmiServiceName = rmiServiceName;
        HeartbeatHandler.databaseVersion = databaseVersion;
    }
    public static void sendHb(){

        try (MulticastSocket multicastSocket = new MulticastSocket()) {
            while(DatabaseManager.getInstance().getConnection() == null){}
            HeartBeatInfo heartbeatInfo = new HeartBeatInfo(rmiRegistryPort, rmiServiceName,
                    Version.getVersion(DatabaseManager.getInstance().getConnection())
                    );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(heartbeatInfo);
            byte[] serializedObject = baos.toByteArray();

            InetAddress group = InetAddress.getByName(multicastAddress);
            multicastSocket.joinGroup(group);
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
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
    }
}
