package com.jabaddon.pomodorotimer.domain.model;

import java.time.LocalDateTime;

public record TimerMemento(
        SessionType sessionType,
        int remainingSeconds,
        LocalDateTime timestamp,
        TimerState state) {
}
