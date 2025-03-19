package com.rbaudu.angel.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente des médias synchronisés pour l'affichage et l'analyse.
 * Cette classe combine des trames vidéo et des segments audio synchronisés.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}

