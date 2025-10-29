package com.jabaddon.timer;

import com.jabaddon.timer.adapter.in.ui.TimerViewController;
import com.jabaddon.timer.config.DependencyContainer;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * JavaFX Application entry point.
 * Bootstraps hexagonal architecture by creating dependency container
 * and wiring up the UI controller.
 */
public class Application extends javafx.application.Application {
    private DependencyContainer container;
    private TimerViewController controller;

    @Override
    public void start(Stage primaryStage) {
        // Set UTILITY style to make window float across all macOS desktops/Spaces
        primaryStage.initStyle(StageStyle.UTILITY);

        // Create dependency container (wires hexagonal architecture)
        container = new DependencyContainer();

        // Create UI controller with injected dependencies
        controller = new TimerViewController(container);
        Scene scene = controller.createScene(primaryStage);

        primaryStage.setTitle("Pomodoro Timer");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        // AlwaysOnTop is managed dynamically by TimerViewController based on focus
        primaryStage.setOnCloseRequest(e -> cleanup());
        primaryStage.show();
    }

    private void cleanup() {
        if (controller != null) {
            controller.shutdown();
        }
        if (container != null) {
            container.shutdown();
        }

        // Force exit since we disabled implicit exit for system tray
        javafx.application.Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
