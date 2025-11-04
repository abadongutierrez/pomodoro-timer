package com.jabaddon.pomodorotimer.adapter.out.notification.shell;

import com.jabaddon.pomodorotimer.application.dto.SessionTypeDTO;
import com.jabaddon.pomodorotimer.application.port.out.NotificationPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("shell")
public class ShellNotificationAdapter implements NotificationPort {
    @Override
    public void playTickSound() {
        System.out.print("\u0007");
    }

    @Override
    public void playAlarmSound() {
        System.out.print("\u0007");
    }

    @Override
    public void showCompletionNotification(SessionTypeDTO currentTypeDto, SessionTypeDTO nextTypeDto) {
        System.out.printf("Session completed: %s. Next session: %s%n",
                currentTypeDto.displayName(), nextTypeDto.displayName());
    }
}
