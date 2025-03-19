package com.rbaudu.angel.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente un segment audio capturé.
 * Cette classe encapsule les données audio et ses métadonnées.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
