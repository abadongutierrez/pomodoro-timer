#!/bin/bash
# Run Pomodoro Timer with JavaFX UI from root of the project

mvn clean build -DskipTests
SPRING_PROFILES_ACTIVE=javafx mvn exec:java -pl app-bootstrap -Dexec.mainClass="com.jabaddon.pomodorotimer.PomodoroTimerApplication"
