package com.jabaddon.pomodorotimer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.jabaddon.pomodorotimer.application.port.out.NotificationPort;
import com.jabaddon.pomodorotimer.application.port.out.TimerPersistencePort;
import com.jabaddon.pomodorotimer.application.port.out.TimerTicksSchedulerPort;
import com.jabaddon.pomodorotimer.application.port.out.UIPort;
import com.jabaddon.pomodorotimer.application.service.TimerApplicationService;

@Configuration
public class AppConfiguration {

    @Value("${app.data.log-file}")
    private String logFile;

    @Value("${app.sound.enabled}")
    private boolean soundEnabled;

    public String getLogFile() {
        return logFile;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    @Bean
    public TimerApplicationService timerApplicationService(
            TimerTicksSchedulerPort timerTicksSchedulerPort,
            NotificationPort notificationPort,
            TimerPersistencePort timerPersistencePort,
            @Lazy UIPort uiPort) {
        return new TimerApplicationService(
            timerTicksSchedulerPort,
            notificationPort,
            timerPersistencePort,
            uiPort
        );
    }
}
