package com.jabaddon.timer.domain.model;

/**
 * Value object representing the current state of the timer.
 * Pure domain concept with no framework dependencies.
 */
public enum TimerState {
    IDLE,       // Timer is not running and has no time set
    READY,      // Timer has time set but not running
    RUNNING,    // Timer is actively counting down
    PAUSED,     // Timer is paused with time remaining
    COMPLETED   // Timer has finished counting down
}
