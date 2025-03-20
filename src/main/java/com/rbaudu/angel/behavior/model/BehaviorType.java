package com.rbaudu.angel.behavior.model;

/**
 * Énumération des types de comportements détectables par le système.
 * Ces comportements sont inférés à partir des séquences d'activités observées.
 */
public enum BehaviorType {
    /**
     * Comportement normal/standard
     */
    NORMAL,
    
    /**
     * Comportement inhabituel ou anormal
     */
    UNUSUAL,
    
    /**
     * Comportement indiquant une routine matinale
     */
    MORNING_ROUTINE,
    
    /**
     * Comportement indiquant une routine du soir
     */
    EVENING_ROUTINE,
    
    /**
     * Comportement alimentaire
     */
    EATING_PATTERN,
    
    /**
     * Comportement de repos
     */
    RESTING_PATTERN,
    
    /**
     * Comportement de loisir
     */
    LEISURE_PATTERN,
    
    /**
     * Comportement social (interaction avec d'autres personnes)
     */
    SOCIAL_PATTERN,
    
    /**
     * Comportement indiquant des tâches ménagères
     */
    HOUSEKEEPING_PATTERN,
    
    /**
     * Comportement agité ou nerveux
     */
    AGITATED,
    
    /**
     * Comportement calme
     */
    CALM,
    
    /**
     * Comportement non identifié
     */
    UNKNOWN
}