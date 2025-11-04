package com.jabaddon.pomodorotimer.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Domain entity representing a countdown timer.
 * Contains pure business logic without any framework dependencies (no JavaFX properties).
 */
class Timer {
    private int remainingSeconds;
    private int initialDurationMinutes;
    private TimerState state;
    private SessionType sessionType;
    private LocalDateTime startedAt;
    private LocalDateTime stoppedAt;
    private LocalDateTime currentPauseStartTime;
    private List<PauseRecord> pauseRecords;

    Timer() {
        this.remainingSeconds = 0;
        this.initialDurationMinutes = 0;
        this.state = TimerState.IDLE;
        this.sessionType = SessionType.WORK;
        this.startedAt = null;
        this.stoppedAt = null;
        this.currentPauseStartTime = null;
        this.pauseRecords = new ArrayList<>();
    }

    /**
     * Starts the timer with the specified duration.
     * Clears any previous history (pause records, stop time).
     * @param minutes Duration in minutes
     * @throws IllegalArgumentException if minutes <= 0
     * @throws IllegalStateException if timer is already running
     */
    void start(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Minutes must be greater than 0");
        }
        if (state == TimerState.RUNNING) {
            throw new IllegalStateException("Timer is already running");
        }
        this.remainingSeconds = minutes * 60;
        this.initialDurationMinutes = minutes;
        this.state = TimerState.RUNNING;
        this.startedAt = LocalDateTime.now();

        // Clear history from previous session
        clearHistory();
    }

    /**
     * Pauses the timer, preserving remaining time.
     * Records the pause start time for tracking.
     * @throws IllegalStateException if timer is not running
     */
    void pause() {
        if (state != TimerState.RUNNING) {
            throw new IllegalStateException("Timer is not running");
        }
        this.state = TimerState.PAUSED;
        this.currentPauseStartTime = LocalDateTime.now();
    }

    /**
     * Resumes a paused timer.
     * Records the pause duration for tracking.
     * @throws IllegalStateException if timer is not paused
     */
    void resume() {
        if (state != TimerState.PAUSED) {
            throw new IllegalStateException("Timer is not paused");
        }
        if (remainingSeconds > 0) {
            this.state = TimerState.RUNNING;

            // Record the pause event
            if (currentPauseStartTime != null) {
                pauseRecords.add(new PauseRecord(currentPauseStartTime, LocalDateTime.now()));
                currentPauseStartTime = null;
            }
        }
    }

    /**
     * Stops the timer and resets remaining time to zero.
     * Records the stop time and handles any active pause.
     */
    void stop() {
        // Record stop time if timer was started
        if (startedAt != null) {
            this.stoppedAt = LocalDateTime.now();

            // If there's an active pause, close it
            if (currentPauseStartTime != null) {
                pauseRecords.add(new PauseRecord(currentPauseStartTime, LocalDateTime.now()));
                currentPauseStartTime = null;
            }
        }

        this.state = TimerState.IDLE;
        this.remainingSeconds = 0;
    }

    /**
     * Resets the timer to the specified duration without starting it.
     * @param minutes Duration in minutes
     */
    void reset(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Minutes must be greater than 0");
        }
        this.remainingSeconds = minutes * 60;
        this.state = TimerState.READY;
    }

    /**
     * Decrements the timer by one second.
     * This should be called by the infrastructure layer (ticker).
     * @return true if timer completed (reached zero), false otherwise
     */
    boolean tick() {
        if (state != TimerState.RUNNING) {
            return false;
        }

        if (remainingSeconds > 0) {
            remainingSeconds--;
            if (remainingSeconds == 0) {
                this.state = TimerState.COMPLETED;
                return true;
            }
        }
        return false;
    }

    // Getters
    int getRemainingSeconds() {
        return remainingSeconds;
    }

    TimerState getState() {
        return state;
    }

    boolean isRunning() {
        return state == TimerState.RUNNING;
    }

    boolean isPaused() {
        return state == TimerState.PAUSED;
    }

    boolean isCompleted() {
        return state == TimerState.COMPLETED;
    }

    SessionType getSessionType() {
        return sessionType;
    }

    void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    LocalDateTime getStartedAt() {
        return startedAt;
    }

    int getInitialDurationMinutes() {
        return initialDurationMinutes;
    }

    LocalDateTime getStoppedAt() {
        return stoppedAt;
    }

    List<PauseRecord> getPauseRecords() {
        return Collections.unmodifiableList(pauseRecords);
    }

    /**
     * Clears the pause records and resets stop time.
     * Used when starting a new session.
     */
    void clearHistory() {
        this.pauseRecords.clear();
        this.stoppedAt = null;
        this.currentPauseStartTime = null;
    }
}
