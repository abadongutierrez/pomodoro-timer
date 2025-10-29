package com.jabaddon.timer.domain.model;

/**
 * Domain entity representing a countdown timer.
 * Contains pure business logic without any framework dependencies (no JavaFX properties).
 */
public class Timer {
    private int remainingSeconds;
    private TimerState state;
    private SessionType sessionType;

    public Timer() {
        this.remainingSeconds = 0;
        this.state = TimerState.IDLE;
        this.sessionType = SessionType.WORK;
    }

    /**
     * Starts the timer with the specified duration.
     * @param minutes Duration in minutes
     * @throws IllegalArgumentException if minutes <= 0
     * @throws IllegalStateException if timer is already running
     */
    public void start(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Minutes must be greater than 0");
        }
        if (state == TimerState.RUNNING) {
            throw new IllegalStateException("Timer is already running");
        }
        this.remainingSeconds = minutes * 60;
        this.state = TimerState.RUNNING;
    }

    /**
     * Pauses the timer, preserving remaining time.
     * @throws IllegalStateException if timer is not running
     */
    public void pause() {
        if (state != TimerState.RUNNING) {
            throw new IllegalStateException("Timer is not running");
        }
        this.state = TimerState.PAUSED;
    }

    /**
     * Resumes a paused timer.
     * @throws IllegalStateException if timer is not paused
     */
    public void resume() {
        if (state != TimerState.PAUSED) {
            throw new IllegalStateException("Timer is not paused");
        }
        if (remainingSeconds > 0) {
            this.state = TimerState.RUNNING;
        }
    }

    /**
     * Stops the timer and resets remaining time to zero.
     */
    public void stop() {
        this.state = TimerState.IDLE;
        this.remainingSeconds = 0;
    }

    /**
     * Resets the timer to the specified duration without starting it.
     * @param minutes Duration in minutes
     */
    public void reset(int minutes) {
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
    public boolean tick() {
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

    /**
     * Gets formatted time as MM:SS string.
     */
    public String getFormattedTime() {
        int mins = remainingSeconds / 60;
        int secs = remainingSeconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    // Getters
    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public TimerState getState() {
        return state;
    }

    public boolean isRunning() {
        return state == TimerState.RUNNING;
    }

    public boolean isPaused() {
        return state == TimerState.PAUSED;
    }

    public boolean isCompleted() {
        return state == TimerState.COMPLETED;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }
}
