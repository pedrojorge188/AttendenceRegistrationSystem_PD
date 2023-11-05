package pt.isec.pd.Threads;

import pt.isec.pd.data.HeartBeatInfo;
import pt.isec.pd.helpers.DatabaseManager;
import pt.isec.pd.helpers.MULTICAST;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HeartbeatHandler extends Thread {
    private static  String multicastAddress;
    private static  int multicastPort;
    private static  int rmiRegistryPort;
    private static  String rmiServiceName;
    private static  int databaseVersion;

    public HeartbeatHandler(int rmiRegistryPort, String rmiServiceName, int databaseVersion) {
        this.multicastAddress = MULTICAST.ADDR;
        this.multicastPort = MULTICAST.PORT;
        this.rmiRegistryPort = rmiRegistryPort;
        this.rmiServiceName = rmiServiceName;
        this.databaseVersion = databaseVersion;
    }
    public static void sendHb(){

        try (DatagramSocket multicastSocket = new DatagramSocket()) {
            HeartBeatInfo heartbeatInfo = new HeartBeatInfo(rmiRegistryPort, rmiServiceName, DatabaseManager.getInstance().getVersion());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(heartbeatInfo);
            byte[] serializedObject = baos.toByteArray();

            InetAddress group = InetAddress.getByName(multicastAddress);
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
                //System.out.println("[SERVER] Heartbeat send to " + multicastAddress + ":" + multicastPort);
            }
    }
}
