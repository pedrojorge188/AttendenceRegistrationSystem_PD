@echo off

cd ..\Cliente

mvn clean javafx:run -Dexec.args="localhost 2000"

pause
