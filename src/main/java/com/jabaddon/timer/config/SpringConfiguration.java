package com.jabaddon.timer.config;

import com.jabaddon.timer.application.port.out.AnimationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for the Timer application.
 * Provides conditional beans and default implementations.
 */
@Configuration
public class SpringConfiguration {

    /**
     * Provides a no-op AnimationPort implementation when JavaFX animation is not available.
     * This bean is only created if no other AnimationPort bean is registered.
     * Used for headless mode or testing.
     */
    @Bean
    @ConditionalOnMissingBean(AnimationPort.class)
    public AnimationPort noOpAnimationPort() {
        return new AnimationPort() {
            @Override
            public void playAnimation(AnimationType animationType) {
                // No-op implementation for headless mode
            }

            @Override
            public void stopAnimation() {
                // No-op implementation for headless mode
            }
        };
    }
}
