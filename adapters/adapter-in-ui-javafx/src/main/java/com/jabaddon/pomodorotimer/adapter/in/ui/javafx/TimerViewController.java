package com.jabaddon.pomodorotimer.adapter.in.ui.javafx;

import com.jabaddon.pomodorotimer.application.service.TimerApplicationService;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.jabaddon.pomodorotimer.adapter.in.ui.javafx.systemtray.MacOsSystemTrayManager;
import com.jabaddon.pomodorotimer.adapter.in.ui.javafx.systemtray.NoOpSystemTrayManager;
import com.jabaddon.pomodorotimer.adapter.in.ui.javafx.systemtray.SystemTrayManager;
import com.jabaddon.pomodorotimer.application.dto.SessionTypeDTO;
import com.jabaddon.pomodorotimer.application.port.in.GetTimerStateQuery;
import com.jabaddon.pomodorotimer.application.port.out.UIPort;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Primary adapter for JavaFX UI (Driving adapter).
 * Supports dual-view mode:
 * - FULL mode: All controls visible when focused
 * - COMPACT mode: Timer-only when unfocused, floating on top
 *
 * Implements UIUpdatePort to receive notifications from the application service.
 * Active when javafx profile is enabled.
 */
@Component
@Profile("javafx")
public class TimerViewController implements UIPort {
    private static final Logger log = LoggerFactory.getLogger(TimerViewController.class);

    // Application service (use cases)
    private final TimerApplicationService timerService;
    private final ApplicationContext applicationContext;

    // System tray integration (OS-specific UI component)
    private final SystemTrayManager systemTrayManager;

    // Stage reference for dynamic resizing
    private Stage stage;
    private Scene scene;

    // Current view mode
    private ViewMode currentMode = ViewMode.FULL;

    // Layouts for different modes
    private VBox fullModeLayout;
    private VBox compactModeLayout;

    // UI Components (Full Mode)
    private Label timerLabel;
    private Label sessionTypeLabel;
    private Label dailyCountLabel;
    private Label cycleIndicatorLabel;
    private Spinner<Integer> minuteSpinner;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;

    // UI Components (Compact Mode)
    private Label compactTimerLabel;
    private Label compactInfoLabel;

    // UI update timeline
    private Timeline updateTimeline;

    // ========== Style Constants ==========

    /**
     * Centralized style definitions for consistent theming.
     */
    private static class StyleConstants {
        // Colors
        static final String COLOR_BACKGROUND = "#2b2b2b";
        static final String COLOR_BACKGROUND_RGBA = "rgba(43, 43, 43, 0.95)";
        static final String COLOR_BACKGROUND_RGBA_HOVER = "rgba(43, 43, 43, 0.98)";
        static final String COLOR_WHITE = "white";
        static final String COLOR_WORK = "#4CAF50";
        static final String COLOR_WORK_LIGHT = "#66BB6A";
        static final String COLOR_WARNING = "#FF9800";
        static final String COLOR_DANGER = "#f44336";
        static final String COLOR_CYCLE = "#FFC107";
        static final String COLOR_INFO = "#2196F3";

        // Font sizes
        static final int FONT_SIZE_SESSION_TYPE = 24;
        static final int FONT_SIZE_TIMER_LARGE = 72;
        static final int FONT_SIZE_TIMER_COMPACT = 48;
        static final int FONT_SIZE_CYCLE = 16;
        static final int FONT_SIZE_CYCLE_COMPACT = 14;
        static final int FONT_SIZE_DAILY_COUNT = 18;
        static final int FONT_SIZE_MINUTE_LABEL = 16;
        static final int FONT_SIZE_BUTTON = 14;

        // Button styles
        static final String BUTTON_BASE_STYLE = "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;";
        static final String BUTTON_START = "-fx-background-color: " + COLOR_WORK + "; " + BUTTON_BASE_STYLE;
        static final String BUTTON_PAUSE = "-fx-background-color: " + COLOR_WARNING + "; " + BUTTON_BASE_STYLE;
        static final String BUTTON_RESET = "-fx-background-color: " + COLOR_DANGER + "; " + BUTTON_BASE_STYLE;

        // Layout styles
        static final String LAYOUT_FULL_MODE = "-fx-background-color: " + COLOR_BACKGROUND + ";";
        static final String LAYOUT_COMPACT_MODE =
            "-fx-background-color: " + COLOR_BACKGROUND_RGBA + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: " + COLOR_WORK + ";" +
            "-fx-border-width: 2;";
        static final String LAYOUT_COMPACT_MODE_HOVER =
            "-fx-background-color: " + COLOR_BACKGROUND_RGBA_HOVER + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: " + COLOR_WORK_LIGHT + ";" +
            "-fx-border-width: 3;";

        // Dimensions
        static final int BUTTON_WIDTH = 100;
        static final int SPINNER_WIDTH = 100;
    }

    public TimerViewController(TimerApplicationService timerService, ApplicationContext applicationContext) {
        this.timerService = timerService;
        this.applicationContext = applicationContext;

        // Create OS-specific system tray manager (UI component)
        this.systemTrayManager = createSystemTrayManager();
    }

    private static SystemTrayManager createSystemTrayManager() {
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isMacOS = osName.contains("mac") || osName.contains("darwin");
        log.info("Detected operating system: {}", osName);

        if (isMacOS) {
            log.info("Running on macOS - enabling system tray integration");
            return new MacOsSystemTrayManager();
        } else {
            log.info("Running on {} - system tray disabled", osName);
            return new NoOpSystemTrayManager();
        }
    }

    public Scene createScene(Stage stage) {
        this.stage = stage;

        // Build both layouts
        fullModeLayout = buildFullModeLayout();
        compactModeLayout = buildCompactModeLayout();

        // Start with full mode
        scene = new Scene(fullModeLayout, 600, 400);
        // double click makes fullmoode go to compact mode
        fullModeLayout.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 2) {
                    switchToCompactMode();
                }
            }
        });
        // double click makes compact mode go to full mode again
        compactModeLayout.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 2) {
                    switchToFullMode();
                }
            }
        });

        // Setup focus listener for automatic view switching
        //setupFocusListener();

        // Initialize system tray
        initializeSystemTray();

        // Start UI update loop
        startUIUpdateLoop();

        return scene;
    }

    // ========== Full Mode Layout ==========

    private VBox buildFullModeLayout() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle(StyleConstants.LAYOUT_FULL_MODE);

        // Setup UI components
        setupFullModeLabels();
        HBox spinnerBox = createSpinnerBox();
        HBox controlBox = createControlButtons();

        root.getChildren().addAll(
                sessionTypeLabel,
                timerLabel,
                spinnerBox,
                controlBox,
                cycleIndicatorLabel,
                dailyCountLabel
        );

        return root;
    }

    private void setupFullModeLabels() {
        sessionTypeLabel = new Label("WORK SESSION");
        sessionTypeLabel.setFont(Font.font("Arial", FontWeight.BOLD, StyleConstants.FONT_SIZE_SESSION_TYPE));
        sessionTypeLabel.setTextFill(Color.web(StyleConstants.COLOR_WORK));

        timerLabel = new Label("25:00");
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, StyleConstants.FONT_SIZE_TIMER_LARGE));
        timerLabel.setTextFill(Color.WHITE);

        cycleIndicatorLabel = new Label("● ○ ○ ○");
        cycleIndicatorLabel.setFont(Font.font("Arial", FontWeight.NORMAL, StyleConstants.FONT_SIZE_CYCLE));
        cycleIndicatorLabel.setTextFill(Color.web(StyleConstants.COLOR_CYCLE));

        dailyCountLabel = new Label("Today's Pomodoros: 0");
        dailyCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, StyleConstants.FONT_SIZE_DAILY_COUNT));
        dailyCountLabel.setTextFill(Color.web(StyleConstants.COLOR_INFO));
    }

    private HBox createSpinnerBox() {
        Label minuteLabel = new Label("Minutes:");
        minuteLabel.setTextFill(Color.WHITE);
        minuteLabel.setFont(Font.font("Arial", StyleConstants.FONT_SIZE_MINUTE_LABEL));

        minuteSpinner = new Spinner<>(1, 120, 25);
        minuteSpinner.setEditable(true);
        minuteSpinner.setPrefWidth(StyleConstants.SPINNER_WIDTH);

        HBox spinnerBox = new HBox(10);
        spinnerBox.setAlignment(Pos.CENTER);
        spinnerBox.getChildren().addAll(minuteLabel, minuteSpinner);

        return spinnerBox;
    }

    private HBox createControlButtons() {
        startButton = new Button("Start");
        startButton.setPrefWidth(StyleConstants.BUTTON_WIDTH);
        startButton.setStyle(StyleConstants.BUTTON_START);
        startButton.setOnAction(e -> handleStart());

        pauseButton = new Button("Pause");
        pauseButton.setPrefWidth(StyleConstants.BUTTON_WIDTH);
        pauseButton.setStyle(StyleConstants.BUTTON_PAUSE);
        pauseButton.setOnAction(e -> handlePause());
        pauseButton.setDisable(true);

        resetButton = new Button("Reset");
        resetButton.setPrefWidth(StyleConstants.BUTTON_WIDTH);
        resetButton.setStyle(StyleConstants.BUTTON_RESET);
        resetButton.setOnAction(e -> handleReset());

        HBox controlBox = new HBox(15);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.getChildren().addAll(startButton, pauseButton, resetButton);

        return controlBox;
    }

    // ========== Compact Mode Layout ==========

    private VBox buildCompactModeLayout() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);
        root.setStyle(StyleConstants.LAYOUT_COMPACT_MODE);

        // Compact timer label (medium size)
        compactTimerLabel = new Label("25:00");
        compactTimerLabel.setFont(Font.font("Arial", FontWeight.BOLD, StyleConstants.FONT_SIZE_TIMER_COMPACT));
        compactTimerLabel.setTextFill(Color.WHITE);

        // Compact info: cycle dots + pomodoro count
        compactInfoLabel = new Label("● ○ ○ ○  (0)");
        compactInfoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, StyleConstants.FONT_SIZE_CYCLE_COMPACT));
        compactInfoLabel.setTextFill(Color.web(StyleConstants.COLOR_CYCLE));

        // Make clickable to focus
        root.setOnMouseClicked(e -> {
            stage.requestFocus();
            stage.toFront();
        });

        // Add hover effect
        root.setOnMouseEntered(e -> root.setStyle(StyleConstants.LAYOUT_COMPACT_MODE_HOVER));
        root.setOnMouseExited(e -> root.setStyle(StyleConstants.LAYOUT_COMPACT_MODE));

        root.getChildren().addAll(compactTimerLabel, compactInfoLabel);

        return root;
    }

    // ========== Focus Listener & View Switching ==========
    // TODO remove later
    private void setupFocusListener() {
        stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                switchToFullMode();
            } else {
                switchToCompactMode();
            }
        });
    }

    private void switchToFullMode() {
        if (currentMode == ViewMode.FULL) return;

        currentMode = ViewMode.FULL;
        scene.setRoot(fullModeLayout);
        stage.setWidth(600);
        stage.setHeight(500);
        stage.setAlwaysOnTop(false);
        stage.setOpacity(1.0);
    }

    private void switchToCompactMode() {
        if (currentMode == ViewMode.COMPACT) return;

        currentMode = ViewMode.COMPACT;
        scene.setRoot(compactModeLayout);
        stage.setWidth(220);
        stage.setHeight(120);
        stage.setAlwaysOnTop(true);
        stage.setOpacity(0.95);

        // Keep position (don't recenter in compact mode)
    }

    // ========== Event Handlers (Delegate to Application Service) ==========

    private void handleStart() {
        GetTimerStateQuery.TimerCurrentStateDTO timerState = timerService.getCurrentState();

        if (timerState.isPaused()) {
            timerService.resume();
            startButton.setText("Start");
            startButton.setDisable(true);
            pauseButton.setDisable(false);
        } else {
            if (minuteSpinner.getValue() == timerState.getSessionType().defaultMinutes()) {
                timerService.startNormalTimer();
            } else {
                timerService.startCustomTimer(minuteSpinner.getValue());
            }

            // Update UI state
            startButton.setDisable(true);
            pauseButton.setDisable(false);
            minuteSpinner.setDisable(true);
        }
    }

    private void handlePause() {
        GetTimerStateQuery.TimerCurrentStateDTO state = timerService.getCurrentState();

        if (state.isRunning()) {
            timerService.pause();
            startButton.setText("Resume");
            startButton.setDisable(false);
            pauseButton.setDisable(true);
        } else if (state.isPaused()) {
            timerService.resume();
            startButton.setDisable(true);
            pauseButton.setDisable(false);
        }
    }

    private void handleReset() {
        timerService.reset();

        // Reset UI state
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        startButton.setText("Start");
        minuteSpinner.setDisable(false);
    }

    // ========== UI Update Loop ==========

    /**
     * Periodically polls the application service for state updates.
     * Updates UI based on current state.
     */
    private void startUIUpdateLoop() {
        updateTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> updateUI()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    private void updateUI() {
        GetTimerStateQuery.TimerCurrentStateDTO state = timerService.getCurrentState();

        // Update full mode UI
        timerLabel.setText(getFormattedTime(state));
        sessionTypeLabel.setText(state.getSessionType().displayName().toUpperCase());
        dailyCountLabel.setText("Today's Pomodoros: " + state.getCompletedPomodoros());
        cycleIndicatorLabel.setText(buildCycleIndicator(state.getCurrentCycle()));

        // Update compact mode UI
        compactTimerLabel.setText(getFormattedTime(state));
        compactInfoLabel.setText(
            buildCycleIndicator(state.getCurrentCycle()) + "  (" + state.getCompletedPomodoros() + ")"
        );

        // Update system tray
        systemTrayManager.updateTimer(getFormattedTime(state));
    }

    private String getFormattedTime(GetTimerStateQuery.TimerCurrentStateDTO state) {
        int mins = state.getRemainingSeconds() / 60;
        int secs = state.getRemainingSeconds() % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private String buildCycleIndicator(int cycle) {
        StringBuilder indicator = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            indicator.append(i < cycle ? "● " : "○ ");
        }
        return indicator.toString().trim();
    }

    // ========== System Tray Integration ==========

    /**
     * Initializes the system tray icon with menu bar integration.
     */
    private void initializeSystemTray() {
        systemTrayManager.initialize(stage);

        // Wire up menu actions
        systemTrayManager.setOnStartPauseAction(this::handlePause);
        systemTrayManager.setOnResetAction(this::handleReset);
        systemTrayManager.setOnShowHideAction(this::toggleWindowVisibility);
    }

    /**
     * Toggles window visibility (for system tray menu).
     */
    private void toggleWindowVisibility() {
        if (stage.isShowing()) {
            stage.hide();
        } else {
            stage.show();
            stage.toFront();
            stage.requestFocus();
        }
    }

    /**
     * Cleanup method called when application closes.
     */
    public void shutdown() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
        systemTrayManager.cleanup();
    }

    // ========== UIPort Implementation ==========

    /**
     * Called by the application service when a timer completes.
     * Resets the UI controls to their initial state for the next session.
     */
    @Override
    public void onTimerCompleted(SessionTypeDTO completedType, SessionTypeDTO nextType) {
        log.info("Timer completed notification received: {} -> {}", completedType, nextType);

        // Use Platform.runLater to ensure UI updates happen on JavaFX Application Thread
        javafx.application.Platform.runLater(() -> {
            // Reset button states
            startButton.setDisable(false);
            pauseButton.setDisable(true);
            startButton.setText("Start");

            // Re-enable spinner for next session
            minuteSpinner.setDisable(false);

            // Update spinner to show next session's default duration
            // Map DTO back to domain to get default minutes (or we could add a helper method)
            int defaultMinutes = getDefaultMinutesForSessionType(nextType);
            minuteSpinner.getValueFactory().setValue(defaultMinutes);

            log.debug("UI controls reset after timer completion");
        });
    }

    /**
     * Helper method to get default minutes for a session type DTO.
     */
    private int getDefaultMinutesForSessionType(SessionTypeDTO sessionType) {
        return switch (sessionType.sessionType()) {
            case WORK -> 25;
            case SHORT_BREAK -> 5;
            case LONG_BREAK -> 15;
        };
    }
}
