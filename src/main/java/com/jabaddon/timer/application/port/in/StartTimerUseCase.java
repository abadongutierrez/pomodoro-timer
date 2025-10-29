package com.jabaddon.timer.application.port.in;

/**
 * Driving port (input): Use case for starting a timer session.
 * This interface defines what the UI can do with the timer.
 */
public interface StartTimerUseCase {
    /**
     * Starts a new timer session with default duration for current session type.
     */
    void startSession();

    /**
     * Starts a custom timer with specified duration.
     * @param minutes Duration in minutes
     */
    void startCustomTimer(int minutes);
}
