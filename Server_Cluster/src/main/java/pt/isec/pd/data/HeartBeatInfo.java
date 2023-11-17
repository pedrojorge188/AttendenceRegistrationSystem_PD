package pt.isec.pd.data;

import java.io.Serializable;

public class HeartBeatInfo implements Serializable {

    static final long SerialVersionUID = 1L;

    private int rmiRegistryPort;
    private String rmiServiceName;
    private String rmiIp;
    private int databaseVersion;

    public HeartBeatInfo(int rmiRegistryPort, String rmiServiceName,String rmiIp, int databaseVersion) {
        this.rmiRegistryPort = rmiRegistryPort;
        this.rmiServiceName = rmiServiceName;
        this.rmiIp = rmiIp;
        this.databaseVersion = databaseVersion;
    }

    public String getRmiIp() {
        return rmiIp;
    }

    public int getRmiRegistryPort() {
        return rmiRegistryPort;
    }

    public String getRmiServiceName() {
        return rmiServiceName;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }
}
