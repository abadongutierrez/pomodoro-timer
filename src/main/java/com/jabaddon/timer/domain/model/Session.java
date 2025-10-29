package com.jabaddon.timer.domain.model;

/**
 * Domain entity representing a Pomodoro session tracker.
 * Manages cycles, completed pomodoros, and session transitions.
 * Pure domain logic without framework dependencies.
 */
public class Session {
    private static final int POMODOROS_BEFORE_LONG_BREAK = 4;

    private int completedPomodoros;
    private int currentCycle;
    private SessionType currentSessionType;

    public Session() {
        this.completedPomodoros = 0;
        this.currentCycle = 0;
        this.currentSessionType = SessionType.WORK;
    }

    /**
     * Marks a work session as completed.
     * Increments pomodoro count and cycle if current session is WORK.
     */
    public void completeWorkSession() {
        if (currentSessionType == SessionType.WORK) {
            completedPomodoros++;
            currentCycle++;
        }
    }

    /**
     * Determines the next session type based on Pomodoro rules.
     * Work -> Short Break (cycles 1-3)
     * Work -> Long Break (cycle 4)
     * Break -> Work
     */
    public SessionType getNextSessionType() {
        if (currentSessionType == SessionType.WORK) {
            if (currentCycle >= POMODOROS_BEFORE_LONG_BREAK) {
                return SessionType.LONG_BREAK;
            } else {
                return SessionType.SHORT_BREAK;
            }
        } else {
            return SessionType.WORK;
        }
    }

    /**
     * Transitions to the next session type and resets cycle if needed.
     */
    public void transitionToNextSession() {
        SessionType nextType = getNextSessionType();

        // Reset cycle after long break
        if (nextType == SessionType.LONG_BREAK) {
            currentCycle = 0;
        }

        currentSessionType = nextType;
    }

    /**
     * Resets the current cycle and returns to work session.
     * Does not reset completed pomodoros count.
     */
    public void reset() {
        this.currentCycle = 0;
        this.currentSessionType = SessionType.WORK;
    }

    /**
     * Resets the entire session including daily pomodoro count.
     * Used for starting a new day.
     */
    public void resetDaily() {
        this.completedPomodoros = 0;
        reset();
    }

    // Getters and Setters
    public int getCompletedPomodoros() {
        return completedPomodoros;
    }

    public void setCompletedPomodoros(int completedPomodoros) {
        this.completedPomodoros = completedPomodoros;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public SessionType getCurrentSessionType() {
        return currentSessionType;
    }

    public void setCurrentSessionType(SessionType currentSessionType) {
        this.currentSessionType = currentSessionType;
    }
}
