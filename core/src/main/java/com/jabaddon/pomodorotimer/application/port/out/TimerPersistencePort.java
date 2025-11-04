package com.jabaddon.pomodorotimer.application.port.out;

import com.jabaddon.pomodorotimer.application.dto.DailyStatisticsDTO;
import com.jabaddon.pomodorotimer.application.dto.TimerRecordDTO;

public interface TimerPersistencePort {
    DailyStatisticsDTO loadTodayStatistics();

    void saveRecord(TimerRecordDTO recordDto);
}
