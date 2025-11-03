package com.jabaddon.pomodorotimer.adapter.in.ui.systemtray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.Stage;

/**
 * No-operation system tray manager.
 * Used on platforms where system tray is not supported or needed.
 * All methods are empty implementations that do nothing.
 */
public class NoOpSystemTrayManager implements SystemTrayManager {
    private static final Logger log = LoggerFactory.getLogger(NoOpSystemTrayManager.class);

    @Override
    public void initialize(Stage stage) {
        // No-op: System tray not supported on this platform
        log.info("System tray disabled (not running on macOS)");
    }

    @Override
    public void updateTimer(String formattedTime) {
        // No-op
    }

    @Override
    public void updateStatus(String status) {
        // No-op
    }

    @Override
    public void setOnStartPauseAction(Runnable action) {
        // No-op
    }

    @Override
    public void setOnResetAction(Runnable action) {
        // No-op
    }

    @Override
    public void setOnShowHideAction(Runnable action) {
        // No-op
    }

    @Override
    public void cleanup() {
        // No-op
    }
}
