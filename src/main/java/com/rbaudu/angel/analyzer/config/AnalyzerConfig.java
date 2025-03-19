package com.rbaudu.angel.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration centralisée pour le module d'analyse d'activités.
 * Cette classe est automatiquement peuplée par Spring Boot à partir des propriétés
 * définies dans le fichier application.properties avec le préfixe "angel.analyzer".
 */
@Configuration
@ConfigurationProperties(prefix = "angel.analyzer")
@Data
public class AnalyzerConfig {
    /**
     * Chemin vers le modèle de détection de présence humaine
     */
    private String humanDetectionModel;
    
    /**
     * Chemin vers le modèle de reconnaissance d'activités
     */
    private String activityRecognitionModel;
    
    /**
     * Chemin vers le modèle de classification audio
     */
    private String audioClassificationModel;
    
    /**
     * Intervalle de capture en millisecondes
     */
    private int captureIntervalMs = 1000;
    
    /**
     * Seuil de confiance pour la détection de présence (entre 0.0 et 1.0)
     */
    private double presenceThreshold = 0.5;
    
    /**
     * Seuil de confiance pour la classification d'activités (entre 0.0 et 1.0)
     */
    private double activityConfidenceThreshold = 0.6;
    
    /**
     * Taille de l'historique pour le lissage temporel des résultats
     */
    private int historySize = 5;
    
    /**
     * Largeur de l'image en entrée pour les modèles
     */
    private int inputImageWidth = 224;
    
    /**
     * Hauteur de l'image en entrée pour les modèles
     */
    private int inputImageHeight = 224;
    
    /**
     * Taux d'échantillonnage audio (Hz)
     */
    private int audioSampleRate = 44100;
    
    /**
     * Activer/désactiver l'analyse audio
     */
    private boolean audioAnalysisEnabled = true;
}
