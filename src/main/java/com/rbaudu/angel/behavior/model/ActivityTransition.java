package com.rbaudu.angel.behavior.model;

import com.rbaudu.angel.analyzer.model.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente une transition entre deux activités avec sa probabilité associée.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTransition {
    
    /**
     * Activité de départ
     */
    private ActivityType fromActivity;
    
    /**
     * Activité d'arrivée
     */
    private ActivityType toActivity;
    
    /**
     * Probabilité de cette transition dans le pattern
     */
    private double probability;
    
    /**
     * Durée typique de cette transition (en secondes)
     */
    private int typicalDurationSec;
}