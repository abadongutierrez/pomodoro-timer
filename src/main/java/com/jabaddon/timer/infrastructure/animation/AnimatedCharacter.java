package com.jabaddon.timer.infrastructure.animation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import java.util.HashMap;
import java.util.Map;

public class AnimatedCharacter {
    private final Map<String, SpriteAnimation> animations;
    private SpriteAnimation currentAnimation;
    private String currentAnimationName;
    private double x;
    private double y;
    private double scale;

    public AnimatedCharacter(double x, double y) {
        this(x, y, 1.0);
    }

    public AnimatedCharacter(double x, double y, double scale) {
        this.animations = new HashMap<>();
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    /**
     * Adds an animation with a name
     * @param name Animation name (e.g., "walk", "idle")
     * @param animation The SpriteAnimation object
     */
    public void addAnimation(String name, SpriteAnimation animation) {
        animations.put(name, animation);

        if (currentAnimation == null) {
            currentAnimation = animation;
            currentAnimationName = name;
        }
    }

    /**
     * Switches to a different animation
     * @param name Name of the animation to play
     */
    public void playAnimation(String name) {
        if (!animations.containsKey(name)) {
            throw new IllegalArgumentException("Animation not found: " + name);
        }

        if (!name.equals(currentAnimationName)) {
            if (currentAnimation != null) {
                currentAnimation.stop();
            }

            currentAnimation = animations.get(name);
            currentAnimationName = name;
            currentAnimation.restart();
        } else if (!currentAnimation.isPlaying()) {
            currentAnimation.play();
        }
    }

    /**
     * Stops the current animation
     */
    public void stopAnimation() {
        if (currentAnimation != null) {
            currentAnimation.stop();
        }
    }

    /**
     * Renders the current frame to a GraphicsContext
     * @param gc The GraphicsContext to draw on
     */
    public void render(GraphicsContext gc) {
        if (currentAnimation != null) {
            WritableImage frame = currentAnimation.getCurrentFrame();
            double width = frame.getWidth() * scale;
            double height = frame.getHeight() * scale;

            gc.drawImage(frame, x, y, width, height);
        }
    }

    /**
     * Updates character position
     * @param x New X coordinate
     * @param y New Y coordinate
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getScale() {
        return scale;
    }

    public String getCurrentAnimationName() {
        return currentAnimationName;
    }

    public boolean isAnimationPlaying() {
        return currentAnimation != null && currentAnimation.isPlaying();
    }

    /**
     * Gets the width of the current frame (scaled)
     */
    public double getWidth() {
        if (currentAnimation != null) {
            return currentAnimation.getCurrentFrame().getWidth() * scale;
        }
        return 0;
    }

    /**
     * Gets the height of the current frame (scaled)
     */
    public double getHeight() {
        if (currentAnimation != null) {
            return currentAnimation.getCurrentFrame().getHeight() * scale;
        }
        return 0;
    }

    public void cleanup() {
        for (SpriteAnimation animation : animations.values()) {
            animation.cleanup();
        }
    }
}
