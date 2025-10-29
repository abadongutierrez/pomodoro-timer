package com.jabaddon.timer.domain.service;

import com.jabaddon.timer.domain.model.Session;
import com.jabaddon.timer.domain.model.SessionType;
import com.jabaddon.timer.domain.model.Timer;

/**
 * Domain service encapsulating Pomodoro session rules and business logic.
 * Coordinates interactions between Timer and Session entities.
 */
public class SessionRules {

    /**
     * Handles the completion of a timer countdown.
     * Applies Pomodoro rules: increment count if work session, determine next session type.
     *
     * @param timer Current timer
     * @param session Current session
     * @return The next session type to transition to
     */
    public SessionType handleTimerCompletion(Timer timer, Session session) {
        SessionType completedType = timer.getSessionType();

        // If work session completed, increment pomodoro count
        if (completedType == SessionType.WORK) {
            session.completeWorkSession();
        }

        // Transition to next session
        session.transitionToNextSession();
        SessionType nextType = session.getCurrentSessionType();

        // Update timer's session type
        timer.setSessionType(nextType);

        return nextType;
    }

    /**
     * Starts a new timer session with the appropriate duration.
     *
     * @param timer Timer to start
     * @param session Current session
     */
    public void startSession(Timer timer, Session session) {
        SessionType sessionType = session.getCurrentSessionType();
        int minutes = sessionType.getDefaultMinutes();

        timer.setSessionType(sessionType);
        timer.start(minutes);
    }

    /**
     * Resets timer and session to initial state.
     *
     * @param timer Timer to reset
     * @param session Session to reset
     */
    public void resetSession(Timer timer, Session session) {
        timer.stop();
        session.reset();
        timer.setSessionType(SessionType.WORK);
    }

    /**
     * Validates if a session can be started.
     *
     * @param timer Current timer
     * @return true if session can be started, false otherwise
     */
    public boolean canStartSession(Timer timer) {
        return !timer.isRunning();
    }

    /**
     * Validates if a timer can be paused.
     *
     * @param timer Current timer
     * @return true if timer can be paused, false otherwise
     */
    public boolean canPauseTimer(Timer timer) {
        return timer.isRunning();
    }

    /**
     * Validates if a timer can be resumed.
     *
     * @param timer Current timer
     * @return true if timer can be resumed, false otherwise
     */
    public boolean canResumeTimer(Timer timer) {
        return timer.isPaused() && timer.getRemainingSeconds() > 0;
    }
}
