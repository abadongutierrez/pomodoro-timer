package com.jabaddon.pomodorotimer.application.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Application DTO representing a timer session record.
 * Used as a contract between application layer and adapters (especially persistence).
 * This is a simple data holder without business logic.
 */
public class TimerRecordDTO {
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private FinishReasonDTO reason;
    private SessionTypeDTO sessionType;
    private int durationMinutes;
    private String description;
    private List<PauseRecordDTO> pauseRecords;

    // Default constructor for Jackson
    public TimerRecordDTO() {
        this.pauseRecords = new ArrayList<>();
    }

    public TimerRecordDTO(
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        FinishReasonDTO reason,
        SessionTypeDTO sessionType,
        int durationMinutes,
        String description,
        List<PauseRecordDTO> pauseRecords
    ) {
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.reason = reason;
        this.sessionType = sessionType;
        this.durationMinutes = durationMinutes;
        this.description = description;
        this.pauseRecords = pauseRecords != null ? pauseRecords : new ArrayList<>();
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public FinishReasonDTO getReason() {
        return reason;
    }

    public void setReason(FinishReasonDTO reason) {
        this.reason = reason;
    }

    public SessionTypeDTO getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionTypeDTO sessionType) {
        this.sessionType = sessionType;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PauseRecordDTO> getPauseRecords() {
        return pauseRecords;
    }

    public void setPauseRecords(List<PauseRecordDTO> pauseRecords) {
        this.pauseRecords = pauseRecords;
    }
}
