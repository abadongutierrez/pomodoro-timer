package com.jabaddon.pomodorotimer.adapter.in.ui.systemtray;

import javafx.stage.Stage;

/**
 * UI component for system tray integration.
 * This is part of the UI presentation layer, not an out port.
 * Manages the display of timer information in the system tray/menu bar.
 *
 * Implementations may be OS-specific or no-op for unsupported platforms.
 */
public interface SystemTrayManager {

    /**
     * Initializes the system tray icon.
     * @param stage The JavaFX stage (for show/hide functionality)
     */
    void initialize(Stage stage);

    /**
     * Updates the timer display in the system tray.
     * @param formattedTime The time in "MM:SS" format
     */
    void updateTimer(String formattedTime);

    /**
     * Updates the status message in the tray tooltip.
     * @param status The status message to display
     */
    void updateStatus(String status);

    /**
     * Sets the callback for start/pause action.
     * @param action The action to execute when start/pause is triggered
     */
    void setOnStartPauseAction(Runnable action);

    /**
     * Sets the callback for reset action.
     * @param action The action to execute when reset is triggered
     */
    void setOnResetAction(Runnable action);

    /**
     * Sets the callback for show/hide action.
     * @param action The action to execute when show/hide is triggered
     */
    void setOnShowHideAction(Runnable action);

    /**
     * Cleanup method to remove tray icon and release resources.
     */
    void cleanup();
}
