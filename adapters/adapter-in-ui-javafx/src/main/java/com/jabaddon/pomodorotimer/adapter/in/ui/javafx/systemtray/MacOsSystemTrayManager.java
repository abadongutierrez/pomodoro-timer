package com.jabaddon.pomodorotimer.adapter.in.ui.javafx.systemtray;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * macOS-specific implementation of system tray/menu bar integration.
 * Displays timer in the macOS menu bar with a popup menu for controls.
 * This is a UI presentation component, not an adapter for business logic.
 */
public class MacOsSystemTrayManager implements SystemTrayManager {
    private static final Logger log = LoggerFactory.getLogger(MacOsSystemTrayManager.class);

    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private Stage stage;
    private Runnable onStartPauseAction;
    private Runnable onResetAction;
    private Runnable onShowHideAction;

    @Override
    public void initialize(Stage stage) {
        this.stage = stage;

        // Ensure AWT is initialized properly with JavaFX
        Platform.setImplicitExit(false);

        // Create system tray in AWT thread
        java.awt.EventQueue.invokeLater(() -> {
            if (!SystemTray.isSupported()) {
                log.error("System tray is not supported on this platform");
                return;
            }
            try {
                systemTray = SystemTray.getSystemTray();

                // Create a simple image for the tray icon
                Image image = createTrayImage("25:00");

                // Create popup menu
                PopupMenu popup = createPopupMenu();

                // Create tray icon
                trayIcon = new TrayIcon(image, "Pomodoro Timer", popup);
                trayIcon.setImageAutoSize(true);

                // Double-click to show/hide window
                trayIcon.addActionListener(e -> toggleWindowVisibility());

                // Add to system tray
                systemTray.add(trayIcon);

                log.info("System tray icon added successfully");

            } catch (AWTException e) {
                log.error("Failed to add system tray icon: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * Creates the popup menu for the tray icon.
     */
    private PopupMenu createPopupMenu() {
        PopupMenu popup = new PopupMenu();

        // Show/Hide menu item
        MenuItem showHideItem = new MenuItem("Show Timer");
        showHideItem.addActionListener(e -> {
            if (onShowHideAction != null) {
                Platform.runLater(onShowHideAction);
            }
        });
        popup.add(showHideItem);

        popup.addSeparator();

        // Start/Pause menu item
        MenuItem startPauseItem = new MenuItem("Start/Pause");
        startPauseItem.addActionListener(e -> {
            if (onStartPauseAction != null) {
                Platform.runLater(onStartPauseAction);
            }
        });
        popup.add(startPauseItem);

        // Reset menu item
        MenuItem resetItem = new MenuItem("Reset");
        resetItem.addActionListener(e -> {
            if (onResetAction != null) {
                Platform.runLater(onResetAction);
            }
        });
        popup.add(resetItem);

        popup.addSeparator();

        // Quit menu item
        MenuItem quitItem = new MenuItem("Quit");
        quitItem.addActionListener(e -> {
            Platform.runLater(() -> {
                cleanup();
                Platform.exit();
                System.exit(0);
            });
        });
        popup.add(quitItem);

        return popup;
    }

    /**
     * Creates a simple image for the tray icon with timer text (MM:SS format).
     * White background with black text for consistent visibility.
     */
    private Image createTrayImage(String text) {
        // Make icon wider to accommodate MM:SS format with larger font
        int width = 60;  // Wider for larger text
        int height = 32; // Standard macOS menu bar height

        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(
            width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = bufferedImage.createGraphics();

        // Enable antialiasing for smooth rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw white background
        g2d.setColor(java.awt.Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Draw black text
        g2d.setColor(java.awt.Color.BLACK);
        g2d.setFont(new Font("SF Mono", Font.PLAIN, 22)); // Larger monospace font

        // Center the text
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int x = (width - textWidth) / 2;
        int y = (height + textHeight) / 2 - 1; // Slight vertical adjustment

        g2d.drawString(text, x, y);

        g2d.dispose();

        return bufferedImage;
    }

    @Override
    public void updateTimer(String formattedTime) {
        if (trayIcon != null) {
            // Update icon with new time
            Image newImage = createTrayImage(formattedTime);
            trayIcon.setImage(newImage);

            // Update tooltip
            trayIcon.setToolTip("Pomodoro Timer - " + formattedTime);
        }
    }

    @Override
    public void updateStatus(String status) {
        if (trayIcon != null) {
            trayIcon.setToolTip("Pomodoro Timer - " + status);
        }
    }

    /**
     * Toggles window visibility.
     */
    private void toggleWindowVisibility() {
        Platform.runLater(() -> {
            if (stage.isShowing()) {
                stage.hide();
            } else {
                stage.show();
                stage.toFront();
            }
        });
    }

    @Override
    public void setOnStartPauseAction(Runnable action) {
        this.onStartPauseAction = action;
    }

    @Override
    public void setOnResetAction(Runnable action) {
        this.onResetAction = action;
    }

    @Override
    public void setOnShowHideAction(Runnable action) {
        this.onShowHideAction = action;
    }

    @Override
    public void cleanup() {
        // Ensure cleanup happens on AWT thread to avoid deadlocks
        java.awt.EventQueue.invokeLater(() -> {
            if (systemTray != null && trayIcon != null) {
                try {
                    systemTray.remove(trayIcon);
                    log.info("System tray icon removed");
                } catch (Exception e) {
                    log.error("Error removing tray icon: {}", e.getMessage(), e);
                }
            }
        });
    }
}
