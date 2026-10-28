@echo off
title SmartShelf 5.0 - Application Launcher
echo ========================================================
echo Starting SmartShelf 5.0 (Spring Boot Backend + React Frontend)
echo ========================================================

start "SmartShelf 5.0 Backend" cmd /k "cd /d %~dp0backend && mvn spring-boot:run"
timeout /t 6 /nobreak >nul
start "SmartShelf 5.0 Frontend" cmd /k "cd /d %~dp0frontend && npm run dev"

echo.
echo SmartShelf 5.0 servers launched!
echo - Backend API:  http://localhost:8093/api
echo - Frontend App: http://localhost:5173
echo.
pause
