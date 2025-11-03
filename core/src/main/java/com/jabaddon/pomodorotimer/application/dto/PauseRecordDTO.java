package com.jabaddon.pomodorotimer.application.dto;

import java.time.LocalDateTime;

/**
 * Application DTO representing a pause event during a timer session.
 * Used as a contract between application layer and adapters.
 */
public class PauseRecordDTO {
    private LocalDateTime pausedAt;
    private LocalDateTime unpausedAt;

    // Default constructor for Jackson
    public PauseRecordDTO() {
    }

    public PauseRecordDTO(LocalDateTime pausedAt, LocalDateTime unpausedAt) {
        this.pausedAt = pausedAt;
        this.unpausedAt = unpausedAt;
    }

    public LocalDateTime getPausedAt() {
        return pausedAt;
    }

    public void setPausedAt(LocalDateTime pausedAt) {
        this.pausedAt = pausedAt;
    }

    public LocalDateTime getUnpausedAt() {
        return unpausedAt;
    }

    public void setUnpausedAt(LocalDateTime unpausedAt) {
        this.unpausedAt = unpausedAt;
    }
}
