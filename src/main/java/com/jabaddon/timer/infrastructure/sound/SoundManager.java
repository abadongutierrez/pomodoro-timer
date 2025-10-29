package com.jabaddon.timer.infrastructure.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SoundManager {
    private static final int SAMPLE_RATE = 44100;
    private Clip tickClip;
    private Clip alarmClip;
    private boolean soundEnabled = true;

    public SoundManager() {
        try {
            initializeSounds();
        } catch (Exception e) {
            System.err.println("Failed to initialize sounds: " + e.getMessage());
            soundEnabled = false;
        }
    }

    private void initializeSounds() throws LineUnavailableException, IOException {
        tickClip = generateTickSound();
        alarmClip = generateAlarmSound();
    }

    private Clip generateTickSound() throws LineUnavailableException, IOException {
        int duration = 50;
        int frequency = 800;
        byte[] buffer = generateTone(frequency, duration, 0.1);
        return createClipFromBuffer(buffer);
    }

    private Clip generateAlarmSound() throws LineUnavailableException, IOException {
        int beepDuration = 200;
        int pauseDuration = 100;
        int frequency1 = 800;
        int frequency2 = 1000;

        byte[] beep1 = generateTone(frequency1, beepDuration, 0.3);
        byte[] pause = generateTone(0, pauseDuration, 0);
        byte[] beep2 = generateTone(frequency2, beepDuration, 0.3);
        byte[] pause2 = generateTone(0, pauseDuration, 0);
        byte[] beep3 = generateTone(frequency1, beepDuration, 0.3);

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

        return createClipFromBuffer(combined);
    }

    private byte[] generateTone(int frequency, int durationMs, double volume) {
        int numSamples = (SAMPLE_RATE * durationMs) / 1000;
        byte[] buffer = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double angle = 2.0 * Math.PI * i * frequency / SAMPLE_RATE;
            short sample = (short) (Math.sin(angle) * 32767 * volume);

            buffer[i * 2] = (byte) (sample & 0xFF);
            buffer[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        return buffer;
    }

    private Clip createClipFromBuffer(byte[] buffer) throws LineUnavailableException, IOException {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, buffer.length / 2);

        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);

        return clip;
    }

    public void playTick() {
        if (!soundEnabled || tickClip == null) {
            return;
        }

        if (tickClip.isRunning()) {
            tickClip.stop();
        }
        tickClip.setFramePosition(0);
        tickClip.start();
    }

    public void playAlarm() {
        if (!soundEnabled || alarmClip == null) {
            return;
        }

        if (alarmClip.isRunning()) {
            alarmClip.stop();
        }
        alarmClip.setFramePosition(0);
        alarmClip.start();
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void cleanup() {
        if (tickClip != null) {
            tickClip.close();
        }
        if (alarmClip != null) {
            alarmClip.close();
        }
    }
}
