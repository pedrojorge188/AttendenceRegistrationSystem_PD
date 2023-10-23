package pt.isec.pd.heartbeat;

import pt.isec.pd.helpers.MULTICAST;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HeartbeatSender extends Thread {
    private final String multicastAddress;
    private final int multicastPort;
    private final int rmiRegistryPort;
    private final String rmiServiceName;
    private final int databaseVersion;

    public HeartbeatSender(int rmiRegistryPort, String rmiServiceName, int databaseVersion) {
        this.multicastAddress = MULTICAST.ADDR;
        this.multicastPort = MULTICAST.PORT;
        this.rmiRegistryPort = rmiRegistryPort;
        this.rmiServiceName = rmiServiceName;
        this.databaseVersion = databaseVersion;
    }

    @Override
    public void run() {
        try {
            DatagramSocket multicastSocket = new DatagramSocket();

            while (true) {
                String heartbeatMessage = "Heartbeat from server - RMI Port: " + rmiRegistryPort
                        + " - RMI Service Name: " + rmiServiceName + " - Database Version: " + databaseVersion;

                byte[] messageBytes = heartbeatMessage.getBytes();
                InetAddress group = InetAddress.getByName(multicastAddress);
                DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, group, multicastPort);
                multicastSocket.send(packet);
                System.out.println("Heartbeat enviado para " + multicastAddress + ":" + multicastPort);

                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
