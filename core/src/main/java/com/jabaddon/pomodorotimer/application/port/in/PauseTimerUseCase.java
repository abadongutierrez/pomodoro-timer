package com.jabaddon.pomodorotimer.application.port.in;

/**
 * Driving port (input): Use case for pausing a running timer.
 */
public interface PauseTimerUseCase {
    /**
     * Pauses the currently running timer.
     */
    void pause();

    /**
     * Resumes a paused timer.
     */
    void resume();
}
