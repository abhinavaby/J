package com.connectai.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.TargetDataLine;

public class AudioUtil {
    private TargetDataLine targetDataLine;
    private ByteArrayOutputStream recordStream;
    private boolean isRecording = false;
    private Thread recordThread;
    private Clip currentClip;
    private long recordStartTime;
    private long recordedDurationMs;

    public AudioFormat getStandardAudioFormat() {
        // 16000 Hz, 16 bit, Mono, Signed, Little-Endian (Standard clean voice recording format)
        return new AudioFormat(16000.0f, 16, 1, true, false);
    }

    public synchronized boolean startRecording() throws Exception {
        AudioFormat format = getStandardAudioFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            throw new IllegalStateException("Microphone line not supported on this device.");
        }

        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open(format);
        targetDataLine.start();

        recordStream = new ByteArrayOutputStream();
        isRecording = true;
        recordStartTime = System.currentTimeMillis();

        recordThread = new Thread(() -> {
            byte[] buffer = new byte[2048];
            while (isRecording) {
                int read = targetDataLine.read(buffer, 0, buffer.length);
                if (read > 0) {
                    recordStream.write(buffer, 0, read);
                }
            }
        });
        recordThread.start();
        return true;
    }

    public synchronized byte[] stopRecording() {
        if (!isRecording) return new byte[0];

        isRecording = false;
        recordedDurationMs = System.currentTimeMillis() - recordStartTime;

        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
        }

        if (recordThread != null) {
            try {
                recordThread.join(1000);
            } catch (InterruptedException ignored) {}
        }

        byte[] rawAudioBytes = recordStream.toByteArray();
        return convertRawToWav(rawAudioBytes);
    }

    public byte[] convertRawToWav(byte[] rawAudioBytes) {
        try {
            AudioFormat format = getStandardAudioFormat();
            ByteArrayInputStream bais = new ByteArrayInputStream(rawAudioBytes);
            AudioInputStream ais = new AudioInputStream(bais, format, rawAudioBytes.length / format.getFrameSize());
            ByteArrayOutputStream wavBaos = new ByteArrayOutputStream();
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavBaos);
            return wavBaos.toByteArray();
        } catch (Exception e) {
            return rawAudioBytes;
        }
    }

    public void playWavBytes(byte[] wavBytes, Runnable onComplete) {
        stopPlayback();
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(wavBytes));
            currentClip = AudioSystem.getClip();
            currentClip.open(ais);
            currentClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    currentClip.close();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            });
            currentClip.start();
        } catch (Exception e) {
            System.err.println("Failed to play audio clip: " + e.getMessage());
        }
    }

    public void playWavFile(File wavFile, Runnable onComplete) {
        stopPlayback();
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(wavFile);
            currentClip = AudioSystem.getClip();
            currentClip.open(ais);
            currentClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    currentClip.close();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            });
            currentClip.start();
        } catch (Exception e) {
            System.err.println("Failed to play audio file: " + e.getMessage());
        }
    }

    public void stopPlayback() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.close();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public long getRecordedDurationSeconds() {
        if (isRecording) {
            return (System.currentTimeMillis() - recordStartTime) / 1000;
        }
        return recordedDurationMs / 1000;
    }
}
