package com.jabaddon.pomodorotimer;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import com.jabaddon.pomodorotimer.adapter.in.ui.TimerViewController;

/**
 * JavaFX Application class.
 * This is launched by PomodoroTimerApplication when javafx profile is active.
 * The Spring context is passed in and managed by PomodoroTimerApplication.
 */
public class Application extends javafx.application.Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static ConfigurableApplicationContext springContext;
    private TimerViewController controller;

    /**
     * Sets the Spring context to be used by this JavaFX application.
     * Called by PomodoroTimerApplication before launching JavaFX.
     */
    public static void setSpringContext(ConfigurableApplicationContext context) {
        springContext = context;
    }

    @Override
    public void init() {
        // Spring context is set by PomodoroTimerApplication before launch
        if (springContext == null) {
            throw new IllegalStateException("Spring context not set. Call Application.setSpringContext() before launching.");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Set UTILITY style to make window float across all macOS desktops/Spaces
            primaryStage.initStyle(StageStyle.UTILITY);

            // Get UI controller from Spring context (with all dependencies injected)
            controller = springContext.getBean(TimerViewController.class);
            Scene scene = controller.createScene(primaryStage);

            primaryStage.setTitle("Pomodoro Timer");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(e -> cleanup());
            primaryStage.show();
        } catch (Exception e) {
            log.error("Failed to start JavaFX application: {}", e.getMessage(), e);
        }
    }

    private void cleanup() {
        if (controller != null) {
            controller.shutdown();
        }
        if (springContext != null) {
            springContext.close();
        }

        // Force exit since we disabled implicit exit for system tray
        javafx.application.Platform.exit();
        System.exit(0);
    }

    @Override
    public void stop() {
        cleanup();
    }
}
