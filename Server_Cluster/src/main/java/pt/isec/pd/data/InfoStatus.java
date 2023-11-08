package pt.isec.pd.data;

import java.io.Serializable;
import java.util.List;

public class InfoStatus implements Serializable {

    static final long SerialVersionUID = 1L;
    private String msg_log;
    private List<String> eventsName;
    private List<String> attendanceRecords;
    private types_status status;

    public enum types_status{
        LOGIN_MADE_USER,
        LOGIN_MADE_ADMIN,
        LOGIN_FAIL,
        REGISTER_MADE,
        REGISTER_FAIL,
        CHANGES_MADE,
        CHANGES_FAIL,
        CODE_SEND_MADE,
        CODE_SEND_FAIL,
        EDIT_EVENT_MADE,
        EDIT_EVENT_FAIL,
        CREATE_EVENT_MADE,
        CREATE_EVENT_FAIL,
        DELETE_EVENT_MADE,
        DELETE_EVENT_FAIL,
        DELETE_ATTENDANCE_MADE,
        DELETE_ATTENDANCE_FAIL,
        INSERT_ATTENDANCE_MADE,
        INSERT_ATTENDANCE_FAIL,
        GENERATE_CODE_MADE,
        GENERATE_CODE_FAIL,
        REQUEST_CSV_EVENT,
        LIST_CREATED_EVENTS,
        LIST_CREATED_EVENTS_FAIL,
        GET_HISTORY,
        GET_HISTORY_FAIL,
        LIST_REGISTERED_ATTENDANCE,
        LIST_REGISTERED_ATTENDANCE_FAIL,
        ASSOC_USER_EVENT_MADE,
        ASSOC_USER_EVENT_FAIL,
        MSG_STACK;

    }

    public InfoStatus(types_status status) {
        this.status = status;
    }

    public String getMsg_log() {
        return msg_log;
    }

    public void setMsg_log(String msg_log) {
        this.msg_log = msg_log;
    }

    public types_status getStatus() {
        return status;
    }

    public void setStatus(types_status status) {
        this.status = status;
    }

    public List<String> getEventsName() {return eventsName;}
    public void setEventsName(List<String> eventsName){ this.eventsName = eventsName;}

    public List<String> getAttendanceRecords() {return attendanceRecords;}
    public void setAttendanceRecords(List<String> attendanceRecords){ this.attendanceRecords = attendanceRecords;}
}