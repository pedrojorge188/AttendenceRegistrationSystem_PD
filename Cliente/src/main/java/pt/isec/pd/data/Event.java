package pt.isec.pd.data;

import java.io.Serializable;

public class Event implements Serializable {

    static final long SerialVersionUID = 1L;

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
