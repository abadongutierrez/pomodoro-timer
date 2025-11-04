@echo off
REM Run Pomodoro Timer with JavaFX UI

cd /d "%~dp0"
set SPRING_PROFILES_ACTIVE=javafx
java -jar target\pomodoro-timer-app-bootstrap-1.0-SNAPSHOT.jar
