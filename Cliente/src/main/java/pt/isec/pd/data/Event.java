package pt.isec.pd.data;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {

    static final long SerialVersionUID = 1L;

    private String event_name;
    private String event_location;
    private String event_date;
    private String event_start_time;
    private String event_end_time;

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
        LIST_CREATED_EVENTS,
        GENERATE_CODE,
        LIST_REGISTERED_ATTENDANCE
    }

    private int attend_code;
    private type_event type;

    public Event(type_event type, int attend_code) {
        this.attend_code = attend_code;
        this.type = type;
    }

    public int getAttend_code() {
        return attend_code;
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
