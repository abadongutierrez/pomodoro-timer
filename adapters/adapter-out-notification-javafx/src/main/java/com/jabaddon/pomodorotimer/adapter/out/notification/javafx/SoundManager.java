package com.jabaddon.pomodorotimer.adapter.out.notification.javafx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SoundManager {
    private static final Logger log = LoggerFactory.getLogger(SoundManager.class);
    private static final int SAMPLE_RATE = 44100;
    private Clip tickClip;
    private Clip alarmClip;
    private boolean soundEnabled;

    public SoundManager() {
        this(true);
    }

    public SoundManager(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;

        try {
            this.initializeSounds();
        } catch (Exception e) {
            log.error("Failed to initialize sounds: {}", e.getMessage(), e);
            this.soundEnabled = false;
        }

    }

    private void initializeSounds() throws LineUnavailableException, IOException {
        this.tickClip = this.generateTickSound();
        this.alarmClip = this.generateAlarmSound();
    }

    private Clip generateTickSound() throws LineUnavailableException, IOException {
        int duration = 50;
        int frequency = 800;
        byte[] buffer = this.generateTone(frequency, duration, 0.1);
        return this.createClipFromBuffer(buffer);
    }

    private Clip generateAlarmSound() throws LineUnavailableException, IOException {
        int beepDuration = 200;
        int pauseDuration = 100;
        int frequency1 = 800;
        int frequency2 = 1000;
        byte[] beep1 = this.generateTone(frequency1, beepDuration, 0.3);
        byte[] pause = this.generateTone(0, pauseDuration, (double)0.0F);
        byte[] beep2 = this.generateTone(frequency2, beepDuration, 0.3);
        byte[] pause2 = this.generateTone(0, pauseDuration, (double)0.0F);
        byte[] beep3 = this.generateTone(frequency1, beepDuration, 0.3);
        byte[] combined = new byte[beep1.length + pause.length + beep2.length + pause2.length + beep3.length];
        int pos = 0;
        System.arraycopy(beep1, 0, combined, pos, beep1.length);
        pos += beep1.length;
        System.arraycopy(pause, 0, combined, pos, pause.length);
        pos += pause.length;
        System.arraycopy(beep2, 0, combined, pos, beep2.length);
        pos += beep2.length;
        System.arraycopy(pause2, 0, combined, pos, pause2.length);
        pos += pause2.length;
        System.arraycopy(beep3, 0, combined, pos, beep3.length);
        return this.createClipFromBuffer(combined);
    }

    private byte[] generateTone(int frequency, int durationMs, double volume) {
        int numSamples = '걄' * durationMs / 1000;
        byte[] buffer = new byte[numSamples * 2];

        for(int i = 0; i < numSamples; ++i) {
            double angle = (Math.PI * 2D) * (double)i * (double)frequency / (double)44100.0F;
            short sample = (short)((int)(Math.sin(angle) * (double)32767.0F * volume));
            buffer[i * 2] = (byte)(sample & 255);
            buffer[i * 2 + 1] = (byte)(sample >> 8 & 255);
        }

        return buffer;
    }

    private Clip createClipFromBuffer(byte[] buffer) throws LineUnavailableException, IOException {
        AudioFormat format = new AudioFormat(44100.0F, 16, 1, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, (long)(buffer.length / 2));
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        return clip;
    }

    public void playTick() {
        if (this.soundEnabled && this.tickClip != null) {
            if (this.tickClip.isRunning()) {
                this.tickClip.stop();
            }

            this.tickClip.setFramePosition(0);
            this.tickClip.start();
        }
    }

    public void playAlarm() {
        if (this.soundEnabled && this.alarmClip != null) {
            if (this.alarmClip.isRunning()) {
                this.alarmClip.stop();
            }

            this.alarmClip.setFramePosition(0);
            this.alarmClip.start();
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public boolean isSoundEnabled() {
        return this.soundEnabled;
    }

    public void cleanup() {
        if (this.tickClip != null) {
            this.tickClip.close();
        }

        if (this.alarmClip != null) {
            this.alarmClip.close();
        }

    }
}

