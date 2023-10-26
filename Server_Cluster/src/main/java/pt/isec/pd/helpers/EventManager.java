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

        System.out.println("[Client] SEND A EVENT NOTIFICATION! (" +event.getType()+ ")");
        InfoStatus response = new InfoStatus(InfoStatus.types_status.MSG_STACK);

        switch (event.getType()){

            case CODE_EVENT -> {
                /*USE DATA BASE TO DO THIS ACTIVITY*/
                System.out.println("[CLIENT] CODE RECEIVED -> "+ event.getAttend_code());
            }
            case EDIT_EVENT -> {
                /*USE DATA BASE TO DO THIS ACTIVITY*/
            }
            case CREATE_EVENT -> {
                /*USE DATA BASE TO DO THIS ACTIVITY*/
            }
            case DELETE_EVENT -> {
                /*USE DATA BASE TO DO THIS ACTIVITY*/
            }
            case GENERATE_CODE -> {
                /*USE DATA BASE TO DO THIS ACTIVITY*/
            }
            case REQUEST_CSV_EVENT -> {
                /*USE DATA BASE TO DO THIS ACTIVITY*/
            }
            case LIST_CREATED_EVENTS -> {
                /*USE DATA BASE TO DO THIS ACTIVITY*/
            }
            case GET_ATTENDANCE_HISTORY -> {
                /*USE DATA BASE TO DO THIS ACTIVITY*/
            }
            case LIST_REGISTERED_ATTENDANCE -> {
                /*USE DATA BASE TO DO THIS ACTIVITY*/
            }
        }

        //Submit response to client ...
        response.setMsg_log(event.getType().toString());
        objectOutputStream.writeObject(response);
        objectOutputStream.flush();

    }

}
