package com.jabaddon.timer.domain.model;

import java.time.LocalDate;

/**
 * Domain entity representing daily Pomodoro statistics.
 * Pure domain model without persistence logic.
 */
public class DailyStatistics {
    private final LocalDate date;
    private int completedPomodoros;

    public DailyStatistics(LocalDate date, int completedPomodoros) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (completedPomodoros < 0) {
            throw new IllegalArgumentException("Completed pomodoros cannot be negative");
        }
        this.date = date;
        this.completedPomodoros = completedPomodoros;
    }

    public static DailyStatistics empty(LocalDate date) {
        return new DailyStatistics(date, 0);
    }

    public static DailyStatistics today() {
        return empty(LocalDate.now());
    }

    public void incrementPomodoros() {
        this.completedPomodoros++;
    }

    public void setCompletedPomodoros(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Completed pomodoros cannot be negative");
        }
        this.completedPomodoros = count;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getCompletedPomodoros() {
        return completedPomodoros;
    }

    public boolean isToday() {
        return date.equals(LocalDate.now());
    }

    @Override
    public String toString() {
        return "DailyStatistics{" +
                "date=" + date +
                ", completedPomodoros=" + completedPomodoros +
                '}';
    }
}
