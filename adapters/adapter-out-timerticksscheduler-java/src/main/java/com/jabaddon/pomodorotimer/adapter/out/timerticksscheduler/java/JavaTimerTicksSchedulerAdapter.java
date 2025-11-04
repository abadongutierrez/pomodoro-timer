package com.jabaddon.pomodorotimer.adapter.out.timerticksscheduler.java;

import com.jabaddon.pomodorotimer.application.port.out.TimerTicksSchedulerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Profile("shell")
public class JavaTimerTicksSchedulerAdapter implements TimerTicksSchedulerPort {

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture;
    private Runnable onTick;
    private volatile boolean paused = false;

    @Override
    public void startTicking(Runnable onTick) {
        this.onTick = onTick;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            if (!paused) {
                this.onTick.run();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void stopTicking() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    @Override
    public void resumeTicking() {
        paused = false;
    }

    @Override
    public void pauseTicking() {
        paused = true;
    }
}
