package com.jabaddon.pomodorotimer.application.dto;

import java.time.LocalDate;

/**
 * Application DTO representing daily Pomodoro statistics.
 * Used as a contract between application layer and adapters.
 * This is a simple data holder without business logic.
 */
public class DailyStatisticsDTO {
    private LocalDate date;
    private int completedPomodoros;
    private int currentCycle;

    // Default constructor for Jackson
    public DailyStatisticsDTO() {
    }

    public DailyStatisticsDTO(LocalDate date, int completedPomodoros, int currentCycle) {
        this.date = date;
        this.completedPomodoros = completedPomodoros;
        this.currentCycle = currentCycle;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getCompletedPomodoros() {
        return completedPomodoros;
    }

    public void setCompletedPomodoros(int completedPomodoros) {
        this.completedPomodoros = completedPomodoros;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public void setCurrentCycle(int currentCycle) {
        this.currentCycle = currentCycle;
    }
}
