package com.rbaudu.angel.analyzer.model;

/**
 * Enumération des types d'activités détectables par le système.
 */
public enum ActivityType {
    /**
     * Personne absente
     */
    ABSENT,
    
    /**
     * Personne présente mais inactive
     */
    PRESENT_INACTIVE,
    
    /**
     * Personne en train de dormir
     */
    SLEEPING,
    
    /**
     * Personne en train de manger
     */
    EATING,
    
    /**
     * Personne en train de lire
     */
    READING,
    
    /**
     * Personne en train de nettoyer
     */
    CLEANING,
    
    /**
     * Personne en train de regarder la télévision
     */
    WATCHING_TV,
    
    /**
     * Personne en train de téléphoner
     */
    CALLING,
    
    /**
     * Personne en train de tricoter
     */
    KNITTING,
    
    /**
     * Personne en train de parler avec quelqu'un
     */
    TALKING,
    
    /**
     * Personne en train de jouer
     */
    PLAYING,
    
    /**
     * Activité inconnue
     */
    UNKNOWN
}
