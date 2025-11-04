package com.jabaddon.pomodorotimer.application.port.out;

import com.jabaddon.pomodorotimer.application.dto.SessionTypeDTO;

public interface NotificationPort {
    void playTickSound();

    void playAlarmSound();

    void showCompletionNotification(SessionTypeDTO currentTypeDto, SessionTypeDTO nextTypeDto);
}
