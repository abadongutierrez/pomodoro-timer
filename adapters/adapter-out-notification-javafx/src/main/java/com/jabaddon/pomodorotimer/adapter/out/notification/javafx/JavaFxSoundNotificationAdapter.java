package com.jabaddon.pomodorotimer.adapter.out.notification.javafx;

import com.jabaddon.pomodorotimer.application.dto.SessionTypeDTO;
import com.jabaddon.pomodorotimer.application.dto.SessionTypeEnumDTO;
import com.jabaddon.pomodorotimer.application.port.out.NotificationPort;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("javafx")
public class JavaFxSoundNotificationAdapter implements NotificationPort {
    private final SoundManager soundManager;

    @Autowired
    public JavaFxSoundNotificationAdapter(@Value("${app.sound.enabled:true}") boolean soundEnabled) {
        this.soundManager = new SoundManager(soundEnabled);
    }

    public JavaFxSoundNotificationAdapter(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @Override
    public void playTickSound() {
        if (this.soundManager != null) {
            this.soundManager.playTick();
        }

    }

    @Override
    public void playAlarmSound() {
        if (this.soundManager != null) {
            this.soundManager.playAlarm();
        }

    }

    @Override
    public void showCompletionNotification(SessionTypeDTO completedType, SessionTypeDTO nextType) {
        String var10000 = this.getDisplayName(completedType);
        String title = var10000 + " Complete!";
        String message = this.buildCompletionMessage(completedType, nextType);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText((String)null);
            alert.setContentText(message);
            alert.show();
        });
    }

    private String buildCompletionMessage(SessionTypeDTO completedType, SessionTypeDTO nextType) {
        if (completedType.sessionType() == SessionTypeEnumDTO.WORK) {
            String var3 = this.getDisplayName(nextType);
            return "Great work! Time for a break.\nNext: " + var3;
        } else {
            String var10000 = this.getDisplayName(nextType);
            return "Break is over! Ready to focus?\nNext: " + var10000;
        }
    }

    private String getDisplayName(SessionTypeDTO sessionType) {
        return switch (sessionType.sessionType()) {
            case WORK -> "Work Session";
            case SHORT_BREAK -> "Short Break";
            case LONG_BREAK -> "Long Break";
            default -> throw new IllegalArgumentException("Invalid session type");
        };
    }

    public void cleanup() {
        if (this.soundManager != null) {
            this.soundManager.cleanup();
        }

    }
}