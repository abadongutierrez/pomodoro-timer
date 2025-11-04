package com.jabaddon.pomodorotimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main entry point for Pomodoro Timer application.
 * Supports both JavaFX and Spring Shell UI modes via Spring profiles.
 *
 * Usage:
 *   JavaFX UI:     mvn spring-boot:run -Dspring-boot.run.profiles=javafx
 *   Shell UI:      mvn spring-boot:run -Dspring-boot.run.profiles=shell
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.jabaddon.pomodorotimer.config",
    "com.jabaddon.pomodorotimer.adapter.out.timerpersistence",
    "com.jabaddon.pomodorotimer.adapter.out.timerticksscheduler",
    "com.jabaddon.pomodorotimer.adapter.out.notification",
    "com.jabaddon.pomodorotimer.adapter.in.ui"
})
public class PomodoroTimerApplication {
    private static final Logger log = LoggerFactory.getLogger(PomodoroTimerApplication.class);

    public static void main(String[] args) {
        // Determine which profile to use
        String activeProfile = getActiveProfile(args);

        log.info("Starting Pomodoro Timer with profile: {}", activeProfile);

        if ("javafx".equals(activeProfile)) {
            // Launch JavaFX application
            launchJavaFxApp(args);
        } else if ("shell".equals(activeProfile)) {
            // Launch Spring Shell application
            launchSpringShellApp(args);
        } else {
            log.error("Unknown profile: {}. Use 'javafx' or 'shell'", activeProfile);
            System.err.println("Error: Unknown profile '" + activeProfile + "'");
            System.err.println("Usage: mvn spring-boot:run -Dspring-boot.run.profiles=<javafx|shell>");
            System.exit(1);
        }
    }

    private static void launchJavaFxApp(String[] args) {
        // Set JavaFX and macOS-specific properties
        System.setProperty("java.awt.headless", "false");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Pomodoro Timer");

        log.info("Launching JavaFX UI...");

        // Create Spring context with javafx profile
        SpringApplication app = new SpringApplication(PomodoroTimerApplication.class);
        app.setAdditionalProfiles("javafx");
        org.springframework.context.ConfigurableApplicationContext context = app.run(args);

        // Pass Spring context to JavaFX Application
        Application.setSpringContext(context);

        // Launch JavaFX Application
        javafx.application.Application.launch(Application.class, args);
    }

    private static void launchSpringShellApp(String[] args) {
        log.info("Launching Spring Shell UI...");

        // Create Spring context with shell profile
        SpringApplication app = new SpringApplication(PomodoroTimerApplication.class);
        app.setAdditionalProfiles("shell");
        app.run(args);
    }

    private static String getActiveProfile(String[] args) {
        // Check command line args first
        for (String arg : args) {
            if (arg.startsWith("--spring.profiles.active=")) {
                return arg.substring("--spring.profiles.active=".length());
            }
        }

        // Check environment variable
        String envProfile = System.getenv("SPRING_PROFILES_ACTIVE");
        if (envProfile != null && !envProfile.isEmpty()) {
            return envProfile;
        }

        // Check system property
        String profile = System.getProperty("spring.profiles.active");
        if (profile != null && !profile.isEmpty()) {
            return profile;
        }

        // Default to javafx (as configured in application.properties)
        return "javafx";
    }
}
