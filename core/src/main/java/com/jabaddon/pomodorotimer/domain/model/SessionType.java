package com.jabaddon.pomodorotimer.domain.model;

import java.util.Optional;

/**
 * Value object representing the type of Pomodoro session.
 * This is a pure domain concept with no framework dependencies.
 */
public enum SessionType {
    WORK(25, "Work Session"),
    SHORT_BREAK(5, "Short Break"),
    LONG_BREAK(15, "Long Break");

    private final int defaultMinutes;
    private final String displayName;

    SessionType(int defaultMinutes, String displayName) {
        this.defaultMinutes = defaultMinutes;
        this.displayName = displayName;
    }

    public int getDefaultMinutes() {
        return defaultMinutes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCustom(int minutes) {
        return defaultMinutes != minutes;
    }
}
