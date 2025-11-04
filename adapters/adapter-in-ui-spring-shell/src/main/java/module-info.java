/**
 * Spring Shell UI adapter for Pomodoro Timer application.
 *
 * This is a driving adapter (inbound) that implements the UI port via CLI.
 * All classes are internal - this module does not export any packages.
 * Spring Boot requires open packages for component scanning and dependency injection.
 */
module com.jabaddon.pomodorotimer.adapter.ui.shell {
    // No exports - this is an adapter implementation (not a library)
    // All adapter classes are internal and wired via Spring Boot

    // Dependencies on other modules
    requires com.jabaddon.pomodorotimer.core;

    // Spring dependencies
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;

    // Jakarta annotations (@PostConstruct, @PreDestroy, etc.)
    requires jakarta.annotation;

    // Spring Shell - NOTE: Spring Shell 3.2.0 is not fully modularized
    // It has split packages which violate JPMS rules
    // We include individual modules that don't conflict
    requires spring.shell.standard;

    // Lanterna for TUI (automatic module name derived from jar name)
    requires com.googlecode.lanterna;

    // Logging
    requires org.slf4j;

    // Open packages to Spring for component scanning and dependency injection
    opens com.jabaddon.pomodorotimer.adapter.in.ui.shell;
}
