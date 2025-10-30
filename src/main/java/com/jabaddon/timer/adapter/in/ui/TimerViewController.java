package com.jabaddon.timer.adapter.in.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.jabaddon.timer.adapter.out.systemtray.NoOpSystemTrayAdapter;
import com.jabaddon.timer.adapter.out.systemtray.SystemTrayMacOsAdapter;
import com.jabaddon.timer.application.port.in.GetTimerStateQuery;
import com.jabaddon.timer.application.port.out.SystemTrayPort;
import com.jabaddon.timer.application.service.TimerApplicationService;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
 */
@Component
public class TimerViewController {
    private static final Logger log = LoggerFactory.getLogger(TimerViewController.class);

    // Application service (use cases)
    private final TimerApplicationService timerService;
    private final ApplicationContext applicationContext;

    // System tray integration (OS-specific)
    private final SystemTrayPort systemTrayAdapter;

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

    private AnimationTimer updateLoop;

    public TimerViewController(TimerApplicationService timerService, ApplicationContext applicationContext) {
        this.timerService = timerService;
        this.applicationContext = applicationContext;

        // Create OS-specific system tray adapter
        this.systemTrayAdapter = createSystemTrayAdapter();
    }

    private static SystemTrayPort createSystemTrayAdapter() {
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isMacOS = osName.contains("mac") || osName.contains("darwin");
        log.info("Detected operating system: {}", osName);

        if (isMacOS) {
            log.info("Running on macOS - enabling system tray integration");
            return new SystemTrayMacOsAdapter();
        } else {
            log.info("Running on {} - system tray disabled", osName);
            return new NoOpSystemTrayAdapter();
        }
    }

    public Scene createScene(Stage stage) {
        this.stage = stage;

        // Build both layouts
        fullModeLayout = buildFullModeLayout();
        compactModeLayout = buildCompactModeLayout();

        // Start with full mode
        scene = new Scene(fullModeLayout, 600, 500);

        // Setup focus listener for automatic view switching
        setupFocusListener();

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
        root.setStyle("-fx-background-color: #2b2b2b;");

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
        sessionTypeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        sessionTypeLabel.setTextFill(Color.web("#4CAF50"));

        timerLabel = new Label("25:00");
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        timerLabel.setTextFill(Color.WHITE);

        cycleIndicatorLabel = new Label("● ○ ○ ○");
        cycleIndicatorLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        cycleIndicatorLabel.setTextFill(Color.web("#FFC107"));

        dailyCountLabel = new Label("Today's Pomodoros: 0");
        dailyCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        dailyCountLabel.setTextFill(Color.web("#2196F3"));
    }

    private HBox createSpinnerBox() {
        Label minuteLabel = new Label("Minutes:");
        minuteLabel.setTextFill(Color.WHITE);
        minuteLabel.setFont(Font.font("Arial", 16));

        minuteSpinner = new Spinner<>(1, 120, 25);
        minuteSpinner.setEditable(true);
        minuteSpinner.setPrefWidth(100);

        HBox spinnerBox = new HBox(10);
        spinnerBox.setAlignment(Pos.CENTER);
        spinnerBox.getChildren().addAll(minuteLabel, minuteSpinner);

        return spinnerBox;
    }

    private HBox createControlButtons() {
        startButton = new Button("Start");
        startButton.setPrefWidth(100);
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        startButton.setOnAction(e -> handleStart());

        pauseButton = new Button("Pause");
        pauseButton.setPrefWidth(100);
        pauseButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        pauseButton.setOnAction(e -> handlePause());
        pauseButton.setDisable(true);

        resetButton = new Button("Reset");
        resetButton.setPrefWidth(100);
        resetButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
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
        root.setStyle(
            "-fx-background-color: rgba(43, 43, 43, 0.95);" +
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #4CAF50;" +
            "-fx-border-width: 2;"
        );

        // Compact timer label (medium size)
        compactTimerLabel = new Label("25:00");
        compactTimerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        compactTimerLabel.setTextFill(Color.WHITE);

        // Compact info: cycle dots + pomodoro count
        compactInfoLabel = new Label("● ○ ○ ○  (0)");
        compactInfoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        compactInfoLabel.setTextFill(Color.web("#FFC107"));

        // Make clickable to focus
        root.setOnMouseClicked(e -> {
            stage.requestFocus();
            stage.toFront();
        });

        // Add hover effect
        root.setOnMouseEntered(e ->
            root.setStyle(
                "-fx-background-color: rgba(43, 43, 43, 0.98);" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #66BB6A;" +
                "-fx-border-width: 3;"
            )
        );
        root.setOnMouseExited(e ->
            root.setStyle(
                "-fx-background-color: rgba(43, 43, 43, 0.95);" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #4CAF50;" +
                "-fx-border-width: 2;"
            )
        );

        root.getChildren().addAll(compactTimerLabel, compactInfoLabel);

        return root;
    }

    // ========== Focus Listener & View Switching ==========

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

        // Center on screen
        stage.centerOnScreen();
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
        GetTimerStateQuery.TimerStateDTO timerState = timerService.getCurrentState();

        if (timerState.isPaused()) {
            timerService.resume();
            startButton.setText("Start");
            startButton.setDisable(true);
            pauseButton.setDisable(false);
        } else {
            if (minuteSpinner.getValue() == timerState.getSessionType().getDefaultMinutes()) {
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
        GetTimerStateQuery.TimerStateDTO state = timerService.getCurrentState();

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
        updateLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateUI();
            }
        };
        updateLoop.start();
    }

    private void updateUI() {
        GetTimerStateQuery.TimerStateDTO state = timerService.getCurrentState();

        // Update full mode UI
        timerLabel.setText(getFormattedTime(state));
        sessionTypeLabel.setText(state.getSessionType().getDisplayName().toUpperCase());
        dailyCountLabel.setText("Today's Pomodoros: " + state.getCompletedPomodoros());
        cycleIndicatorLabel.setText(buildCycleIndicator(state.getCurrentCycle()));

        // Update compact mode UI
        compactTimerLabel.setText(getFormattedTime(state));
        compactInfoLabel.setText(
            buildCycleIndicator(state.getCurrentCycle()) + "  (" + state.getCompletedPomodoros() + ")"
        );

        // Update system tray
        systemTrayAdapter.updateTimer(getFormattedTime(state));
    }

    private String getFormattedTime(GetTimerStateQuery.TimerStateDTO state) {
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
        systemTrayAdapter.initialize(stage);

        // Wire up menu actions
        systemTrayAdapter.setOnStartPauseAction(this::handlePause);
        systemTrayAdapter.setOnResetAction(this::handleReset);
        systemTrayAdapter.setOnShowHideAction(this::toggleWindowVisibility);
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
        if (updateLoop != null) {
            updateLoop.stop();
        }
        systemTrayAdapter.cleanup();
    }
}
