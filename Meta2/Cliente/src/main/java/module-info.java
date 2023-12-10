module pt.isec.pd.attendence_registration_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.json;
    requires com.google.gson;


    exports pt.isec.pd.Controllers;
    opens pt.isec.pd.Controllers to javafx.fxml;
    exports pt.isec.pd;
    opens pt.isec.pd to javafx.fxml;
}