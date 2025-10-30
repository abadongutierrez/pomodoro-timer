package com.jabaddon.pomodorotimer;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.jabaddon.pomodorotimer.adapter.in.ui.TimerViewController;

/**
 * JavaFX Application entry point integrated with Spring Boot.
 * Bootstraps Spring Boot context and hexagonal architecture with dependency injection.
 */
@SpringBootApplication
public class Application extends javafx.application.Application {
    private static ConfigurableApplicationContext springContext;
    private TimerViewController controller;

    @Override
    public void init() {
        // Initialize Spring Boot context before JavaFX starts
        springContext = SpringApplication.run(Application.class);
    }

    @Override
    public void start(Stage primaryStage) {
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

    public static void main(String[] args) {
        // Explicitly set headless mode to false to enable system tray on macOS
        System.setProperty("java.awt.headless", "false");

        // Enable macOS-specific menu bar integration
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Pomodoro Timer");

        launch(args);
    }
}
