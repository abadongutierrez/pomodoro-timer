#!/bin/bash
# Run Pomodoro Timer with Spring Shell UI

cd "$(dirname "$0")/app-bootstrap"
SPRING_PROFILES_ACTIVE=shell java -jar target/pomodoro-timer-app-bootstrap-1.0-SNAPSHOT.jar
