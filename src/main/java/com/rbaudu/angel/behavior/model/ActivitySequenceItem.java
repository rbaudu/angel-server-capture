package com.rbaudu.angel.behavior.model;

import com.rbaudu.angel.analyzer.model.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Représente une activité dans une séquence avec son horodatage
 * et sa durée.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySequenceItem {
    
    /**
     * Type d'activité
     */
    private ActivityType activityType;
    
    /**
     * Horodatage de début
     */
    private Instant startTime;
    
    /**
     * Horodatage de fin (null si en cours)
     */
    private Instant endTime;
    
    /**
     * Score de confiance pour cette activité
     */
    private double confidence;
    
    /**
     * Calcule la durée de l'activité en secondes
     */
    public int getDurationSec() {
        if (endTime == null) {
            return (int) (Instant.now().getEpochSecond() - startTime.getEpochSecond());
        }
        return (int) (endTime.getEpochSecond() - startTime.getEpochSecond());
    }
    
    /**
     * Indique si l'activité est toujours en cours
     */
    public boolean isOngoing() {
        return endTime == null;
    }
}
