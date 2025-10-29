package com.jabaddon.timer.config;

import com.jabaddon.timer.adapter.out.animation.JavaFxAnimationAdapter;
import com.jabaddon.timer.adapter.out.notification.SoundNotificationAdapter;
import com.jabaddon.timer.adapter.out.persistence.FileStatisticsAdapter;
import com.jabaddon.timer.adapter.out.timer.JavaFxTimerAdapter;
import com.jabaddon.timer.application.port.out.AnimationPort;
import com.jabaddon.timer.application.port.out.NotificationPort;
import com.jabaddon.timer.application.port.out.PersistencePort;
import com.jabaddon.timer.application.port.out.TimerPort;
import com.jabaddon.timer.application.service.TimerApplicationService;
import com.jabaddon.timer.infrastructure.animation.AnimatedCharacter;
import javafx.scene.canvas.GraphicsContext;

/**
 * Dependency Injection Container.
 * Manually wires all dependencies for hexagonal architecture.
 * This is where the hexagon is assembled!
 */
public class DependencyContainer {
    private final TimerApplicationService timerService;
    private final JavaFxAnimationAdapter animationAdapter;
    private final SoundNotificationAdapter notificationAdapter;

    public DependencyContainer(AnimatedCharacter character, GraphicsContext gc) {
        // Create driven adapters (implementations of secondary ports)
        TimerPort timerPort = new JavaFxTimerAdapter();
        this.notificationAdapter = new SoundNotificationAdapter();
        PersistencePort persistencePort = new FileStatisticsAdapter();
        this.animationAdapter = new JavaFxAnimationAdapter(character, gc);

        // Create application service with all dependencies injected
        this.timerService = new TimerApplicationService(
                timerPort,
                notificationAdapter,
                persistencePort,
                animationAdapter
        );
    }

    /**
     * Constructor without animation (for testing or headless mode).
     */
    public DependencyContainer() {
        TimerPort timerPort = new JavaFxTimerAdapter();
        this.notificationAdapter = new SoundNotificationAdapter();
        PersistencePort persistencePort = new FileStatisticsAdapter();
        this.animationAdapter = null;

        // No-op animation adapter for headless mode
        AnimationPort noOpAnimation = new AnimationPort() {
            @Override
            public void playAnimation(AnimationType animationType) {}

            @Override
            public void stopAnimation() {}
        };

        this.timerService = new TimerApplicationService(
                timerPort,
                notificationAdapter,
                persistencePort,
                noOpAnimation
        );
    }

    /**
     * Gets the main application service (facade for all use cases).
     */
    public TimerApplicationService getTimerService() {
        return timerService;
    }

    /**
     * Gets the animation adapter (needed for rendering loop).
     */
    public JavaFxAnimationAdapter getAnimationAdapter() {
        return animationAdapter;
    }

    /**
     * Cleanup method to release resources.
     */
    public void shutdown() {
        if (timerService != null) {
            timerService.shutdown();
        }
        if (notificationAdapter != null) {
            notificationAdapter.cleanup();
        }
    }
}
