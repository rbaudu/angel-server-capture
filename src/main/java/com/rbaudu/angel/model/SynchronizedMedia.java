package com.rbaudu.angel.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Représente des médias synchronisés pour l'affichage et l'analyse.
 * Cette classe combine des trames vidéo et des segments audio synchronisés.
 */
public class SynchronizedMedia {
    
    /**
     * Identifiant unique pour ce média synchronisé.
     */
    private String id;
    
    /**
     * Trame vidéo associée.
     */
    private VideoFrame videoFrame;
    
    /**
     * Segment audio associé.
     */
    private AudioChunk audioChunk;
    
    /**
     * Horodatage de synchronisation.
     */
    private Instant syncTimestamp;
    
    /**
     * Indique si cette synchronisation contient une vidéo.
     */
    private boolean hasVideo;
    
    /**
     * Indique si cette synchronisation contient un audio.
     */
    private boolean hasAudio;
    
    /**
     * Délai de synchronisation en millisecondes.
     */
    private long syncDelayMs;
    
    /**
     * Résultats d'analyse additionnels en format JSON.
     */
    private String analysisResults;

    /**
     * Constructeur par défaut
     */
    public SynchronizedMedia() {
    }

    /**
     * Constructeur avec tous les champs
     */
    public SynchronizedMedia(String id, VideoFrame videoFrame, AudioChunk audioChunk, Instant syncTimestamp,
                            boolean hasVideo, boolean hasAudio, long syncDelayMs, String analysisResults) {
        this.id = id;
        this.videoFrame = videoFrame;
        this.audioChunk = audioChunk;
        this.syncTimestamp = syncTimestamp;
        this.hasVideo = hasVideo;
        this.hasAudio = hasAudio;
        this.syncDelayMs = syncDelayMs;
        this.analysisResults = analysisResults;
    }

    /**
     * Getters et Setters
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    public void setVideoFrame(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    public AudioChunk getAudioChunk() {
        return audioChunk;
    }

    public void setAudioChunk(AudioChunk audioChunk) {
        this.audioChunk = audioChunk;
    }

    public Instant getSyncTimestamp() {
        return syncTimestamp;
    }

    public void setSyncTimestamp(Instant syncTimestamp) {
        this.syncTimestamp = syncTimestamp;
    }

    public boolean isHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    public boolean isHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(boolean hasAudio) {
        this.hasAudio = hasAudio;
    }

    public long getSyncDelayMs() {
        return syncDelayMs;
    }

    public void setSyncDelayMs(long syncDelayMs) {
        this.syncDelayMs = syncDelayMs;
    }

    public String getAnalysisResults() {
        return analysisResults;
    }

    public void setAnalysisResults(String analysisResults) {
        this.analysisResults = analysisResults;
    }
    
    /**
     * Crée un média synchronisé avec seulement une trame vidéo.
     * 
     * @param videoFrame la trame vidéo
     * @return un nouvel objet SynchronizedMedia
     */
    public static SynchronizedMedia ofVideo(VideoFrame videoFrame) {
        return SynchronizedMedia.builder()
                .id(generateId(videoFrame.getTimestamp()))
                .videoFrame(videoFrame)
                .syncTimestamp(videoFrame.getTimestamp())
                .hasVideo(true)
                .hasAudio(false)
                .syncDelayMs(0)
                .build();
    }
    
    /**
     * Crée un média synchronisé avec seulement un segment audio.
     * 
     * @param audioChunk le segment audio
     * @return un nouvel objet SynchronizedMedia
     */
    public static SynchronizedMedia ofAudio(AudioChunk audioChunk) {
        return SynchronizedMedia.builder()
                .id(generateId(audioChunk.getTimestamp()))
                .audioChunk(audioChunk)
                .syncTimestamp(audioChunk.getTimestamp())
                .hasVideo(false)
                .hasAudio(true)
                .syncDelayMs(0)
                .build();
    }
    
    /**
     * Génère un identifiant unique basé sur l'horodatage.
     * 
     * @param timestamp l'horodatage de référence
     * @return un identifiant unique sous forme de chaîne
     */
    private static String generateId(Instant timestamp) {
        return "sync-" + timestamp.toEpochMilli();
    }

    /**
     * Méthode equals pour la comparaison des objets
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynchronizedMedia that = (SynchronizedMedia) o;
        return hasVideo == that.hasVideo &&
               hasAudio == that.hasAudio &&
               syncDelayMs == that.syncDelayMs &&
               Objects.equals(id, that.id) &&
               Objects.equals(videoFrame, that.videoFrame) &&
               Objects.equals(audioChunk, that.audioChunk) &&
               Objects.equals(syncTimestamp, that.syncTimestamp) &&
               Objects.equals(analysisResults, that.analysisResults);
    }

    /**
     * Méthode hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, videoFrame, audioChunk, syncTimestamp, hasVideo,
                          hasAudio, syncDelayMs, analysisResults);
    }

    /**
     * Méthode toString
     */
    @Override
    public String toString() {
        return "SynchronizedMedia{" +
               "id='" + id + '\'' +
               ", syncTimestamp=" + syncTimestamp +
               ", hasVideo=" + hasVideo +
               ", hasAudio=" + hasAudio +
               ", syncDelayMs=" + syncDelayMs +
               '}';
    }

    /**
     * Builder statique pour créer des instances de SynchronizedMedia
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private VideoFrame videoFrame;
        private AudioChunk audioChunk;
        private Instant syncTimestamp;
        private boolean hasVideo;
        private boolean hasAudio;
        private long syncDelayMs;
        private String analysisResults;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder videoFrame(VideoFrame videoFrame) {
            this.videoFrame = videoFrame;
            return this;
        }

        public Builder audioChunk(AudioChunk audioChunk) {
            this.audioChunk = audioChunk;
            return this;
        }

        public Builder syncTimestamp(Instant syncTimestamp) {
            this.syncTimestamp = syncTimestamp;
            return this;
        }

        public Builder hasVideo(boolean hasVideo) {
            this.hasVideo = hasVideo;
            return this;
        }

        public Builder hasAudio(boolean hasAudio) {
            this.hasAudio = hasAudio;
            return this;
        }

        public Builder syncDelayMs(long syncDelayMs) {
            this.syncDelayMs = syncDelayMs;
            return this;
        }

        public Builder analysisResults(String analysisResults) {
            this.analysisResults = analysisResults;
            return this;
        }

        public SynchronizedMedia build() {
            return new SynchronizedMedia(id, videoFrame, audioChunk, syncTimestamp,
                                      hasVideo, hasAudio, syncDelayMs, analysisResults);
        }
    }
}