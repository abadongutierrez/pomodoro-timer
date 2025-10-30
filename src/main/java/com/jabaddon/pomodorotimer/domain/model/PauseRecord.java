package com.jabaddon.pomodorotimer.domain.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Objects;

/**
 * Domain value object representing a pause event during a timer session.
 * Records when the timer was paused and when it was resumed.
 */
public class PauseRecord {
    private final LocalDateTime pausedAt;
    private final LocalDateTime unpausedAt;

    /**
     * Creates a new pause record.
     *
     * @param pausedAt The timestamp when the timer was paused
     * @param unpausedAt The timestamp when the timer was resumed (can be null if still paused)
     */
    public PauseRecord(LocalDateTime pausedAt, LocalDateTime unpausedAt) {
        if (pausedAt == null) {
            throw new IllegalArgumentException("pausedAt cannot be null");
        }
        if (unpausedAt != null && pausedAt.isAfter(unpausedAt)) {
            throw new IllegalArgumentException("pausedAt cannot be after unpausedAt");
        }

        this.pausedAt = pausedAt;
        this.unpausedAt = unpausedAt;
    }

    /**
     * Calculates the duration of this pause in seconds.
     * If the timer is still paused (unpausedAt is null), returns -1.
     *
     * @return The pause duration in seconds, or -1 if still paused
     */
    public long getPauseDurationSeconds() {
        if (unpausedAt == null) {
            return -1; // Still paused
        }
        return Duration.between(pausedAt, unpausedAt).getSeconds();
    }

    /**
     * Checks if this pause is still active (not yet resumed).
     *
     * @return true if still paused, false if resumed
     */
    public boolean isStillPaused() {
        return unpausedAt == null;
    }

    public LocalDateTime getPausedAt() {
        return pausedAt;
    }

    public LocalDateTime getUnpausedAt() {
        return unpausedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PauseRecord that = (PauseRecord) o;
        return Objects.equals(pausedAt, that.pausedAt) &&
               Objects.equals(unpausedAt, that.unpausedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pausedAt, unpausedAt);
    }

    @Override
    public String toString() {
        return "PauseRecord{" +
               "pausedAt=" + pausedAt +
               ", unpausedAt=" + unpausedAt +
               ", duration=" + (isStillPaused() ? "ongoing" : getPauseDurationSeconds() + "s") +
               '}';
    }
}
