package com.rbaudu.angel.behavior.model;

import com.rbaudu.angel.analyzer.model.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Représente un motif de comportement défini par une séquence d'activités
 * et d'autres caractéristiques (durée, transitions, etc.).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorPattern {
    
    /**
     * Identifiant unique du pattern de comportement
     */
    private String id;
    
    /**
     * Type de comportement que ce pattern représente
     */
    private BehaviorType type;
    
    /**
     * Nom convivial du pattern (ex: "Petit-déjeuner", "Préparation au coucher")
     */
    private String name;
    
    /**
     * Description du pattern de comportement
     */
    private String description;
    
    /**
     * Séquence d'activités qui composent ce pattern de comportement
     */
    private List<ActivityType> activitySequence;
    
    /**
     * Durée minimale prévue pour ce pattern (en secondes)
     */
    private Integer minDurationSec;
    
    /**
     * Durée maximale prévue pour ce pattern (en secondes)
     */
    private Integer maxDurationSec;
    
    /**
     * Indique si l'ordre exact des activités doit être respecté
     */
    private boolean strictOrderRequired;
    
    /**
     * Score de référence pour ce pattern (utilisé pour la comparaison)
     */
    private double baselineScore;
    
    /**
     * Transitions typiques entre activités dans ce pattern
     * (utilisé pour des analyses plus avancées)
     */
    private List<ActivityTransition> transitions;
    
    /**
     * Heures de la journée typiques pour ce pattern (format 0-23)
     */
    private List<Integer> typicalHours;
}