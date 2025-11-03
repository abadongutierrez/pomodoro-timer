package com.jabaddon.pomodorotimer.application.port.in;

/**
 * Driving port (input): Use case for resetting timer and session.
 */
public interface ResetTimerUseCase {
    /**
     * Stops the timer and resets the session cycle.
     */
    void reset();

    /**
     * Stops the timer.
     */
    void stop();
}
