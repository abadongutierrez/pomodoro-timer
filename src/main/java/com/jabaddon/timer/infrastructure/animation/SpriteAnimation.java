package com.jabaddon.timer.infrastructure.animation;

import javafx.animation.AnimationTimer;
import javafx.scene.image.WritableImage;

public class SpriteAnimation {
    private final WritableImage[] frames;
    private final double frameDelayNanos;
    private int currentFrameIndex;
    private long lastFrameTime;
    private boolean playing;
    private boolean loop;
    private AnimationTimer timer;
    private Runnable onFrameUpdate;

    /**
     * Creates a sprite animation
     * @param frames Array of frames to animate
     * @param framesPerSecond Animation speed in FPS
     * @param loop Whether to loop the animation
     */
    public SpriteAnimation(WritableImage[] frames, double framesPerSecond, boolean loop) {
        if (frames == null || frames.length == 0) {
            throw new IllegalArgumentException("Frames array cannot be null or empty");
        }

        this.frames = frames;
        this.frameDelayNanos = 1_000_000_000.0 / framesPerSecond;
        this.currentFrameIndex = 0;
        this.lastFrameTime = 0;
        this.playing = false;
        this.loop = loop;

        initializeTimer();
    }

    private void initializeTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastFrameTime == 0) {
                    lastFrameTime = now;
                    return;
                }

                if (now - lastFrameTime >= frameDelayNanos) {
                    currentFrameIndex++;

                    if (currentFrameIndex >= frames.length) {
                        if (loop) {
                            currentFrameIndex = 0;
                        } else {
                            currentFrameIndex = frames.length - 1;
                            stop();
                        }
                    }

                    lastFrameTime = now;

                    if (onFrameUpdate != null) {
                        onFrameUpdate.run();
                    }
                }
            }
        };
    }

    public void play() {
        if (!playing) {
            playing = true;
            lastFrameTime = 0;
            timer.start();
        }
    }

    public void stop() {
        if (playing) {
            playing = false;
            timer.stop();
        }
    }

    public void reset() {
        currentFrameIndex = 0;
        lastFrameTime = 0;
    }

    public void restart() {
        stop();
        reset();
        play();
    }

    public WritableImage getCurrentFrame() {
        return frames[currentFrameIndex];
    }

    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }

    public int getFrameCount() {
        return frames.length;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setOnFrameUpdate(Runnable callback) {
        this.onFrameUpdate = callback;
    }

    public void cleanup() {
        if (timer != null) {
            timer.stop();
        }
    }
}
