package com.jabaddon.pomodorotimer.application.service;

import java.time.LocalDateTime;

import com.jabaddon.pomodorotimer.application.port.in.PauseTimerUseCase;
import com.jabaddon.pomodorotimer.application.port.in.ResetTimerUseCase;
import com.jabaddon.pomodorotimer.application.port.in.StartTimerUseCase;
import com.jabaddon.pomodorotimer.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jabaddon.pomodorotimer.application.dto.DailyStatisticsDTO;
import com.jabaddon.pomodorotimer.application.mapper.DomainToDtoMapper;
import com.jabaddon.pomodorotimer.application.dto.SessionTypeDTO;
import com.jabaddon.pomodorotimer.application.dto.TimerRecordDTO;
import com.jabaddon.pomodorotimer.application.port.in.GetTimerStateQuery;
import com.jabaddon.pomodorotimer.application.port.out.NotificationPort;
import com.jabaddon.pomodorotimer.application.port.out.TimerPersistencePort;
import com.jabaddon.pomodorotimer.application.port.out.TimerTicksSchedulerPort;
import com.jabaddon.pomodorotimer.application.port.out.UIPort;

/**
 * Application Service implementing all use cases.
 * Orchestrates domain logic and coordinates with external systems via ports.
 * This is the heart of the hexagonal architecture.
 */
public class TimerApplicationService implements
        GetTimerStateQuery,
        PauseTimerUseCase,
        ResetTimerUseCase,
        StartTimerUseCase,
        SessionDomainEventHandler {
    private static final Logger log = LoggerFactory.getLogger(TimerApplicationService.class);

    // Domain entities and services
    private final Session session;

    // Driven ports (dependencies on external systems)
    private final TimerTicksSchedulerPort timerPort;
    private final NotificationPort notificationPort;
    private final TimerPersistencePort timerHistoryPort;
    private final UIPort uiUpdatePort;

    public TimerApplicationService(
            TimerTicksSchedulerPort timerPort,
            NotificationPort notificationPort,
            TimerPersistencePort timerHistoryPort,
            UIPort uiUpdatePort) {

        
        // Store port references
        this.timerPort = timerPort;
        this.notificationPort = notificationPort;
        this.timerHistoryPort = timerHistoryPort;
        this.uiUpdatePort = uiUpdatePort;

        // Load today's statistics from history and initialize session
        DailyStatisticsDTO todayStatsDto = timerHistoryPort.loadTodayStatistics();
        DailyStatistics todayStats = DomainToDtoMapper.toDomain(todayStatsDto);

        // Initialize domain objects
        this.session = new Session(this);
        this.session.initializeFromTodayStats(todayStats);
        
        log.info("Restored session state: {} completed pomodoros, cycle: {}",
            todayStats.getCompletedPomodoros(), todayStats.getCurrentCycle());
    }

    // ========== StartTimerUseCase Implementation ==========

    @Override
    public void startNormalTimer() {
        session.startSession();
    }

    @Override
    public void onSessionStarted(SessionType sessionType, int minutes) {
        log.info("Session started. type={}, minutes={}, custom={}",
                sessionType, minutes, sessionType.isCustom(minutes));
        timerPort.startTicking(this::onTick);
    }

    @Override
    public void startCustomTimer(int minutes) {
        session.startCustomSession(minutes);
    }

    // ========== PauseTimerUseCase Implementation ==========

    @Override
    public void pause() {
        if (session.pauseTimer()) {
            // this probably should be handled with domain events?
            timerPort.pauseTicking();
        }
    }

    @Override
    public void resume() {
        if (session.resumeTimer()) {
           // this probably should be handled with domain events?
            timerPort.resumeTicking();
        }
    }

    // ========== ResetTimerUseCase Implementation ==========

    @Override
    public void reset() {
        // Save to history before resetting if timer was running
        if (session.isTimerRunning() || session.isTimerPaused()) {
            handleTimerStop();
        }

        session.resetTimer();
        timerPort.stopTicking();
    }

    @Override
    public void stop() {
        // Save to history before stopping if timer was running
        if (session.isTimerRunning() || session.isTimerPaused()) {
            handleTimerStop();
        }

        session.resetTimer();
        timerPort.stopTicking();
    }

    // ========== GetTimerStateQuery Implementation ==========

    @Override
    public TimerCurrentStateDTO getCurrentState() {
        TimerMemento memento = session.createTimerMemento();
        
        return new GetTimerStateQuery.TimerCurrentStateDTO(
                memento.remainingSeconds(),
                DomainToDtoMapper.toDto(memento.state()),
                DomainToDtoMapper.toDto(memento.sessionType()),
                session.getCompletedPomodoros(),
                session.getCurrentCycle()
        );
    }

    // ========== Internal Event Handlers ==========

    /**
     * Called every second by the TimerPort.
     * This is the core timer tick logic.
     */
    private void onTick() {
        // Play tick sound
        notificationPort.playTickSound();

        // Tick the timer
        boolean completed = session.tick();

        // If timer completed, handle completion
        if (completed) {
            handleTimerCompletion();
        }
    }

    /**
     * Handles timer completion - plays alarm, updates stats, transitions session.
     */
    private void handleTimerCompletion() {
        timerPort.stopTicking();
        notificationPort.playAlarmSound();

        SessionType currentType = session.timerCurrentSessionType();

        LocalDateTime finishedAt = LocalDateTime.now();

        // TODO saving record should be a domain event
        if (session.isTimerCompleted() || session.wasTimerStopped()) {
            TimerRecord record = session.createTimerRecord(finishedAt);
            TimerRecordDTO recordDto = DomainToDtoMapper.toDto(record);
            timerHistoryPort.saveRecord(recordDto);
        }

        SessionType nextType = session.handleTimerCompletion();

        // Show notification (translate domain types to DTOs)
        SessionTypeDTO currentTypeDto = DomainToDtoMapper.toDto(currentType);
        SessionTypeDTO nextTypeDto = DomainToDtoMapper.toDto(nextType);
        notificationPort.showCompletionNotification(currentTypeDto, nextTypeDto);

        // Notify UI to reset controls (translate domain types to DTOs)
        uiUpdatePort.onTimerCompleted(currentTypeDto, nextTypeDto);
    }

    /**
     * Handles timer stop - saves incomplete timer to history.
     * Called when user manually stops or resets the timer.
     */
    private void handleTimerStop() {
        if (session.wasTimerStopped()) {
            // Capture stop time now (before timer.stop() is called)
            TimerRecord record = session.createTimerRecord(LocalDateTime.now());
            TimerRecordDTO recordDto = DomainToDtoMapper.toDto(record);
            timerHistoryPort.saveRecord(recordDto);
            log.debug("Saved stopped timer to history: {}", record);
        }
    }

    // ========== Lifecycle Methods ==========

    /**
     * Cleanup method to be called when application closes.
     */
    public void shutdown() {
        timerPort.stopTicking();
    }

    public Integer getNormalTimerSession() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
