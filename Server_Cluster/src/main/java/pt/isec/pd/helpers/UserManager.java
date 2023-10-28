package pt.isec.pd.helpers;

import pt.isec.pd.data.InfoStatus;
import pt.isec.pd.data.User;

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

                    //para debug (máis tarde é necessario fazer com a base de dados)
                    if(user.getUsername_email().equals("admin@admin")){
                        response = new InfoStatus(InfoStatus.types_status.LOGIN_MADE_ADMIN);
                    }else{
                        response = new InfoStatus(InfoStatus.types_status.LOGIN_MADE_USER);
                    }

                    objectOutputStream.writeObject(response);
                    objectOutputStream.flush();
                    user1 = user;

                }catch (Exception exception){
                    exception.printStackTrace();
                }

            }
            case REGISTER -> {

                try{

                    InfoStatus response = new InfoStatus(InfoStatus.types_status.REGISTER_MADE);
                    objectOutputStream.writeObject(response);
                    objectOutputStream.flush();
                    user1 = user;

                }catch (Exception exception){
                    exception.printStackTrace();
                }

            }
            case CHANGES -> {

                try{

                    InfoStatus response = new InfoStatus(InfoStatus.types_status.CHANGES_MADE);
                    objectOutputStream.writeObject(response);
                    objectOutputStream.flush();
                    user1 = user;

                }catch (Exception exception){
                    exception.printStackTrace();
                }

            }
        }

    }

}
