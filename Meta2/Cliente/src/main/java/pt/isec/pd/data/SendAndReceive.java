package pt.isec.pd.data;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

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

    public int createEvent(String eventName, String eventLocal, String eventDate, String eventStartHour, String eventEndHour) throws IOException {
        // Use URLEncoder para lidar com espaços e outros caracteres especiais
        String encodedEventName = URLEncoder.encode(eventName, StandardCharsets.UTF_8.toString());
        URL url = new URL(server_Domain+"/event/create/name="+ encodedEventName +"/location="+ eventLocal +"/date="+eventDate+"/start_time="+ eventStartHour+"/end_time="+eventEndHour);
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

    public int deleteEvent(String eventName) throws IOException {
        // Use URLEncoder para lidar com espaços e outros caracteres especiais
        String encodedEventName = URLEncoder.encode(eventName, StandardCharsets.UTF_8.toString());

        URL url = new URL(server_Domain+"/event/delete/name="+encodedEventName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Bearer " + acc_token);

        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            pcs.firePropertyChange(InfoStatus.types_status.CODE_SEND_MADE.toString(),null,null);
        else
            pcs.firePropertyChange(InfoStatus.types_status.CODE_SEND_FAIL.toString(),null,null);

        connection.disconnect();
        return connection.getResponseCode();
    }

    public int generateEventCode(String eventName, String codeTime) throws IOException {
        // Use URLEncoder para lidar com espaços e outros caracteres especiais
        String encodedEventName = URLEncoder.encode(eventName, StandardCharsets.UTF_8.toString());

        URL url = new URL(server_Domain+"/code/generate/name="+encodedEventName+"/time="+codeTime);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + acc_token);

        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            try (InputStream inputStream = connection.getInputStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNextInt()) {
                    event_code = scanner.nextInt();
                    // Agora 'eventCode' contém o valor retornado pelo server
                    pcs.firePropertyChange(InfoStatus.types_status.CODE_SEND_MADE.toString(), null, null);
                }
            }
        } else
            pcs.firePropertyChange(InfoStatus.types_status.CODE_SEND_FAIL.toString(),null,null);

        connection.disconnect();
        return connection.getResponseCode();
    }

    public JsonArray searchEvent(String eventName, String eventStartHour, String eventEndHour) throws IOException {
        String spec = server_Domain + "/event/list";
        String encodedEventName = URLEncoder.encode(eventName, StandardCharsets.UTF_8.toString());

        if (!eventName.isBlank())
            spec += "?name=" + encodedEventName;

        if (!eventStartHour.isBlank() && eventName.isBlank()) {
            spec += "?start_time=" + eventStartHour + "&end_time=" + eventEndHour;
        } else if(!eventStartHour.isBlank() ) {
            spec += "&start_time=" + eventStartHour + "&end_time=" + eventEndHour;
        }

        System.out.println("(search event) SPEC: " + spec);
        URL url = new URL(spec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + acc_token);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            return processJSONResponse(connection);

        return null;
    }

    public JsonArray searchEventByAttendances(String eventName) throws IOException {
        String spec = server_Domain + "/code/list";
        String encodedEventName = URLEncoder.encode(eventName, StandardCharsets.UTF_8.toString());

        if (!eventName.isBlank())
            spec += "/" + encodedEventName;

        System.out.println("(search event by attendances) SPEC: " + spec);
        URL url = new URL(spec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + acc_token);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            return processJSONResponse(connection);

        return null;
    }


    public JsonArray searchEventAttendances(String eventName, String eventStartHour, String eventEndHour) throws IOException {
        String spec = server_Domain + "/code/search";
        String encodedEventName = URLEncoder.encode(eventName, StandardCharsets.UTF_8.toString());

        if (!eventName.isBlank())
            spec += "?name=" + encodedEventName;

        if (!eventStartHour.isBlank() && eventName.isBlank()) {
            spec += "?start_time=" + eventStartHour + "&end_time=" + eventEndHour;
        } else if(!eventStartHour.isBlank() ) {
            spec += "&start_time=" + eventStartHour + "&end_time=" + eventEndHour;
        }

        System.out.println("(search event) SPEC: " + spec);
        URL url = new URL(spec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + acc_token);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            return processJSONResponse(connection);

        return null;
    }

    public JsonArray processJSONResponse(HttpURLConnection connection) throws IOException {

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            JsonReader jsonReader = Json.createReader(connection.getInputStream());
            JsonArray jsonArray = jsonReader.readArray();
            return jsonArray;
        }else{
            return null;
        }
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