package com.jabaddon.timer.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain entity representing a timer session (completed or stopped).
 * Records start time, finish time, reason for ending, and all pause events.
 */
public class TimerRecord {
    private final LocalDateTime startedAt;
    private final LocalDateTime finishedAt;
    private final FinishReason reason;
    private final SessionType sessionType;
    private final int durationMinutes;
    private final String description;
    private final List<PauseRecord> pauseRecords;

    // jackson required
    public TimerRecord() {
        this.startedAt = null;
        this.finishedAt = null;
        this.reason = null;
        this.sessionType = null;
        this.durationMinutes = 0;
        this.description = "";
        this.pauseRecords = new ArrayList<>();
    }

    /**
     * Creates a new timer record.
     *
     * @param startedAt The timestamp when the timer started
     * @param finishedAt The timestamp when the timer finished (completed or stopped)
     * @param reason The reason the timer ended (COMPLETED or STOPPED)
     * @param sessionType The type of session (WORK, SHORT_BREAK, LONG_BREAK)
     * @param durationMinutes The duration of the session in minutes
     * @param description Optional description of the session (can be empty string)
     * @param pauseRecords List of pause events during the session
     */
    public TimerRecord(
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        FinishReason reason,
        SessionType sessionType,
        int durationMinutes,
        String description,
        List<PauseRecord> pauseRecords
    ) {
        if (startedAt == null) {
            throw new IllegalArgumentException("startedAt cannot be null");
        }
        if (finishedAt == null) {
            throw new IllegalArgumentException("finishedAt cannot be null");
        }
        if (reason == null) {
            throw new IllegalArgumentException("reason cannot be null");
        }
        if (sessionType == null) {
            throw new IllegalArgumentException("sessionType cannot be null");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("durationMinutes must be greater than 0");
        }
        if (startedAt.isAfter(finishedAt)) {
            throw new IllegalArgumentException("startedAt cannot be after finishedAt");
        }

        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.reason = reason;
        this.sessionType = sessionType;
        this.durationMinutes = durationMinutes;
        this.description = description != null ? description : "";
        this.pauseRecords = pauseRecords != null
            ? new ArrayList<>(pauseRecords)
            : new ArrayList<>();
    }

    /**
     * Checks if the timer was completed normally.
     *
     * @return true if completed, false if stopped
     */
    public boolean wasCompleted() {
        return reason == FinishReason.COMPLETED;
    }

    /**
     * Checks if the timer was stopped before completion.
     *
     * @return true if stopped, false if completed
     */
    public boolean wasStopped() {
        return reason == FinishReason.STOPPED;
    }

    /**
     * Calculates the total time spent paused during this session.
     *
     * @return Total pause duration in seconds
     */
    public long getTotalPausedSeconds() {
        return pauseRecords.stream()
            .filter(pause -> !pause.isStillPaused())
            .mapToLong(PauseRecord::getPauseDurationSeconds)
            .sum();
    }

    /**
     * Gets the number of times the timer was paused.
     *
     * @return The count of pause events
     */
    public int getPauseCount() {
        return pauseRecords.size();
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public FinishReason getReason() {
        return reason;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public List<PauseRecord> getPauseRecords() {
        return Collections.unmodifiableList(pauseRecords);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimerRecord that = (TimerRecord) o;
        return durationMinutes == that.durationMinutes &&
               Objects.equals(startedAt, that.startedAt) &&
               Objects.equals(finishedAt, that.finishedAt) &&
               reason == that.reason &&
               sessionType == that.sessionType &&
               Objects.equals(description, that.description) &&
               Objects.equals(pauseRecords, that.pauseRecords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startedAt, finishedAt, reason, sessionType,
                           durationMinutes, description, pauseRecords);
    }

    @Override
    public String toString() {
        return "TimerRecord{" +
               "startedAt=" + startedAt +
               ", finishedAt=" + finishedAt +
               ", reason=" + reason +
               ", sessionType=" + sessionType +
               ", durationMinutes=" + durationMinutes +
               ", description='" + description + '\'' +
               ", pauseCount=" + getPauseCount() +
               ", totalPausedSeconds=" + getTotalPausedSeconds() +
               '}';
    }
}
