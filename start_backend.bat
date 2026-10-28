@echo off
title SmartShelf 5.0 - Spring Boot Backend
echo ========================================================
echo Starting SmartShelf 5.0 Spring Boot Backend on port 8093
echo ========================================================
cd /d "%~dp0backend"
mvn spring-boot:run
pause
