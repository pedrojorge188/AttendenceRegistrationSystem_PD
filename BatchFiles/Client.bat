@echo off

cd ..\Client

mvn clean javafx:run -Dexec.args="localhost 2000"

pause
