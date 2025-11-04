@echo off
REM Run Pomodoro Timer with Spring Shell UI

cd /d "%~dp0"
set SPRING_PROFILES_ACTIVE=shell
java -jar target\pomodoro-timer-app-bootstrap-1.0-SNAPSHOT.jar
