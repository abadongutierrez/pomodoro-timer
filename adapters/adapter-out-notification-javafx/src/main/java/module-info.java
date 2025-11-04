/**
 * JavaFX notification adapter for Pomodoro Timer application.
 *
 * This is a driven adapter (outbound) that implements the notification port
 * using JavaFX Media for sound playback.
 * All classes are internal - this module does not export any packages.
 * Spring Boot requires open packages for component scanning and dependency injection.
 */
module com.jabaddon.pomodorotimer.adapter.notification.javafx {
    // No exports - this is an adapter implementation (not a library)
    // All adapter classes are internal and wired via Spring Boot

    // Dependencies on other modules
    requires com.jabaddon.pomodorotimer.core;

    // JavaFX dependencies
    requires javafx.controls;
    requires javafx.media;

    // Java Sound API for audio playback
    requires java.desktop;

    // Spring Boot dependencies
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;

    // Jakarta annotations
    requires jakarta.annotation;

    // Logging
    requires org.slf4j;
    requires javafx.graphics;

    // Open packages to Spring for component scanning and dependency injection
    opens com.jabaddon.pomodorotimer.adapter.out.notification.javafx;
}
