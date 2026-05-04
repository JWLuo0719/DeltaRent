@echo off
set "ROOT_DIR=%~dp0"

echo =======================
echo  Start DeltaRent Project
echo =======================

:: Start Backend
echo.
echo [1/2] Starting Backend (Spring Boot :8080)...
start "DeltaRent-Backend" /D "%ROOT_DIR%src\server" cmd /k "gradlew.bat bootRun"

:: Wait 20s
echo   Waiting for backend startup...
timeout /t 20 /nobreak > nul

:: Start Frontend
echo.
echo [2/2] Starting Frontend (Vite :5173)...
start "DeltaRent-Frontend" /D "%ROOT_DIR%src\web" cmd /k "npm run dev"

echo.
echo =======================
echo  Backend: http://localhost:8080
echo  Frontend: http://localhost:5173
echo =======================
pause
