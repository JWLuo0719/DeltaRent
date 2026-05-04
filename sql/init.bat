@echo off
chcp 65001 > nul
echo ==============================
echo  Initializing database deltarent
echo ==============================

mysql -u root -p123456 -e "DROP DATABASE IF EXISTS deltarent;"
mysql -u root -p123456 < "%~dp0schema_v1.sql"

echo.
echo Initialization completed!
echo.
pause