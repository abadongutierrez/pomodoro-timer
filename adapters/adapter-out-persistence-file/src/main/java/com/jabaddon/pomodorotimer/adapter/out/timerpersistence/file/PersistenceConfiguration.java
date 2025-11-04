package com.jabaddon.pomodorotimer.adapter.out.timerpersistence.file;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class PersistenceConfiguration {
    @Value("${app.data.directory}")
    private String dataDirectory;
    @Value("${app.data.history-file}")
    private String historyFile;

    public Path getDataDirectoryPath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, this.dataDirectory);
    }

    public String getDataDirectory() {
        return this.dataDirectory;
    }

    public String getHistoryFile() {
        return this.historyFile;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return (new ObjectMapper())
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
