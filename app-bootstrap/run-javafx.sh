#!/bin/bash
# Run Pomodoro Timer with JavaFX UI

cd "$(dirname "$0")/app-bootstrap"
SPRING_PROFILES_ACTIVE=javafx java -jar target/pomodoro-timer-app-bootstrap-1.0-SNAPSHOT.jar
