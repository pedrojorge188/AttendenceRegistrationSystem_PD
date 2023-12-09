package pt.isec.pd.data;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class SendAndReceive {

    private static String server_Domain;
    private static String acc_token;
    private static SendAndReceive instance;
    private List<String> eventsName;
    private List<String> attendanceRecords;
    private PropertyChangeSupport pcs;
    private int event_code;

    private SendAndReceive() {}

    public static SendAndReceive getInstance() {
        if (instance == null) {
            instance = new SendAndReceive();
        }
        return instance;
    }


    public int login(String username, String password) throws IOException {
        String responseBody = null;
        URL url = new URL(server_Domain+"/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String authString = username + ":" + password;
        String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Basic " + encodedAuthString);
        Scanner s;

        if(connection.getErrorStream()!=null) {
            s = new Scanner(connection.getErrorStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        }

        try {
            s = new Scanner(connection.getInputStream()).useDelimiter("\\A");
            responseBody = s.hasNext() ? s.next() : null;
        } catch (IOException e){}

        connection.disconnect();

        acc_token = responseBody;
        if(acc_token == null)
            return connection.getResponseCode();

        url = new URL(server_Domain+"/permission");

        connection = (HttpURLConnection) url.openConnection();
        authString = username + ":" + password;
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + acc_token);

        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.flush();
        wr.close();

        int responseCode = connection.getResponseCode();

        boolean permissionGranted = (responseCode == HttpURLConnection.HTTP_OK);
        if(permissionGranted)
            pcs.firePropertyChange(InfoStatus.types_status.LOGIN_MADE_ADMIN.toString(),null,null);
        else
            pcs.firePropertyChange(InfoStatus.types_status.LOGIN_MADE_USER.toString(),null,null);

        connection.disconnect();
        return connection.getResponseCode();
    }

    public int register(int studentId, String username, String email, String psw) throws IOException {

        String responseBody = null;
        URL url = new URL(server_Domain+"/register/id="+studentId+"&username="+username+"&email="+email+"&password="+psw);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            pcs.firePropertyChange(InfoStatus.types_status.LOGIN_MADE_USER.toString(),null,null);

        connection.disconnect();
        return connection.getResponseCode();
    }

    public int sendCode(int code) throws IOException {

        URL url = new URL(server_Domain+"/code/send/"+code);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + acc_token);

        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            pcs.firePropertyChange(InfoStatus.types_status.CODE_SEND_MADE.toString(),null,null);
        else
            pcs.firePropertyChange(InfoStatus.types_status.CODE_SEND_FAIL.toString(),null,null);

        connection.disconnect();
        return connection.getResponseCode();
    }

    public boolean connect(String addr,int port){
        if(addr == null || port <= 0)
            return false;
        server_Domain = "http://"+addr+":"+port;
        System.out.println("Connected to "+ server_Domain);
        try{
            pcs = new PropertyChangeSupport(this);
            eventsName = new ArrayList<>();
        }catch(Exception exp){
            return false;
        }
      return true;
    }


    public int getEventCode() {
        return this.event_code;
    }

    public void addPropertyChangeListener(String property,PropertyChangeListener listener){
        pcs.addPropertyChangeListener(property,listener);
    }

}