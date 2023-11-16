@echo off

cd ..\Server_Cluster\src\main\java\

javac pt\isec\pd\Backup_Server.java
mkdir C:\default_backup
java pt.isec.pd.Backup_Server C:\default_backup

pause