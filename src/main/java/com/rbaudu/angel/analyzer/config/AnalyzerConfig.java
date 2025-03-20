package com.rbaudu.angel.analyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration centralisée pour le module d'analyse d'activités.
 * Cette classe est automatiquement peuplée par Spring Boot à partir des propriétés
 * définies dans le fichier application.properties avec le préfixe "angel.analyzer".
 */
@Configuration
@ConfigurationProperties(prefix = "angel.analyzer")
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

    /**
     * Getter pour humanDetectionModel
     */
    public String getHumanDetectionModel() {
        return humanDetectionModel;
    }

    /**
     * Setter pour humanDetectionModel
     */
    public void setHumanDetectionModel(String humanDetectionModel) {
        this.humanDetectionModel = humanDetectionModel;
    }

    /**
     * Getter pour activityRecognitionModel
     */
    public String getActivityRecognitionModel() {
        return activityRecognitionModel;
    }

    /**
     * Setter pour activityRecognitionModel
     */
    public void setActivityRecognitionModel(String activityRecognitionModel) {
        this.activityRecognitionModel = activityRecognitionModel;
    }

    /**
     * Getter pour audioClassificationModel
     */
    public String getAudioClassificationModel() {
        return audioClassificationModel;
    }

    /**
     * Setter pour audioClassificationModel
     */
    public void setAudioClassificationModel(String audioClassificationModel) {
        this.audioClassificationModel = audioClassificationModel;
    }

    /**
     * Getter pour captureIntervalMs
     */
    public int getCaptureIntervalMs() {
        return captureIntervalMs;
    }

    /**
     * Setter pour captureIntervalMs
     */
    public void setCaptureIntervalMs(int captureIntervalMs) {
        this.captureIntervalMs = captureIntervalMs;
    }

    /**
     * Getter pour presenceThreshold
     */
    public double getPresenceThreshold() {
        return presenceThreshold;
    }

    /**
     * Setter pour presenceThreshold
     */
    public void setPresenceThreshold(double presenceThreshold) {
        this.presenceThreshold = presenceThreshold;
    }

    /**
     * Getter pour activityConfidenceThreshold
     */
    public double getActivityConfidenceThreshold() {
        return activityConfidenceThreshold;
    }

    /**
     * Setter pour activityConfidenceThreshold
     */
    public void setActivityConfidenceThreshold(double activityConfidenceThreshold) {
        this.activityConfidenceThreshold = activityConfidenceThreshold;
    }

    /**
     * Getter pour historySize
     */
    public int getHistorySize() {
        return historySize;
    }

    /**
     * Setter pour historySize
     */
    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    /**
     * Getter pour inputImageWidth
     */
    public int getInputImageWidth() {
        return inputImageWidth;
    }

    /**
     * Setter pour inputImageWidth
     */
    public void setInputImageWidth(int inputImageWidth) {
        this.inputImageWidth = inputImageWidth;
    }

    /**
     * Getter pour inputImageHeight
     */
    public int getInputImageHeight() {
        return inputImageHeight;
    }

    /**
     * Setter pour inputImageHeight
     */
    public void setInputImageHeight(int inputImageHeight) {
        this.inputImageHeight = inputImageHeight;
    }

    /**
     * Getter pour audioSampleRate
     */
    public int getAudioSampleRate() {
        return audioSampleRate;
    }

    /**
     * Setter pour audioSampleRate
     */
    public void setAudioSampleRate(int audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }

    /**
     * Getter pour audioAnalysisEnabled
     */
    public boolean isAudioAnalysisEnabled() {
        return audioAnalysisEnabled;
    }

    /**
     * Setter pour audioAnalysisEnabled
     */
    public void setAudioAnalysisEnabled(boolean audioAnalysisEnabled) {
        this.audioAnalysisEnabled = audioAnalysisEnabled;
    }
}