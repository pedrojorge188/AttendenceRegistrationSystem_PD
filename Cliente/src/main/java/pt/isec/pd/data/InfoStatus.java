package pt.isec.pd.data;

import java.io.Serializable;

public class InfoStatus implements Serializable {

    static final long SerialVersionUID = 1L;
    private String msg_log;
    private types_status status;

    public enum types_status{
        LOGIN_MADE_USER,
        LOGIN_MADE_ADMIN,
        LOGIN_FAIL,
        REGISTER_MADE,
        REGISTER_FAIL,
        CHANGES_MADE,
        CHAGES_FAIL,
        MSG_STACK
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
}
