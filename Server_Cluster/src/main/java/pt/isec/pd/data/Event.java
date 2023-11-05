package pt.isec.pd.data;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {

    static final long SerialVersionUID = 1L;

    private String csv_msg;
    private String event_identify;
    private String event_name;
    private String event_location;
    private String event_date;
    private String event_start_time;
    private String event_end_time;
    private String user_email;
    private int attend_code;
    private type_event type;

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_start_time() {
        return event_start_time;
    }

    public void setEvent_start_time(String event_start_time) {
        this.event_start_time = event_start_time;
    }

    public String getEvent_end_time() {
        return event_end_time;
    }

    public void setEvent_end_time(String event_end_time) {
        this.event_end_time = event_end_time;
    }

    public enum type_event{
        CODE_EVENT,
        REQUEST_CSV_EVENT,
        GET_ATTENDANCE_HISTORY,
        CREATE_EVENT,
        EDIT_EVENT,
        DELETE_EVENT,
        DELETE_ATTENDANCE,
        INSERT_ATTENDANCE,
        LIST_CREATED_EVENTS,
        LIST_CREATED_EVENTS_BY_USER,
        GENERATE_CODE,
        LIST_REGISTERED_ATTENDANCE,
        ASSOC_USER_EVENT
    }


    public Event(type_event type, int attend_code) {
        this.attend_code = attend_code;
        this.type = type;
        csv_msg = "";
    }

    public int getAttend_code() {
        return attend_code;
    }

    public String getEvent_identify() {
        return event_identify;
    }

    public String getCsv_msg() {
        return csv_msg;
    }

    public void setCsv_msg(String csv_msg) {
        this.csv_msg = csv_msg;
    }

    public void setEvent_identify(String event_identify) {
        this.event_identify = event_identify;
    }

    public type_event getType() {
        return type;
    }

    public void setType(type_event type) {
        this.type = type;
    }

    public void setAttend_code(int attend_code) {
        this.attend_code = attend_code;
    }
}
