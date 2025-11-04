package com.jabaddon.pomodorotimer.application.dto;

public enum TimerStateDTO {
    IDLE,       // Timer is not running and has no time set
    READY,      // Timer has time set but not running
    RUNNING,    // Timer is actively counting down
    PAUSED,     // Timer is paused with time remaining
    COMPLETED   // Timer has finished counting down
}
