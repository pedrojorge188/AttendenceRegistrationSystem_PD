package pt.isec.pd.helpers;

import pt.isec.pd.data.Event;
import pt.isec.pd.data.InfoStatus;
import pt.isec.pd.data.User;
import pt.isec.pd.database.DatabaseManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EventManager {

    public static void manage(Event event, Socket clientSocket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, User user) throws IOException {

        switch (event.getType()) {

            case CODE_EVENT -> {

                System.out.println("Code received ( " + event.getAttend_code() + " ) by: " + event.getUser_email());
                if(DatabaseManager.getInstance().verifyCode(event)) {
                    InfoStatus response = new InfoStatus(InfoStatus.types_status.CODE_SEND_MADE);
                    response.setMsg_log(event.getType().toString());
                    objectOutputStream.writeObject(response);
                    objectOutputStream.flush();
                }else{
                    InfoStatus response = new InfoStatus(InfoStatus.types_status.CODE_SEND_FAIL);
                    response.setMsg_log(event.getType().toString());
                    objectOutputStream.writeObject(response);
                    objectOutputStream.flush();
                }
            }
            case EDIT_EVENT -> {
                try {
                    if (DatabaseManager.getInstance().changeEvent(event)) {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.EDIT_EVENT_MADE);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    } else {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.EDIT_EVENT_FAIL);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            case CREATE_EVENT -> {
                try {
                    if (DatabaseManager.getInstance().creatEvent(event)) {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.CREATE_EVENT_MADE);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    } else {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.CREATE_EVENT_FAIL);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            case DELETE_EVENT -> {
                try {
                    if (DatabaseManager.getInstance().deleteEvent(event)) {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.DELETE_EVENT_MADE);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    } else {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.DELETE_EVENT_FAIL);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            case ASSOC_USER_EVENT -> {
                try {
                    if (DatabaseManager.getInstance().assocUserEvent(event)) {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.ASSOC_USER_EVENT_MADE);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    } else {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.ASSOC_USER_EVENT_FAIL);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            case GENERATE_CODE -> {

                try {
                    if (DatabaseManager.getInstance().generateCode(event)) {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.GENERATE_CODE_MADE);
                        response.setMsg_log(String.valueOf(event.getAttend_code()));
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    } else {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.GENERATE_CODE_FAIL);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            case REQUEST_CSV_EVENT -> {
                String defaultFileName;
                if(event.getCsv_msg().equals("UserEvents")) {
                    defaultFileName = "userEvents-"+event.getUser_email()+".csv";
                    if(DatabaseManager.getInstance().csvUserEvents(event, defaultFileName)){
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.REQUEST_CSV_EVENT);
                        response.setMsg_log(event.getType().toString());
                        event.setEvent_name("");event.setEvent_end_time("");event.setEvent_start_time("");
                        response.setEventsName(DatabaseManager.getInstance().getCreatedEvents(event));
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                        DatabaseManager.getInstance().sendCSVFile(defaultFileName,clientSocket);
                    }
                }else if(event.getCsv_msg().equals("EventAttend")){
                    defaultFileName = "eventAttend-"+event.getEvent_name()+".csv";
                    if(DatabaseManager.getInstance().csvAttendEvents(event, defaultFileName)){
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.REQUEST_CSV_EVENT);
                        response.setMsg_log(event.getType().toString());
                        event.setEvent_name("");event.setEvent_end_time("");event.setEvent_start_time("");
                        response.setEventsName(DatabaseManager.getInstance().getCreatedEvents(event));
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                        DatabaseManager.getInstance().sendCSVFile(defaultFileName,clientSocket);
                    }
                }
            }
            case LIST_CREATED_EVENTS -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.LIST_CREATED_EVENTS);
                response.setMsg_log(event.getType().toString());
                response.setEventsName(DatabaseManager.getInstance().getCreatedEvents(event));
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
            }
            case LIST_CREATED_EVENTS_BY_USER -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.LIST_CREATED_EVENTS);
                response.setMsg_log(event.getType().toString() + "by user:" + event.getUser_email());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
            }
            case GET_ATTENDANCE_HISTORY -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.GET_HISTORY);
                response.setMsg_log(event.getType().toString());
                response.setUserAttendanceRecords(DatabaseManager.getInstance().getUserAttendance(event));
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
            }
            case LIST_REGISTERED_ATTENDANCE -> {
                InfoStatus response = new InfoStatus(InfoStatus.types_status.LIST_REGISTERED_ATTENDANCE);
                response.setMsg_log(event.getType().toString());
                response.setAttendanceRecords(DatabaseManager.getInstance().getAttendance(event));
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
            }
            case INSERT_ATTENDANCE -> {
                try {
                    if (DatabaseManager.getInstance().insertAttendance(event)) {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.INSERT_ATTENDANCE_MADE);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    } else {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.INSERT_ATTENDANCE_FAIL);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            }
            case DELETE_ATTENDANCE -> {
                try {
                    if (DatabaseManager.getInstance().deleteAttendance(event)) {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.DELETE_ATTENDANCE_MADE);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    } else {
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.DELETE_ATTENDANCE_FAIL);
                        response.setMsg_log(event.getType().toString());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

    }

}
