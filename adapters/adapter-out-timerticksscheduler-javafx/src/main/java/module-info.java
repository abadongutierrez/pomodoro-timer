/**
 * JavaFX timer ticks scheduler adapter for Pomodoro Timer application.
 *
 * This is a driven adapter (outbound) that implements the timer ticks scheduler port
 * using JavaFX Timeline for periodic updates.
 * All classes are internal - this module does not export any packages.
 * Spring Boot requires open packages for component scanning and dependency injection.
 */
module com.jabaddon.pomodorotimer.adapter.scheduler.javafx {
    // No exports - this is an adapter implementation (not a library)
    // All adapter classes are internal and wired via Spring Boot

    // Dependencies on other modules
    requires com.jabaddon.pomodorotimer.core;

    // JavaFX dependencies
    requires javafx.controls;

    // Spring Boot dependencies
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;

    // Jakarta annotations
    requires jakarta.annotation;

    // Logging
    requires org.slf4j;

    // Open packages to Spring for component scanning and dependency injection
    opens com.jabaddon.pomodorotimer.adapter.out.timerticksscheduler.javafx;
}
