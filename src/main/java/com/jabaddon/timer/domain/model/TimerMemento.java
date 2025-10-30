package com.jabaddon.timer.domain.model;

import java.time.LocalDateTime;

public record TimerMemento(
        SessionType sessionType,
        int remainingSeconds,
        LocalDateTime timestamp,
        TimerState state) {
}
