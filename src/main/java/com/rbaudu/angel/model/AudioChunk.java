package com.rbaudu.angel.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Représente un segment audio capturé.
 * Cette classe encapsule les données audio et ses métadonnées.
 */
public class AudioChunk {
    
    /**
     * Données audio encodées en Base64.
     */
    private String audioData;
    
    /**
     * Horodatage de capture du segment audio.
     */
    private Instant timestamp;
    
    /**
     * Numéro de séquence du segment.
     */
    private long sequenceNumber;
    
    /**
     * Taux d'échantillonnage (Hz).
     */
    private int sampleRate;
    
    /**
     * Nombre de canaux audio (1 pour mono, 2 pour stéréo).
     */
    private int channels;
    
    /**
     * Format audio (ex: WAV, MP3, etc.).
     */
    private String format;
    
    /**
     * Durée du segment audio en millisecondes.
     */
    private int durationMs;
    
    /**
     * Niveau sonore maximal dans ce segment (dB).
     */
    private Double maxSoundLevel;
    
    /**
     * Niveau sonore moyen dans ce segment (dB).
     */
    private Double avgSoundLevel;
    
    /**
     * Indique si un son significatif a été détecté.
     */
    private boolean soundDetected;

    /**
     * Constructeur par défaut
     */
    public AudioChunk() {
    }

    /**
     * Constructeur avec tous les champs
     */
    public AudioChunk(String audioData, Instant timestamp, long sequenceNumber, int sampleRate,
                     int channels, String format, int durationMs, Double maxSoundLevel,
                     Double avgSoundLevel, boolean soundDetected) {
        this.audioData = audioData;
        this.timestamp = timestamp;
        this.sequenceNumber = sequenceNumber;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.format = format;
        this.durationMs = durationMs;
        this.maxSoundLevel = maxSoundLevel;
        this.avgSoundLevel = avgSoundLevel;
        this.soundDetected = soundDetected;
    }

    /**
     * Getters et Setters
     */
    public String getAudioData() {
        return audioData;
    }

    public void setAudioData(String audioData) {
        this.audioData = audioData;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(int durationMs) {
        this.durationMs = durationMs;
    }

    public Double getMaxSoundLevel() {
        return maxSoundLevel;
    }

    public void setMaxSoundLevel(Double maxSoundLevel) {
        this.maxSoundLevel = maxSoundLevel;
    }

    public Double getAvgSoundLevel() {
        return avgSoundLevel;
    }

    public void setAvgSoundLevel(Double avgSoundLevel) {
        this.avgSoundLevel = avgSoundLevel;
    }

    public boolean isSoundDetected() {
        return soundDetected;
    }

    public void setSoundDetected(boolean soundDetected) {
        this.soundDetected = soundDetected;
    }

    /**
     * Méthode equals pour la comparaison des objets
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioChunk that = (AudioChunk) o;
        return sequenceNumber == that.sequenceNumber &&
               sampleRate == that.sampleRate &&
               channels == that.channels &&
               durationMs == that.durationMs &&
               soundDetected == that.soundDetected &&
               Objects.equals(audioData, that.audioData) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(format, that.format) &&
               Objects.equals(maxSoundLevel, that.maxSoundLevel) &&
               Objects.equals(avgSoundLevel, that.avgSoundLevel);
    }

    /**
     * Méthode hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(audioData, timestamp, sequenceNumber, sampleRate, channels,
                          format, durationMs, maxSoundLevel, avgSoundLevel, soundDetected);
    }

    /**
     * Méthode toString
     */
    @Override
    public String toString() {
        return "AudioChunk{" +
               "timestamp=" + timestamp +
               ", sequenceNumber=" + sequenceNumber +
               ", sampleRate=" + sampleRate +
               ", channels=" + channels +
               ", format='" + format + '\'' +
               ", durationMs=" + durationMs +
               ", maxSoundLevel=" + maxSoundLevel +
               ", avgSoundLevel=" + avgSoundLevel +
               ", soundDetected=" + soundDetected +
               '}';
        // audioData est exclu pour éviter une sortie trop verbale
    }

    /**
     * Builder statique pour créer des instances de AudioChunk
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String audioData;
        private Instant timestamp;
        private long sequenceNumber;
        private int sampleRate;
        private int channels;
        private String format;
        private int durationMs;
        private Double maxSoundLevel;
        private Double avgSoundLevel;
        private boolean soundDetected;

        public Builder audioData(String audioData) {
            this.audioData = audioData;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder sequenceNumber(long sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        public Builder sampleRate(int sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }

        public Builder channels(int channels) {
            this.channels = channels;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder durationMs(int durationMs) {
            this.durationMs = durationMs;
            return this;
        }

        public Builder maxSoundLevel(Double maxSoundLevel) {
            this.maxSoundLevel = maxSoundLevel;
            return this;
        }

        public Builder avgSoundLevel(Double avgSoundLevel) {
            this.avgSoundLevel = avgSoundLevel;
            return this;
        }

        public Builder soundDetected(boolean soundDetected) {
            this.soundDetected = soundDetected;
            return this;
        }

        public AudioChunk build() {
            return new AudioChunk(audioData, timestamp, sequenceNumber, sampleRate, channels,
                               format, durationMs, maxSoundLevel, avgSoundLevel, soundDetected);
        }
    }
}