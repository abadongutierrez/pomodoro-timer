package com.jabaddon.pomodorotimer.adapter.in.ui.shell;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.jabaddon.pomodorotimer.application.port.in.GetTimerStateQuery;
import com.jabaddon.pomodorotimer.domain.model.SessionType;
import com.jabaddon.pomodorotimer.domain.model.TimerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * TUI (Terminal User Interface) mode for watching the timer in real-time.
 * Displays timer with live updates and handles keyboard shortcuts using Lanterna.
 */
public class TimerWatchMode {
    private static final Logger logger = LoggerFactory.getLogger(TimerWatchMode.class);

    private final SpringShellUIAdapter uiAdapter;
    private volatile boolean running = true;

    public TimerWatchMode(SpringShellUIAdapter uiAdapter) {
        this.uiAdapter = uiAdapter;
    }

    /**
     * Enters watch mode - blocks until user exits.
     */
    public void enter() {
        running = true;
        Terminal terminal = null;
        Screen screen = null;
        try {
            terminal = new DefaultTerminalFactory().createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            screen.setCursorPosition(null);
            
            runWatchLoop(screen);
            
        } catch (IOException e) {
            logger.error("Error in watch mode", e);
            System.err.println("Error entering watch mode: " + e.getMessage());
        } finally {
            running = false;
            if (screen != null) {
                try {
                    screen.stopScreen();
                } catch (IOException e) {
                    logger.error("Error stopping screen", e);
                }
            }
            System.out.println("Exited watch mode");
            System.out.println();
        }
    }

    private void runWatchLoop(Screen screen) throws IOException {
        long lastUpdateTime = 0;
        final long UPDATE_INTERVAL_MS = 500;

        while (running) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime >= UPDATE_INTERVAL_MS) {
                updateDisplay(screen);
                lastUpdateTime = currentTime;
            }

            KeyStroke keyStroke = screen.pollInput();
            if (keyStroke != null) {
                if (keyStroke.getKeyType() == KeyType.Escape || 
                    keyStroke.getKeyType() == KeyType.EOF) {
                    running = false;
                    break;
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
                break;
            }
        }
    }

    private void updateDisplay(Screen screen) {
        try {
            GetTimerStateQuery.TimerStateDTO state = uiAdapter.getCurrentState();
            
            screen.clear();
            TextGraphics textGraphics = screen.newTextGraphics();
            
            printTimerDisplay(state, textGraphics);
            
            screen.refresh();
        } catch (Exception e) {
            logger.error("Error updating display", e);
        }
    }

    private void printTimerDisplay(GetTimerStateQuery.TimerStateDTO state, TextGraphics textGraphics) {
        int startRow = 2;
        int startCol = 2;
        
        textGraphics.putString(startCol, startRow++, "  ╔════════════════════════════════════════╗");
        textGraphics.putString(startCol, startRow++, "  ║                                        ║");
        textGraphics.putString(startCol, startRow++, String.format("  ║      %s %-25s ║",
                getSessionEmoji(state.getSessionType()),
                getSessionName(state.getSessionType()).toUpperCase()));
        textGraphics.putString(startCol, startRow++, "  ║                                        ║");
        textGraphics.putString(startCol, startRow++, String.format("  ║           %s                    ║",
                formatLargeTime(state.getRemainingSeconds())));
        textGraphics.putString(startCol, startRow++, "  ║                                        ║");
        textGraphics.putString(startCol, startRow++, String.format("  ║      %s  %-26s ║",
                getStateIcon(state.getState()),
                getStateText(state.getState())));
        textGraphics.putString(startCol, startRow++, "  ║                                        ║");
        textGraphics.putString(startCol, startRow++, String.format("  ║      Cycle: %-27s ║",
                formatCycle(state.getCurrentCycle())));
        textGraphics.putString(startCol, startRow++, String.format("  ║      Today: 🍅 %-24d ║",
                state.getCompletedPomodoros()));
        textGraphics.putString(startCol, startRow++, "  ║                                        ║");
        textGraphics.putString(startCol, startRow++, "  ╚════════════════════════════════════════╝");
        startRow++;
        textGraphics.putString(startCol, startRow++, "  ┌────────────────────────────────────────┐");
        textGraphics.putString(startCol, startRow++, "  │  ESC: Exit watch mode                  │");
        textGraphics.putString(startCol, startRow++, "  └────────────────────────────────────────┘");
    }

    private String formatLargeTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
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

    private String getSessionName(SessionType type) {
        return switch (type) {
            case WORK -> "Work Session";
            case SHORT_BREAK -> "Short Break";
            case LONG_BREAK -> "Long Break";
        };
    }

    private String getSessionEmoji(SessionType type) {
        return switch (type) {
            case WORK -> "🍅";
            case SHORT_BREAK -> "☕";
            case LONG_BREAK -> "🌴";
        };
    }

    private String getStateIcon(TimerState state) {
        return switch (state) {
            case RUNNING -> "▶";
            case PAUSED -> "⏸";
            case IDLE -> "⏹";
            case COMPLETED -> "✓";
            case READY -> "⏺";
        };
    }

    private String getStateText(TimerState state) {
        return switch (state) {
            case RUNNING -> "Running";
            case PAUSED -> "Paused";
            case IDLE -> "Idle";
            case COMPLETED -> "Completed";
            case READY -> "Ready";
        };
    }
}
