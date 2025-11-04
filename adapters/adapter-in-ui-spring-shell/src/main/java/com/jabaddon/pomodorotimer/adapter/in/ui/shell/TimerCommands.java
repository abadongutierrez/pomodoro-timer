package com.jabaddon.pomodorotimer.adapter.in.ui.shell;

import com.jabaddon.pomodorotimer.application.port.in.GetTimerStateQuery;
import com.jabaddon.pomodorotimer.application.port.in.PauseTimerUseCase;
import com.jabaddon.pomodorotimer.application.port.in.ResetTimerUseCase;
import com.jabaddon.pomodorotimer.application.port.in.StartTimerUseCase;
import com.jabaddon.pomodorotimer.domain.model.SessionType;
import com.jabaddon.pomodorotimer.domain.model.TimerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Spring Shell command handler for timer operations.
 * Primary adapter translating shell commands into use case calls.
 * Active when shell profile is enabled.
 */
@ShellComponent
@Profile("shell")
public class TimerCommands {
    private static final Logger logger = LoggerFactory.getLogger(TimerCommands.class);

    private final StartTimerUseCase startTimer;
    private final PauseTimerUseCase pauseTimer;
    private final ResetTimerUseCase resetTimer;
    private final SpringShellUIAdapter uiAdapter;
    private final TimerWatchMode watchMode;

    @Autowired
    public TimerCommands(
            StartTimerUseCase startTimer,
            PauseTimerUseCase pauseTimer,
            ResetTimerUseCase resetTimer,
            SpringShellUIAdapter uiAdapter) {
        this.startTimer = startTimer;
        this.pauseTimer = pauseTimer;
        this.resetTimer = resetTimer;
        this.uiAdapter = uiAdapter;
        this.watchMode = new TimerWatchMode(uiAdapter);
    }

    @ShellMethod(key = "timer start", value = "Start a timer session")
    public String start(
            @ShellOption(defaultValue = "0", help = "Duration in minutes (0 for normal session)")
            int minutes) {

        try {
            if (minutes > 0) {
                startTimer.startCustomTimer(minutes);
                logger.info("Started custom timer: {} minutes", minutes);
                return String.format("✓ Timer started: %d:00 (custom duration)\n" +
                                   "Run 'timer watch' to see live updates", minutes);
            } else {
                startTimer.startNormalTimer();
                logger.info("Started normal timer");
                return "✓ Timer started: 25:00 (WORK session)\n" +
                       "Run 'timer watch' to see live updates";
            }
        } catch (Exception e) {
            logger.error("Error starting timer", e);
            return "✗ Error starting timer: " + e.getMessage();
        }
    }

    @ShellMethod(key = "timer pause", value = "Pause the running timer")
    public String pause() {
        try {
            GetTimerStateQuery.TimerStateDTO state = uiAdapter.getCurrentState();

            if (state.getState() == TimerState.RUNNING) {
                pauseTimer.pause();
                logger.info("Timer paused");
                return "⏸  Timer paused at " + formatTime(state.getRemainingSeconds());
            } else if (state.getState() == TimerState.PAUSED) {
                return "ℹ  Timer is already paused";
            } else {
                return "✗ No timer running";
            }
        } catch (Exception e) {
            logger.error("Error pausing timer", e);
            return "✗ Error pausing timer: " + e.getMessage();
        }
    }

    @ShellMethod(key = "timer resume", value = "Resume a paused timer")
    public String resume() {
        try {
            GetTimerStateQuery.TimerStateDTO state = uiAdapter.getCurrentState();

            if (state.getState() == TimerState.PAUSED) {
                pauseTimer.resume();
                logger.info("Timer resumed");
                return "▶  Timer resumed";
            } else if (state.getState() == TimerState.RUNNING) {
                return "ℹ  Timer is already running";
            } else {
                return "✗ No timer to resume";
            }
        } catch (Exception e) {
            logger.error("Error resuming timer", e);
            return "✗ Error resuming timer: " + e.getMessage();
        }
    }

    @ShellMethod(key = "timer reset", value = "Reset the timer and session")
    public String reset() {
        try {
            resetTimer.reset();
            logger.info("Timer reset");
            return "🔄 Timer reset";
        } catch (Exception e) {
            logger.error("Error resetting timer", e);
            return "✗ Error resetting timer: " + e.getMessage();
        }
    }

    @ShellMethod(key = "timer stop", value = "Stop the timer")
    public String stop() {
        try {
            resetTimer.stop();
            logger.info("Timer stopped");
            return "⏹  Timer stopped";
        } catch (Exception e) {
            logger.error("Error stopping timer", e);
            return "✗ Error stopping timer: " + e.getMessage();
        }
    }

    @ShellMethod(key = "timer status", value = "Show current timer status")
    public String status() {
        try {
            GetTimerStateQuery.TimerStateDTO state = uiAdapter.getCurrentState();
            return formatDetailedStatus(state);
        } catch (Exception e) {
            logger.error("Error getting timer status", e);
            return "✗ Error getting status: " + e.getMessage();
        }
    }

    @ShellMethod(key = "timer watch", value = "Watch timer with live updates (press Ctrl+Q to exit)")
    public String watch() {
        try {
            watchMode.enter();
            return ""; // Watch mode handles its own output
        } catch (Exception e) {
            logger.error("Error entering watch mode", e);
            return "✗ Error entering watch mode: " + e.getMessage();
        }
    }

    private String formatDetailedStatus(GetTimerStateQuery.TimerStateDTO state) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("┌─────────────────────────────────────┐\n");
        sb.append(String.format("│ Status: %-27s │\n", getStateDisplay(state.getState())));
        sb.append(String.format("│ Time:   %-27s │\n", formatTime(state.getRemainingSeconds())));
        sb.append(String.format("│ Type:   %-27s │\n", getSessionTypeDisplay(state.getSessionType())));
        sb.append(String.format("│ Cycle:  %-27s │\n", formatCycle(state.getCurrentCycle())));
        sb.append(String.format("│ Today:  🍅 %-24d │\n", state.getCompletedPomodoros()));
        sb.append("└─────────────────────────────────────┘\n");
        return sb.toString();
    }

    private String getStateDisplay(TimerState state) {
        return switch (state) {
            case RUNNING -> "⏱  RUNNING";
            case PAUSED -> "⏸  PAUSED";
            case IDLE -> "⏹  IDLE";
            case COMPLETED -> "✓  COMPLETED";
            case READY -> "⏺  READY";
        };
    }

    private String getSessionTypeDisplay(SessionType type) {
        return switch (type) {
            case WORK -> "🍅 Work Session";
            case SHORT_BREAK -> "☕ Short Break";
            case LONG_BREAK -> "🌴 Long Break";
        };
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    private String formatCycle(int cycle) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 4; i++) {
            if (i <= cycle) {
                sb.append("●");
            } else {
                sb.append("○");
            }
        }
        sb.append(String.format(" (%d/4)", cycle));
        return sb.toString();
    }
}
