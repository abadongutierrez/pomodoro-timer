/**
 * Bootstrap module for Pomodoro Timer application.
 *
 * This module serves as the entry point and wires all adapters together
 * using Spring Boot dependency injection. It contains the main application
 * launcher and configuration.
 *
 * This module does not export any packages as it's an application entry point,
 * not a library to be consumed by others.
 */
module com.jabaddon.pomodorotimer.bootstrap {
    // No exports - this is an application entry point (not a library)

    // Dependencies on application modules
    requires com.jabaddon.pomodorotimer.core;

    // Adapter dependencies (all adapters wired via Spring)
    requires com.jabaddon.pomodorotimer.adapter.ui.javafx;
    requires com.jabaddon.pomodorotimer.adapter.ui.shell;
    requires com.jabaddon.pomodorotimer.adapter.persistence.file;
    requires com.jabaddon.pomodorotimer.adapter.notification.javafx;
    requires com.jabaddon.pomodorotimer.adapter.notification.shell;
    requires com.jabaddon.pomodorotimer.adapter.scheduler.javafx;
    requires com.jabaddon.pomodorotimer.adapter.scheduler.java;

    // Spring Boot dependencies
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;

    // JavaFX for application launcher
    requires javafx.controls;
    requires javafx.graphics;

    // Jakarta annotations
    requires jakarta.annotation;

    // Logging
    requires org.slf4j;

    // Open packages to Spring for component scanning and dependency injection
    opens com.jabaddon.pomodorotimer;
    opens com.jabaddon.pomodorotimer.config;

    // Export main class for JavaFX and Spring Boot launchers
    exports com.jabaddon.pomodorotimer;
}
