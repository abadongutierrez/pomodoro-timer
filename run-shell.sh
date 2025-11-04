#!/bin/bash
# Run Pomodoro Timer with Spring Shell from root of the project

mvn clean build -DskipTests
SPRING_PROFILES_ACTIVE=shell mvn exec:java -pl app-bootstrap -Dexec.mainClass="com.jabaddon.pomodorotimer.PomodoroTimerApplication"
