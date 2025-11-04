/**
 * JavaFX UI adapter for Pomodoro Timer application.
 *
 * This is a driving adapter (inbound) that implements the UI port.
 * All classes are internal - this module does not export any packages.
 * Spring Boot requires open packages for component scanning and dependency injection.
 */
module com.jabaddon.pomodorotimer.adapter.ui.javafx {
    // Export UI controller package for bootstrap module's JavaFX Application launcher
    // This is a special case where the bootstrap needs direct access to the view controller
    exports com.jabaddon.pomodorotimer.adapter.in.ui.javafx;

    // Dependencies on other modules
    requires com.jabaddon.pomodorotimer.core;

    // JavaFX dependencies
    requires javafx.controls;
    requires javafx.fxml;

    // AWT for system tray (MacOsSystemTrayManager)
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

    // Open packages to Spring for component scanning and dependency injection
    // Also open to JavaFX for FXML loading
    opens com.jabaddon.pomodorotimer.adapter.in.ui.javafx to javafx.fxml;
    opens com.jabaddon.pomodorotimer.adapter.in.ui.javafx.systemtray;
}
