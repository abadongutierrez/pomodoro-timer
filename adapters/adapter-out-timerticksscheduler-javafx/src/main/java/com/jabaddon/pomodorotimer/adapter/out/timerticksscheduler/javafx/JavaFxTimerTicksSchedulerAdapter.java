package com.jabaddon.pomodorotimer.adapter.out.timerticksscheduler.javafx;

import com.jabaddon.pomodorotimer.application.port.out.TimerTicksSchedulerPort;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("javafx")
public class JavaFxTimerTicksSchedulerAdapter implements TimerTicksSchedulerPort {
    private Timeline timeline;
    private Runnable currentCallback;

    public void startTicking(Runnable onTick) {
        this.currentCallback = onTick;
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1.0F), (event) -> {
            if (this.currentCallback != null) {
                this.currentCallback.run();
            }

        }));
        this.timeline.setCycleCount(-1);
        this.timeline.play();
    }

    public void stopTicking() {
        if (this.timeline != null) {
            this.timeline.stop();
        }

        this.currentCallback = null;
    }

    public void pauseTicking() {
        if (this.timeline != null) {
            this.timeline.pause();
        }

    }

    public void resumeTicking() {
        if (this.timeline != null) {
            this.timeline.play();
        }

    }
}

