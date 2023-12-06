package pt.isec.pd.models;

import java.io.Serializable;

public class User implements Serializable {
    static final long SerialVersionUID = 1L;

    public enum types_msg{
        LOGIN,
        REGISTER,
        CHANGES
    }

    private types_msg type;
    private String name;
    private int Student_uid;
    private String username_email;
    private String password;

    public User(types_msg type, int uid, String name, String username_email, String password) {
        this.type = type;
        this.name = name;
        Student_uid = uid;
        this.username_email = username_email;
        this.password = password;
    }

    public types_msg getType(){
        return type;
    }

    public int getStudent_uid() {
        return Student_uid;
    }

    public void setStudent_uid(int student_uid) {
        Student_uid = student_uid;
    }

    public String getUsername_email() {
        return username_email;
    }

    public void setUsername_email(String username_email) {
        this.username_email = username_email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
