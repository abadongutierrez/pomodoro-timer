package com.jabaddon.pomodorotimer.application.port.out;

public interface TimerTicksSchedulerPort {
    void startTicking(Runnable onTick);

    void stopTicking();

    void resumeTicking();

    void pauseTicking();
}
