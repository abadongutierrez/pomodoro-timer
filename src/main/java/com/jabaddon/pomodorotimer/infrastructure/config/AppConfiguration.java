package com.jabaddon.pomodorotimer.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Application configuration properties.
 * Centralizes all configurable values from application.properties.
 */
@Configuration
public class AppConfiguration {

    @Value("${app.data.directory}")
    private String dataDirectory;

    @Value("${app.data.history-file}")
    private String historyFile;

    @Value("${app.data.log-file}")
    private String logFile;

    @Value("${app.sound.enabled}")
    private boolean soundEnabled;

    /**
     * Gets the full path to the application data directory.
     * @return Path to ~/.pomodoro-timer (or configured directory)
     */
    public Path getDataDirectoryPath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, dataDirectory);
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public String getHistoryFile() {
        return historyFile;
    }

    public String getLogFile() {
        return logFile;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}
