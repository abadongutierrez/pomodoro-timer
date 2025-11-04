package com.jabaddon.pomodorotimer.adapter.in.ui.shell;

import com.jabaddon.pomodorotimer.application.dto.SessionTypeDTO;
import com.jabaddon.pomodorotimer.application.port.in.GetTimerStateQuery;
import com.jabaddon.pomodorotimer.application.port.out.UIPort;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Primary adapter implementing UIPort for Spring Shell.
 * Provides callbacks from the application layer to the UI layer.
 * Also maintains current timer state by polling the query port.
 * Active when shell profile is enabled.
 */
@Component
@Profile("shell")
public class SpringShellUIAdapter implements UIPort {
    private static final Logger logger = LoggerFactory.getLogger(SpringShellUIAdapter.class);

    private final GetTimerStateQuery stateQuery;
    private volatile GetTimerStateQuery.TimerStateDTO currentState;
    private ScheduledExecutorService statePollingExecutor;

    @Autowired
    public SpringShellUIAdapter(GetTimerStateQuery stateQuery) {
        this.stateQuery = stateQuery;
    }

    @PostConstruct
    public void initialize() {
        // Start background polling to keep state fresh
        statePollingExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "shell-ui-state-poller");
            thread.setDaemon(true);
            return thread;
        });

        // Poll state every 100ms
        statePollingExecutor.scheduleAtFixedRate(
            this::updateState,
            0,
            500,
            TimeUnit.MILLISECONDS
        );

        logger.info("Spring Shell UI adapter initialized");
    }

    @PreDestroy
    public void shutdown() {
        if (statePollingExecutor != null && !statePollingExecutor.isShutdown()) {
            statePollingExecutor.shutdownNow();
        }
        logger.info("Spring Shell UI adapter shut down");
    }

    private void updateState() {
        try {
            currentState = stateQuery.getCurrentState();
        } catch (Exception e) {
            logger.error("Error updating timer state", e);
        }
    }

    @Override
    public void onTimerCompleted(SessionTypeDTO completedType, SessionTypeDTO nextType) {
        logger.info("Timer completed: {} -> {}", completedType, nextType);

        // Print completion notification to console
        System.out.println();
        System.out.println("========================================");
        System.out.println(getEmoji(completedType) + " " + getDisplayName(completedType) + " Complete!");
        System.out.println("----------------------------------------");
        System.out.println("Next: " + getDisplayName(nextType));
        System.out.println("========================================");
        System.out.println();
        System.out.flush();
    }

    /**
     * Gets the current timer state.
     * This is called by commands and watch mode to display state.
     */
    public GetTimerStateQuery.TimerStateDTO getCurrentState() {
        updateState();
        return currentState;
    }

    private String getDisplayName(SessionTypeDTO sessionType) {
        return switch (sessionType) {
            case WORK -> "Work Session";
            case SHORT_BREAK -> "Short Break";
            case LONG_BREAK -> "Long Break";
        };
    }

    private String getEmoji(SessionTypeDTO sessionType) {
        return switch (sessionType) {
            case WORK -> "🍅";
            case SHORT_BREAK -> "☕";
            case LONG_BREAK -> "🌴";
        };
    }
}
