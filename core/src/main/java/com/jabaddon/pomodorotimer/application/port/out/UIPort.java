package com.jabaddon.pomodorotimer.application.port.out;

import com.jabaddon.pomodorotimer.application.dto.SessionTypeDTO;

public interface UIPort {
    void onTimerCompleted(SessionTypeDTO currentTypeDto, SessionTypeDTO nextTypeDto);
}
