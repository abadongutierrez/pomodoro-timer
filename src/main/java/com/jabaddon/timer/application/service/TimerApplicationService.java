package com.jabaddon.timer.application.service;

import com.jabaddon.timer.application.port.in.*;
import com.jabaddon.timer.application.port.out.*;
import com.jabaddon.timer.domain.model.*;
import com.jabaddon.timer.domain.service.SessionRules;

/**
 * Application Service implementing all use cases.
 * Orchestrates domain logic and coordinates with external systems via ports.
 * This is the heart of the hexagonal architecture.
 */
public class TimerApplicationService implements
        StartTimerUseCase,
        PauseTimerUseCase,
        ResetTimerUseCase,
        GetTimerStateQuery {

    // Domain entities and services
    private final Timer timer;
    private final Session session;
    private final SessionRules sessionRules;

    // Driven ports (dependencies on external systems)
    private final TimerPort timerPort;
    private final NotificationPort notificationPort;
    private final PersistencePort persistencePort;
    private final AnimationPort animationPort;

    // Statistics management
    private DailyStatistics dailyStatistics;

    public TimerApplicationService(
            TimerPort timerPort,
            NotificationPort notificationPort,
            PersistencePort persistencePort,
            AnimationPort animationPort) {

        // Initialize domain objects
        this.timer = new Timer();
        this.session = new Session();
        this.sessionRules = new SessionRules();

        // Store port references
        this.timerPort = timerPort;
        this.notificationPort = notificationPort;
        this.persistencePort = persistencePort;
        this.animationPort = animationPort;

        // Load today's statistics
        this.dailyStatistics = persistencePort.loadTodayStatistics();
        this.session.setCompletedPomodoros(dailyStatistics.getCompletedPomodoros());
    }

    // ========== StartTimerUseCase Implementation ==========

    @Override
    public void startSession() {
        if (!sessionRules.canStartSession(timer)) {
            return; // Timer already running
        }

        // Start the session with default duration
        sessionRules.startSession(timer, session);

        // Start the ticker
        timerPort.startTicking(this::onTick);

        // Update animation based on session type
        updateAnimation();
    }

    @Override
    public void startCustomTimer(int minutes) {
        if (!sessionRules.canStartSession(timer)) {
            return;
        }

        timer.start(minutes);
        timerPort.startTicking(this::onTick);
        updateAnimation();
    }

    // ========== PauseTimerUseCase Implementation ==========

    @Override
    public void pause() {
        if (!sessionRules.canPauseTimer(timer)) {
            return;
        }

        timer.pause();
        timerPort.pauseTicking();
        animationPort.playAnimation(AnimationPort.AnimationType.IDLE);
    }

    @Override
    public void resume() {
        if (!sessionRules.canResumeTimer(timer)) {
            return;
        }

        timer.resume();
        timerPort.resumeTicking();
        updateAnimation();
    }

    // ========== ResetTimerUseCase Implementation ==========

    @Override
    public void reset() {
        sessionRules.resetSession(timer, session);
        timerPort.stopTicking();
        animationPort.playAnimation(AnimationPort.AnimationType.IDLE);
    }

    @Override
    public void stop() {
        timer.stop();
        timerPort.stopTicking();
        animationPort.playAnimation(AnimationPort.AnimationType.IDLE);
    }

    // ========== GetTimerStateQuery Implementation ==========

    @Override
    public TimerStateDTO getCurrentState() {
        return new TimerStateDTO(
                timer.getRemainingSeconds(),
                timer.getFormattedTime(),
                timer.getState(),
                timer.getSessionType(),
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
        boolean completed = timer.tick();

        // If timer completed, handle completion
        if (completed) {
            handleTimerCompletion();
        }
    }

    /**
     * Handles timer completion - plays alarm, updates stats, transitions session.
     */
    private void handleTimerCompletion() {
        // Stop the ticker
        timerPort.stopTicking();

        // Play alarm sound
        notificationPort.playAlarmSound();

        // Get the session types
        SessionType completedType = timer.getSessionType();
        SessionType nextType = sessionRules.handleTimerCompletion(timer, session);

        // Update daily statistics if work session completed
        if (completedType == SessionType.WORK) {
            dailyStatistics.incrementPomodoros();
            persistencePort.saveTodayStatistics(dailyStatistics);
        }

        // Show notification
        notificationPort.showCompletionNotification(completedType, nextType);

        // Play celebration animation
        animationPort.playAnimation(AnimationPort.AnimationType.CELEBRATING);
    }

    /**
     * Updates animation based on current session type.
     */
    private void updateAnimation() {
        AnimationPort.AnimationType animationType = switch (timer.getSessionType()) {
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
}
