package com.rbaudu.angel.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente une trame vidéo capturée.
 * Cette classe encapsule les données d'une trame vidéo et ses métadonnées.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoFrame {
    
    /**
     * Données d'image encodées en Base64.
     */
    private String imageData;
    
    /**
     * Horodatage de capture de la trame.
     */
    private Instant timestamp;
    
    /**
     * Numéro de séquence de la trame.
     */
    private long sequenceNumber;
    
    /**
     * Largeur de l'image en pixels.
     */
    private int width;
    
    /**
     * Hauteur de l'image en pixels.
     */
    private int height;
    
    /**
     * Format de l'image (ex: JPEG, PNG, etc.).
     */
    private String format;
    
    /**
     * Indique si un mouvement a été détecté dans cette trame.
     */
    private boolean motionDetected;
    
    /**
     * Indique si une personne a été détectée dans cette trame.
     */
    private boolean personDetected;
    
    /**
     * Position X du centre de la personne détectée (si applicable).
     */
    private Integer personX;
    
    /**
     * Position Y du centre de la personne détectée (si applicable).
     */
    private Integer personY;
}
