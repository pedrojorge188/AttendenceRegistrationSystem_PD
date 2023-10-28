package pt.isec.pd.Threads;

import pt.isec.pd.data.HeartBeatInfo;
import pt.isec.pd.helpers.MULTICAST;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HeartbeatHandler extends Thread {
    private final String multicastAddress;
    private final int multicastPort;
    private final int rmiRegistryPort;
    private final String rmiServiceName;
    private final int databaseVersion;

    public HeartbeatHandler(int rmiRegistryPort, String rmiServiceName, int databaseVersion) {
        this.multicastAddress = MULTICAST.ADDR;
        this.multicastPort = MULTICAST.PORT;
        this.rmiRegistryPort = rmiRegistryPort;
        this.rmiServiceName = rmiServiceName;
        this.databaseVersion = databaseVersion;
    }

    @Override
    public void run() {
        try (DatagramSocket multicastSocket = new DatagramSocket()) {
            while (true) {

                HeartBeatInfo heartbeatInfo = new HeartBeatInfo(rmiRegistryPort, rmiServiceName, databaseVersion);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(heartbeatInfo);
                byte[] serializedObject = baos.toByteArray();

                InetAddress group = InetAddress.getByName(multicastAddress);
                DatagramPacket packet = new DatagramPacket(serializedObject, serializedObject.length, group, multicastPort);
                multicastSocket.send(packet);

              //  System.out.println("[SERVER] Heartbeat send to " + multicastAddress + ":" + multicastPort);

                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
