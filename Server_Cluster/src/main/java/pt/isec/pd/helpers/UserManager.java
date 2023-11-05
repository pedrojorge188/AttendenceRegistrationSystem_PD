package pt.isec.pd.helpers;

import pt.isec.pd.data.InfoStatus;
import pt.isec.pd.data.User;

import javax.xml.crypto.Data;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserManager {

    public UserManager(){

    }

    public static void manage(User user, Socket clientSocket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, User user1){
        System.out.println("[Client " + user.getUsername_email() + "-] SEND A USER NOTIFICATION! (" + user.getType() +")");


        switch (user.getType()) {
            case LOGIN -> {

                try{
                    InfoStatus response;

                    if(DatabaseManager.getInstance().userExists(user.getUsername_email(), user.getPassword()).contains("admin"))
                        response = new InfoStatus(InfoStatus.types_status.LOGIN_MADE_ADMIN);
                    else if(DatabaseManager.getInstance().userExists(user.getUsername_email(), user.getPassword()).contains("normal"))
                        response = new InfoStatus(InfoStatus.types_status.LOGIN_MADE_USER);
                    else
                        response = new InfoStatus(InfoStatus.types_status.LOGIN_FAIL);

                    response.setMsg_log(user.getUsername_email());
                    objectOutputStream.writeObject(response);
                    objectOutputStream.flush();
                    user1 = user;

                }catch (Exception exception){
                    exception.printStackTrace();
                }

            }
            case REGISTER -> {
                try{
                    if(DatabaseManager.getInstance().userCreate(user.getName(),user.getStudent_uid(),user.getUsername_email(),user.getPassword())){
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.REGISTER_MADE);
                        response.setMsg_log(user.getUsername_email());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }else{
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.REGISTER_FAIL);
                        response.setMsg_log(user.getUsername_email());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }
                    user1 = user;

                }catch (Exception exception){
                    exception.printStackTrace();
                }

            }
            case CHANGES -> {

                try{
                    if(DatabaseManager.getInstance().changeUserAccount(user.getUsername_email(),user.getPassword(),user.getName())){
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.CHANGES_MADE);
                        response.setMsg_log(user.getUsername_email());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }else{
                        InfoStatus response = new InfoStatus(InfoStatus.types_status.CHANGES_FAIL);
                        response.setMsg_log(user.getUsername_email());
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }

                }catch (Exception exception){
                    exception.printStackTrace();
                }

            }
        }

    }

}
