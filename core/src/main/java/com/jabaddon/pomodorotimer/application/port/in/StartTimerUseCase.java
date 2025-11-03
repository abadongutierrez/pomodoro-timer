package com.jabaddon.pomodorotimer.application.port.in;

/**
 * Driving port (input): Use case for starting a timer session.
 * This interface defines what the UI can do with the timer.
 */
public interface StartTimerUseCase {
    void startNormalTimer();

    void startCustomTimer(int minutes);
}
