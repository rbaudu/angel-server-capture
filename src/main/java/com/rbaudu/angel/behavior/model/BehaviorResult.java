package com.rbaudu.angel.behavior.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Résultat d'une analyse de comportement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorResult {
    
    /**
     * Identifiant unique du résultat
     */
    private String id;
    
    /**
     * Horodatage de l'analyse
     */
    private Instant timestamp;
    
    /**
     * Horodatage de début du comportement observé
     */
    private Instant startTime;
    
    /**
     * Type de comportement principal détecté
     */
    private BehaviorType behaviorType;
    
    /**
     * Score de confiance (entre 0.0 et 1.0)
     */
    private double confidence;
    
    /**
     * Patterns de comportement détectés avec leur score
     */
    private Map<BehaviorPattern, Double> detectedPatterns;
    
    /**
     * Historique des activités qui ont mené à cette analyse
     */
    private List<ActivitySequenceItem> activityHistory;
    
    /**
     * Durée observée du comportement (en secondes)
     */
    private int durationSec;
    
    /**
     * Indique si le comportement est toujours en cours
     */
    private boolean ongoing;
    
    /**
     * Facteurs contribuant à cette détection
     */
    private Map<String, Double> contributingFactors;
    
    /**
     * Factory method pour créer un résultat de comportement inconnu
     */
    public static BehaviorResult unknownBehavior() {
        return BehaviorResult.builder()
                .behaviorType(BehaviorType.UNKNOWN)
                .confidence(0.0)
                .timestamp(Instant.now())
                .build();
    }
    
    /**
     * Factory method pour créer un résultat de comportement normal
     */
    public static BehaviorResult normalBehavior(double confidence) {
        return BehaviorResult.builder()
                .behaviorType(BehaviorType.NORMAL)
                .confidence(confidence)
                .timestamp(Instant.now())
                .build();
    }
}
