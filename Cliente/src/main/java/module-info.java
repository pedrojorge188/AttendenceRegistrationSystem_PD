module pt.isec.pd.attendence_registration_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens pt.isec.pd.attendence_registration_system to javafx.fxml;
    exports pt.isec.pd.attendence_registration_system;
    exports pt.isec.pd.attendence_registration_system.Controllers;
    opens pt.isec.pd.attendence_registration_system.Controllers to javafx.fxml;
}