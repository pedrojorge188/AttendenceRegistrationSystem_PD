module pt.isec.pd.attendence_registration_system {
    requires javafx.controls;
    requires javafx.fxml;


    opens pt.isec.pd.attendence_registration_system to javafx.fxml;
    exports pt.isec.pd.attendence_registration_system;
}