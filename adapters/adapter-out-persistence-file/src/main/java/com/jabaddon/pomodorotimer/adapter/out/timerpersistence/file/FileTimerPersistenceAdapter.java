package com.jabaddon.pomodorotimer.adapter.out.timerpersistence.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jabaddon.pomodorotimer.application.dto.*;
import com.jabaddon.pomodorotimer.application.port.out.TimerPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileTimerPersistenceAdapter implements TimerPersistencePort {
    private static final Logger log = LoggerFactory.getLogger(FileTimerPersistenceAdapter.class);
    private final Path historyFilePath;
    private final ObjectMapper objectMapper;

    public FileTimerPersistenceAdapter(PersistenceConfiguration appConfig, ObjectMapper objectMapper) {
        Path timerDir = appConfig.getDataDirectoryPath();
        this.historyFilePath = timerDir.resolve(appConfig.getHistoryFile());
        this.objectMapper = objectMapper;

        try {
            if (!Files.exists(timerDir)) {
                Files.createDirectories(timerDir);
                log.info("Created timer directory: {}", timerDir);
            }
        } catch (IOException e) {
            log.error("Failed to create timer directory: {}", e.getMessage(), e);
        }

    }

    @Override
    public DailyStatisticsDTO loadTodayStatistics() {
        return loadTodayStatistics(LocalDate.now());
    }

    public void saveRecord(TimerRecordDTO record) {
        try {
            List<TimerRecordDTO> records = this.loadAllRecords();
            records.add(record);
            records.sort(Comparator.comparing(TimerRecordDTO::getFinishedAt).reversed());
            String json = this.objectMapper.writeValueAsString(records);
            Files.writeString(this.historyFilePath, json);
            log.debug("Saved timer record: {}", record);
        } catch (IOException e) {
            log.error("Failed to save timer record: {}", e.getMessage(), e);
        }

    }

    public List<TimerRecordDTO> loadAllRecords() {
        if (!Files.exists(this.historyFilePath)) {
            return new ArrayList<>();
        } else {
            try {
                String json = Files.readString(this.historyFilePath);
                if (json.trim().isEmpty()) {
                    return new ArrayList<>();
                } else {
                    List<TimerRecordDTO> records = this.objectMapper.readValue(json, new TypeReference<>() {
                    });
                    return records != null ? records : new ArrayList<>();
                }
            } catch (IOException e) {
                log.error("Failed to load timer records: {}", e.getMessage(), e);
                return new ArrayList<>();
            }
        }
    }

    public List<TimerRecordDTO> loadRecordsByDate(LocalDate date) {
        return this.loadAllRecords().stream().filter((record) ->
                record.getFinishedAt().toLocalDate().equals(date)).collect(Collectors.toList());
    }

    public List<TimerRecordDTO> loadRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return this.loadAllRecords().stream().filter((record) -> {
            LocalDate recordDate = record.getFinishedAt().toLocalDate();
            return !recordDate.isBefore(startDate) && !recordDate.isAfter(endDate);
        }).collect(Collectors.toList());
    }

    public void clearAllRecords() {
        try {
            if (Files.exists(this.historyFilePath)) {
                Files.delete(this.historyFilePath);
                log.info("Cleared all timer records");
            }
        } catch (IOException e) {
            log.error("Failed to clear timer records: {}", e.getMessage(), e);
        }

    }

    public DailyStatisticsDTO loadTodayStatistics(LocalDate date) {
        List<TimerRecordDTO> records = this.loadRecordsByDate(date);
        long completedPomodoros = records.stream()
                .filter((record) -> record.getReason() == FinishReasonDTO.COMPLETED)
                .filter((record) -> record.getSessionType().sessionType() == SessionTypeEnumDTO.WORK)
                .count();
        long currentCycle = completedPomodoros % 4;
        DailyStatisticsDTO statistics = new DailyStatisticsDTO(date, (int) completedPomodoros, (int) currentCycle);
        log.debug("Loaded statistics for {}: {} completed pomodoros, cycle: {}",
                date, completedPomodoros, statistics.getCurrentCycle());
        return statistics;
    }
}
