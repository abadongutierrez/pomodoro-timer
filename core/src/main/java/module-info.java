/**
 * Core module for Pomodoro Timer application.
 *
 * This module contains the pure domain logic and application services
 * following hexagonal architecture principles. It exports only the public API
 * (ports, DTOs, and application services) while keeping domain entities internal.
 */
module com.jabaddon.pomodorotimer.core {
    // Export public API - Input ports (use cases)
    exports com.jabaddon.pomodorotimer.application.port.in;

    // Export public API - Output ports (infrastructure interfaces)
    exports com.jabaddon.pomodorotimer.application.port.out;

    // Export public API - Application services
    exports com.jabaddon.pomodorotimer.application.service;

    // Export public API - Data Transfer Objects
    exports com.jabaddon.pomodorotimer.application.dto;

    // Note: Domain remains internal
    // exports com.jabaddon.pomodorotimer.domain.model;

    // Internal packages:
    //   - com.jabaddon.pomodorotimer.domain.exception

    // Dependencies
    requires org.slf4j;
}
