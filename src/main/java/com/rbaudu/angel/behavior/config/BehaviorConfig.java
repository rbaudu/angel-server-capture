package com.rbaudu.angel.behavior.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour le module de reconnaissance de comportement.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "angel.behavior")
public class BehaviorConfig {
    
    /**
     * Chemin vers le fichier de définitions des patterns de comportement
     */
    private String patternsDefinitionPath = "classpath:behavior/patterns.json";
    
    /**
     * Taille de la fenêtre temporelle pour l'analyse de comportement (en secondes)
     */
    private int timeWindowSec = 3600; // 1 heure par défaut
    
    /**
     * Seuil de confiance minimum pour considérer un comportement comme détecté
     */
    private double confidenceThreshold = 0.65;
    
    /**
     * Taille maximale de l'historique des comportements stockés
     */
    private int historySize = 100;
    
    /**
     * Intervalle minimum entre deux analyses de comportement (en ms)
     */
    private int analysisIntervalMs = 5000; // 5 secondes par défaut
    
    /**
     * Active/désactive l'analyse continue des comportements
     */
    private boolean continuousAnalysis = true;
    
    /**
     * Active/désactive la détection d'anomalies de comportement
     */
    private boolean anomalyDetectionEnabled = true;
    
    /**
     * Seuil pour la détection d'anomalies (écart par rapport à la référence)
     */
    private double anomalyThreshold = 2.0;
    
    /**
     * Stratégie de fusion des données pour l'analyse de comportement
     * (Options: WEIGHTED, BAYESIAN, RULE_BASED)
     */
    private String fusionStrategy = "WEIGHTED";
    
    /**
     * Nombre minimum d'activités à considérer pour une analyse de comportement
     */
    private int minActivitiesForAnalysis = 3;
}
