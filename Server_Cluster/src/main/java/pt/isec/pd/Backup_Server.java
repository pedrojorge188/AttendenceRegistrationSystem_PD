package pt.isec.pd;

import pt.isec.pd.Threads.HeartbeatListener;
import pt.isec.pd.helpers.MULTICAST;

import java.io.*;
import java.net.*;
import java.util.Timer;

public class Backup_Server {
    private int databaseVersion;
    private String backupDir;
    private MulticastSocket multicastSocket;
    private InetAddress multicastGroup;

    public Backup_Server(String backupDir) {
        this.backupDir = backupDir;
        this.databaseVersion = 0;

        try {
            multicastSocket = new MulticastSocket(MULTICAST.PORT);
            multicastGroup = InetAddress.getByName(MULTICAST.ADDR);
            multicastSocket.joinGroup(multicastGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        int databaseVersion = 0; //por enquanto
        String backupDir;
        MulticastSocket multicastSocket;
        InetAddress multicastGroup;

        if (args.length != 1) {
            System.err.println("Usage: java BackupServer <backup_directory>");
            System.exit(1);
        }


        System.out.println("Backup Server Started!! -> backup_directory => "+args[0]+" <-");
        backupDir = args[0];
        HeartbeatListener heartbeatListener = new HeartbeatListener(databaseVersion);
        heartbeatListener.start();
    }
}
