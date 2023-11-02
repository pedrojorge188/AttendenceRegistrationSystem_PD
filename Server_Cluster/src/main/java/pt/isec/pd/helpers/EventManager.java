package pt.isec.pd.helpers;

import pt.isec.pd.data.Event;
import pt.isec.pd.data.InfoStatus;
import pt.isec.pd.data.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EventManager {

    public static void manage(Event event, Socket clientSocket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, User user) throws IOException {

        switch (event.getType()) {

            case CODE_EVENT -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.CODE_SEND_MADE);
                response.setMsg_log(event.getType().toString());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT - "+event.getUser_email()+"] CODE RECEIVED -> code:"+event.getAttend_code());
            }
            case EDIT_EVENT -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.EDIT_EVENT_MADE);
                response.setMsg_log(event.getType().toString());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT] EVENT: "+event.getEvent_name()+" EDITED");
            }
            case CREATE_EVENT -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.CREATE_EVENT_MADE);
                response.setMsg_log(event.getType().toString());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT] EVENT: "+event.getEvent_name()+" CREATED");
            }
            case DELETE_EVENT -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.DELETE_EVENT_MADE);
                response.setMsg_log(event.getType().toString());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT] EVENT: "+event.getEvent_name()+" DELETED");

            }
            case GENERATE_CODE -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.GENERATE_CODE_MADE);
                response.setMsg_log(event.getType().toString());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT] CODE FOR: "+event.getEvent_name()+" EVENT GENERATED");
            }
            case REQUEST_CSV_EVENT -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.REQUEST_CSV_EVENT);
                response.setMsg_log(event.getType().toString());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT] CSV FOR: "+event.getEvent_name()+" EVENT SENT");
            }
            case LIST_CREATED_EVENTS -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.LIST_CREATED_EVENTS);
                response.setMsg_log(event.getType().toString());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT] LIST OF EVENTS SENT");
            }
            case LIST_CREATED_EVENTS_BY_USER -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.LIST_CREATED_EVENTS);
                response.setMsg_log(event.getType().toString() + "by user:" + event.getUser_email());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT] LIST OF EVENTS OF USER "+ event.getUser_email()+"sent");
            }
            case GET_ATTENDANCE_HISTORY -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.GET_HISTORY);
                response.setMsg_log(event.getType().toString());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT] LIST ATTENDANCE HISTORY REQUESTED");
            }
            case LIST_REGISTERED_ATTENDANCE -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.LIST_REGISTERED_ATTENDANCE);
                response.setMsg_log(event.getType().toString());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT] LIST OF REGISTERD ATTENDENCE SENT");
            }
            case INSERT_ATTENDANCE -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.INSERT_ATTENDANCE_MADE);
                response.setMsg_log(event.getType().toString());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT] CLIENT INSERTED TO ATTENDANCE");
            }
            case DELETE_ATTENDANCE -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.DELETE_ATTENDANCE_MADE);
                response.setMsg_log(event.getType().toString());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.println("[CLIENT] CLIENT DELETED FROM ATTENDANCE");
            }
        }

    }

}
