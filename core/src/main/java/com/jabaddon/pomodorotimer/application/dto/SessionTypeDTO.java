package com.jabaddon.pomodorotimer.application.dto;

/**
 * Application DTO representing the type of Pomodoro session.
 * Used as a contract between application layer and adapters.
 */
public record SessionTypeDTO(SessionTypeEnumDTO sessionType, int defaultMinutes, String displayName) {
}
