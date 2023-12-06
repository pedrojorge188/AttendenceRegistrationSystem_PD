@echo off
java --module-path ./javafx/lib --add-modules javafx.controls,javafx.fxml -jar ./jars/Client.jar localhost 2000
pause