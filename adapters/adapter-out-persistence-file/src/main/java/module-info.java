/**
 * File persistence adapter for Pomodoro Timer application.
 *
 * This is a driven adapter (outbound) that implements the persistence port.
 * All classes are internal - this module does not export any packages.
 * Spring Boot requires open packages for component scanning and dependency injection.
 */
module com.jabaddon.pomodorotimer.adapter.persistence.file {
    // No exports - this is an adapter implementation (not a library)
    // All adapter classes are internal and wired via Spring Boot

    // Dependencies on other modules
    requires com.jabaddon.pomodorotimer.core;

    // Jackson for JSON serialization
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    // Spring Boot dependencies
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;

    // Jakarta annotations
    requires jakarta.annotation;

    // Logging
    requires org.slf4j;
    requires com.fasterxml.jackson.core;

    // Open packages to Spring for component scanning and dependency injection
    opens com.jabaddon.pomodorotimer.adapter.out.timerpersistence.file;
}
