package com.jabaddon.pomodorotimer.domain.model;

import java.time.LocalDateTime;

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
    private int currentSessionTypeMinutes;
    private Timer timer;
    private SessionDomainEventHandler eventHandler;

    public Session() {
        this.completedPomodoros = 0;
        this.currentCycle = 0;
        this.currentSessionType = SessionType.WORK; // default session type to start
        this.timer = new Timer();
        this.eventHandler = new NoOpSessionDomainEventHandler();
    }

    public Session(SessionDomainEventHandler customEventHandler) {
        this();
        this.eventHandler = customEventHandler;
    }

    private void completeWorkSession() {
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
    private SessionType getNextSessionType() {
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
    private void transitionToNextSession() {
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

    public int getCurrentCycle() {
        return currentCycle;
    }

    public SessionType getCurrentSessionType() {
        return currentSessionType;
    }

    public int getCurrentSessionTypeMinutes() {
        return currentSessionTypeMinutes;
    }

    public boolean isTimerRunning() {
        return timer.isRunning();
    }

    public boolean isTimerPaused() {
        return timer.isPaused();
    }

    public boolean startSession() {
        if (timer.isRunning()) {
            return false;
        }
        SessionType sessionType = getCurrentSessionType();
        currentSessionTypeMinutes = sessionType.getDefaultMinutes();
        timer.setSessionType(sessionType);
        timer.start(currentSessionTypeMinutes);
        eventHandler.onSessionStarted(sessionType, currentSessionTypeMinutes);
        return true;
    }

    public boolean startCustomSession(int minutes) {
        if (timer.isRunning()) {
            return false;
        }
        SessionType sessionType = getCurrentSessionType();
        currentSessionTypeMinutes = minutes;
        timer.setSessionType(sessionType);
        timer.start(currentSessionTypeMinutes);
        eventHandler.onSessionStarted(sessionType, currentSessionTypeMinutes);
        return true;
    }

    public void resetTimer() {
        timer.stop();
        reset();
        timer.setSessionType(SessionType.WORK);
    }

    public boolean pauseTimer() {
        if (timer.isRunning()) {
            timer.pause();
            return true;
        }
        return false;
    }

    public boolean resumeTimer() {
        if (timer.isPaused()) {
            timer.resume();
            return true;
        }
        return false;
    }

    public boolean isTimerStarted() {
        return timer.getStartedAt() != null && timer.getInitialDurationMinutes() > 0;
    }

    public TimerRecord createTimerRecord(LocalDateTime finishedAt) {
        return new TimerRecord(
                timer.getStartedAt(),
                finishedAt,
                isTimerStarted() && isTimerCompleted() ?
                    FinishReason.COMPLETED : FinishReason.STOPPED,
                timer.getSessionType(),
                timer.getInitialDurationMinutes(),
                "",    // Empty description for now
                timer.getPauseRecords()  // Include all pause events
        );
    }

    public boolean tick() {
        return timer.tick();
    }

    public boolean isTimerCompleted() {
        return timer.isCompleted();
    }

    public boolean wasTimerStopped() {
        return !timer.isRunning() && !timer.isCompleted() && timer.getStartedAt() != null;
    }

    public SessionType timerCurrentSessionType() {
        return timer.getSessionType();
    }

    public SessionType handleTimerCompletion() {
        if (!timer.isCompleted()) throw new IllegalStateException("Timer not completed");

        SessionType completedType = timer.getSessionType();

        if (completedType == SessionType.WORK) {
            completeWorkSession();
        }

        // Transition to next session
        transitionToNextSession();
        SessionType nextType = getCurrentSessionType();

        timer.setSessionType(nextType);

        return nextType;
    }

    public TimerMemento createTimerMemento() {
        return new TimerMemento(
                timer.getSessionType(),
                timer.getRemainingSeconds(),
                LocalDateTime.now(),
                timer.getState()
        );
    }

    public void initializeFromTodayStats(DailyStatistics todayStats) {
        this.completedPomodoros = todayStats.getCompletedPomodoros();
        this.currentCycle = todayStats.getCurrentCycle();
    }

    private class NoOpSessionDomainEventHandler implements SessionDomainEventHandler {

        @Override
        public void onSessionStarted(SessionType sessionType, int minutes) {
        }
    }
}
