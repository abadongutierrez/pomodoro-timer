package com.jabaddon.pomodorotimer.application.dto;

/**
 * Application DTO representing the type of Pomodoro session.
 * Used as a contract between application layer and adapters.
 */
public enum SessionTypeDTO {
    WORK,
    SHORT_BREAK,
    LONG_BREAK
}
