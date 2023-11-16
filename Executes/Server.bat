@echo off

cd ..\Server_Cluster\src\main\java\

javac pt\isec\pd\Main_Server.java

java -cp "..\..\..\database\lib\sqlite-jdbc-3.43.0.0.jar;" pt.isec.pd.Main_Server 2000 ..\..\..\database\ default_rmi_service_name 6000

pause