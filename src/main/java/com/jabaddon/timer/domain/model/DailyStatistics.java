package com.jabaddon.timer.domain.model;

import java.time.LocalDate;

/**
 * Domain entity representing daily Pomodoro statistics.
 * Pure domain model without persistence logic.
 */
public class DailyStatistics {
    private static final int POMODOROS_BEFORE_LONG_BREAK = 4;

    private final LocalDate date;
    private int completedPomodoros;
    private int currentCycle;

    public DailyStatistics(LocalDate date, int completedPomodoros) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (completedPomodoros < 0) {
            throw new IllegalArgumentException("Completed pomodoros cannot be negative");
        }
        this.date = date;
        this.completedPomodoros = completedPomodoros;
        this.currentCycle = calculateCycleFromPomodoros(completedPomodoros);
    }

    /**
     * Calculates the current cycle position based on completed pomodoros.
     * Cycle resets to 0 after every 4th pomodoro (long break).
     *
     * @param completedPomodoros The number of completed pomodoros
     * @return The current cycle (0-3)
     */
    private static int calculateCycleFromPomodoros(int completedPomodoros) {
        return completedPomodoros % POMODOROS_BEFORE_LONG_BREAK;
    }

    public static DailyStatistics empty(LocalDate date) {
        return new DailyStatistics(date, 0);
    }

    public static DailyStatistics today() {
        return empty(LocalDate.now());
    }

    public void incrementPomodoros() {
        this.completedPomodoros++;
        this.currentCycle = calculateCycleFromPomodoros(this.completedPomodoros);
    }

    public void setCompletedPomodoros(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Completed pomodoros cannot be negative");
        }
        this.completedPomodoros = count;
        this.currentCycle = calculateCycleFromPomodoros(count);
    }

    public LocalDate getDate() {
        return date;
    }

    public int getCompletedPomodoros() {
        return completedPomodoros;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public boolean isToday() {
        return date.equals(LocalDate.now());
    }

    @Override
    public String toString() {
        return "DailyStatistics{" +
                "date=" + date +
                ", completedPomodoros=" + completedPomodoros +
                ", currentCycle=" + currentCycle +
                '}';
    }
}
