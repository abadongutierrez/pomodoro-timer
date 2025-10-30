package com.jabaddon.pomodorotimer.application.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.jabaddon.pomodorotimer.application.port.in.GetTimerStateQuery;
import com.jabaddon.pomodorotimer.application.port.in.PauseTimerUseCase;
import com.jabaddon.pomodorotimer.application.port.in.ResetTimerUseCase;
import com.jabaddon.pomodorotimer.application.port.in.StartTimerUseCase;
import com.jabaddon.pomodorotimer.application.port.out.AnimationPort;
import com.jabaddon.pomodorotimer.application.port.out.NotificationPort;
import com.jabaddon.pomodorotimer.application.port.out.PersistencePort;
import com.jabaddon.pomodorotimer.application.port.out.TimerHistoryPort;
import com.jabaddon.pomodorotimer.application.port.out.TimerPort;
import com.jabaddon.pomodorotimer.application.port.out.UIPort;
import com.jabaddon.pomodorotimer.domain.model.Session;
import com.jabaddon.pomodorotimer.domain.model.SessionDomainEventHandler;
import com.jabaddon.pomodorotimer.domain.model.SessionType;
import com.jabaddon.pomodorotimer.domain.model.TimerMemento;
import com.jabaddon.pomodorotimer.domain.model.TimerRecord;

/**
 * Application Service implementing all use cases.
 * Orchestrates domain logic and coordinates with external systems via ports.
 * This is the heart of the hexagonal architecture.
 */
@Service
public class TimerApplicationService implements
        StartTimerUseCase,
        PauseTimerUseCase,
        ResetTimerUseCase,
        GetTimerStateQuery,
        SessionDomainEventHandler {
    private static final Logger log = LoggerFactory.getLogger(TimerApplicationService.class);

    // Domain entities and services
    private final Session session;

    // Driven ports (dependencies on external systems)
    private final TimerPort timerPort;
    private final NotificationPort notificationPort;
    private final PersistencePort persistencePort;
    private final AnimationPort animationPort;
    private final TimerHistoryPort timerHistoryPort;
    private final UIPort uiUpdatePort;

    public TimerApplicationService(
            TimerPort timerPort,
            NotificationPort notificationPort,
            PersistencePort persistencePort,
            AnimationPort animationPort,
            TimerHistoryPort timerHistoryPort,
            @Lazy UIPort uiUpdatePort) {

        // Initialize domain objects
        this.session = new Session(this);

        // Store port references
        this.timerPort = timerPort;
        this.notificationPort = notificationPort;
        this.persistencePort = persistencePort;
        this.animationPort = animationPort;
        this.timerHistoryPort = timerHistoryPort;
        this.uiUpdatePort = uiUpdatePort;

        // Load today's statistics from history and initialize session
        var todayStats = persistencePort.loadTodayStatistics();
        this.session.setCompletedPomodoros(todayStats.getCompletedPomodoros());
        this.session.setCurrentCycle(todayStats.getCurrentCycle());
        log.info("Restored session state: {} completed pomodoros, cycle: {}",
            todayStats.getCompletedPomodoros(), todayStats.getCurrentCycle());
    }

    // ========== StartTimerUseCase Implementation ==========

    @Override
    public void startNormalTimer() {
        session.startSession();
    }

    @Override
    public void onSessionStarted() {
        log.info("Normal Session started. type={}, minutes={}",
            session.getCurrentSessionType(),
            session.getCurrentSessionType().getDefaultMinutes());
        timerPort.startTicking(this::onTick);
        updateAnimation();
    }

    @Override
    public void startCustomTimer(int minutes) {
        session.startCustomSession(minutes);
    }

    @Override
    public void onCustomSessionStarted(int minutes) {
        log.info("Custom Session started. type={}, minutes={}",
            session.getCurrentSessionType(),
            minutes);
        timerPort.startTicking(this::onTick);
        updateAnimation();
    }

    // ========== PauseTimerUseCase Implementation ==========

    @Override
    public void pause() {
        if (session.pauseTimer()) {
            // this probably should be handled with domain events?
            timerPort.pauseTicking();
            animationPort.playAnimation(AnimationPort.AnimationType.IDLE);
        }
    }

    @Override
    public void resume() {
        if (session.resumeTimer()) {
           // this probably should be handled with domain events?
            timerPort.resumeTicking();
            updateAnimation();
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
        animationPort.playAnimation(AnimationPort.AnimationType.IDLE);
    }

    @Override
    public void stop() {
        // Save to history before stopping if timer was running
        if (session.isTimerRunning() || session.isTimerPaused()) {
            handleTimerStop();
        }

        session.resetTimer();
        timerPort.stopTicking();
        animationPort.playAnimation(AnimationPort.AnimationType.IDLE);
    }

    // ========== GetTimerStateQuery Implementation ==========

    @Override
    public TimerStateDTO getCurrentState() {
        TimerMemento memento = session.createTimerMemento();
        return new GetTimerStateQuery.TimerStateDTO(
                memento.remainingSeconds(),
                memento.state(),
                memento.sessionType(),
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
        if (session.wasTimerCompleted() || session.wasTimerStopped()) {
            TimerRecord record = session.createTimerRecord(finishedAt);
            timerHistoryPort.saveRecord(record);
        }

        SessionType nextType = session.handleTimerCompletion();

        // Show notification
        notificationPort.showCompletionNotification(currentType, nextType);

        // Play celebration animation
        animationPort.playAnimation(AnimationPort.AnimationType.CELEBRATING);

        // Notify UI to reset controls
        uiUpdatePort.onTimerCompleted(currentType, nextType);
    }

    /**
     * Handles timer stop - saves incomplete timer to history.
     * Called when user manually stops or resets the timer.
     */
    private void handleTimerStop() {
        if (session.wasTimerStopped()) {
            // Capture stop time now (before timer.stop() is called)
            TimerRecord record = session.createTimerRecord(LocalDateTime.now()); 
            timerHistoryPort.saveRecord(record);
            log.debug("Saved stopped timer to history: {}", record);
        }
    }

    /**
     * Updates animation based on current session type.
     */
    private void updateAnimation() {
        AnimationPort.AnimationType animationType = switch (session.timerCurrentSessionType()) {
            case WORK -> AnimationPort.AnimationType.WORKING;
            case SHORT_BREAK, LONG_BREAK -> AnimationPort.AnimationType.RESTING;
        };
        animationPort.playAnimation(animationType);
    }

    // ========== Lifecycle Methods ==========

    /**
     * Cleanup method to be called when application closes.
     */
    public void shutdown() {
        timerPort.stopTicking();
        animationPort.stopAnimation();
    }

    public Integer getNormalTimerSession() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
