@echo off
REM Run Pomodoro Timer with JavaFX UI

mvn clean build -DskipTests
set SPRING_PROFILES_ACTIVE=javafx
mvn exec:java -pl app-bootstrap -Dexec.mainClass="com.jabaddon.pomodorotimer.PomodoroTimerApplication"
