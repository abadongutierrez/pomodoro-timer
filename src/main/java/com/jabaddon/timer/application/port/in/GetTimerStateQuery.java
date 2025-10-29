package com.jabaddon.timer.application.port.in;

import com.jabaddon.timer.domain.model.SessionType;
import com.jabaddon.timer.domain.model.TimerState;

/**
 * Driving port (input): Query for retrieving current timer state.
 * This follows CQRS pattern - queries are separate from commands.
 */
public interface GetTimerStateQuery {
    /**
     * Gets the current timer state as a DTO suitable for UI display.
     */
    TimerStateDTO getCurrentState();

    /**
     * Data Transfer Object containing all timer state information.
     * Immutable snapshot of timer state for UI consumption.
     */
    class TimerStateDTO {
        private final int remainingSeconds;
        private final String formattedTime;
        private final TimerState state;
        private final SessionType sessionType;
        private final int completedPomodoros;
        private final int currentCycle;

        public TimerStateDTO(
                int remainingSeconds,
                String formattedTime,
                TimerState state,
                SessionType sessionType,
                int completedPomodoros,
                int currentCycle) {
            this.remainingSeconds = remainingSeconds;
            this.formattedTime = formattedTime;
            this.state = state;
            this.sessionType = sessionType;
            this.completedPomodoros = completedPomodoros;
            this.currentCycle = currentCycle;
        }

        public int getRemainingSeconds() {
            return remainingSeconds;
        }

        public String getFormattedTime() {
            return formattedTime;
        }

        public TimerState getState() {
            return state;
        }

        public SessionType getSessionType() {
            return sessionType;
        }

        public int getCompletedPomodoros() {
            return completedPomodoros;
        }

        public int getCurrentCycle() {
            return currentCycle;
        }

        public boolean isRunning() {
            return state == TimerState.RUNNING;
        }

        public boolean isPaused() {
            return state == TimerState.PAUSED;
        }
    }
}
