package pt.isec.pd;

import pt.isec.pd.Threads.HeartbeatListener;
import pt.isec.pd.database.IRemoteBackupService;
import pt.isec.pd.helpers.MULTICAST;

import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Timer;

public class Backup_Server {
    private int databaseVersion;
    private File backupDir;
    private InetAddress multicastGroup;

    public Backup_Server(File backupDir) {
        this.backupDir = backupDir;
        this.databaseVersion = 0;
    }
    public boolean isDirEmpty(){
        if (backupDir == null || !backupDir.exists() || !backupDir.isDirectory()||!backupDir.canWrite()) {
            System.out.println("Invalid backup directory");
            return false;
        }
        String[] files = backupDir.list();
        return files == null || files.length == 0;
    }
    public void createBackupDirectory() {
        if (backupDir == null) {
            System.out.println("Invalid backup directory");
            return;
        }
        if (!backupDir.exists()) {
            if (backupDir.mkdirs()) System.out.println("Backup directory created: " + backupDir.getAbsolutePath());
            else System.out.println("Failed to create backup directory");
        } else System.out.println("Backup directory already exists: " + backupDir.getAbsolutePath());
    }
    public void createBackup(String fileName,String ip,String rmiName){
        long offset;
        byte [] b = new byte[0];
        String localFilePath;
        if(backupDir==null) return;
        try{
            localFilePath = new File(backupDir.getPath()+File.separator+fileName).getCanonicalPath();
        }catch(IOException ex){
            System.out.println(ex);
            return;
        }
        try(FileOutputStream localFileOutputStream = new FileOutputStream(localFilePath)/*...*/){
            System.out.println("Ficheiro " + localFilePath + " criado.");
            IRemoteBackupService remoteBackupService = (IRemoteBackupService) Naming.lookup("rmi://"+ip+"/"+rmiName); //...
            offset = 0;
            while((b=remoteBackupService.getDatabase(offset))!=null){
                localFileOutputStream.write(b);
                offset += b.length;
            }
            System.out.println("Transferencia do ficheiro " + fileName + " concluida.");
        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
        }catch(NotBoundException e){
            System.out.println("Servico remoto desconhecido - " + e);
        }catch(IOException e){
            System.out.println("Erro E/S - " + e);
        }catch(Exception e){
            System.out.println("Erro - " + e);
        }
    }
    public static void main(String[] args) {
        int databaseVersion = 0;
        MulticastSocket multicastSocket;
        InetAddress multicastGroup;

        if (args.length != 1) {
            System.err.println("Usage: java BackupServer <backup_directory>");
            System.exit(1);
        }
        Backup_Server backupServer=new Backup_Server(new File(args[0]));
        if(!backupServer.isDirEmpty()){System.out.println("Directory is not empty!");return;}
        backupServer.createBackupDirectory();
        System.out.println("Backup Server Started!! -> backup_directory => "+args[0]+" <-");
        HeartbeatListener heartbeatListener = new HeartbeatListener(databaseVersion);
        heartbeatListener.start();
    }
}