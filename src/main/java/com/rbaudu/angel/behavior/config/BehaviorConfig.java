package com.rbaudu.angel.behavior.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * Configuration pour le module de reconnaissance de comportement.
 */
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

    /**
     * Constructeur par défaut
     */
    public BehaviorConfig() {
    }

    /**
     * Getters et Setters
     */
    public String getPatternsDefinitionPath() {
        return patternsDefinitionPath;
    }

    public void setPatternsDefinitionPath(String patternsDefinitionPath) {
        this.patternsDefinitionPath = patternsDefinitionPath;
    }

    public int getTimeWindowSec() {
        return timeWindowSec;
    }

    public void setTimeWindowSec(int timeWindowSec) {
        this.timeWindowSec = timeWindowSec;
    }

    public double getConfidenceThreshold() {
        return confidenceThreshold;
    }

    public void setConfidenceThreshold(double confidenceThreshold) {
        this.confidenceThreshold = confidenceThreshold;
    }

    public int getHistorySize() {
        return historySize;
    }

    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    public int getAnalysisIntervalMs() {
        return analysisIntervalMs;
    }

    public void setAnalysisIntervalMs(int analysisIntervalMs) {
        this.analysisIntervalMs = analysisIntervalMs;
    }

    public boolean isContinuousAnalysis() {
        return continuousAnalysis;
    }

    public void setContinuousAnalysis(boolean continuousAnalysis) {
        this.continuousAnalysis = continuousAnalysis;
    }

    public boolean isAnomalyDetectionEnabled() {
        return anomalyDetectionEnabled;
    }

    public void setAnomalyDetectionEnabled(boolean anomalyDetectionEnabled) {
        this.anomalyDetectionEnabled = anomalyDetectionEnabled;
    }

    public double getAnomalyThreshold() {
        return anomalyThreshold;
    }

    public void setAnomalyThreshold(double anomalyThreshold) {
        this.anomalyThreshold = anomalyThreshold;
    }

    public String getFusionStrategy() {
        return fusionStrategy;
    }

    public void setFusionStrategy(String fusionStrategy) {
        this.fusionStrategy = fusionStrategy;
    }

    public int getMinActivitiesForAnalysis() {
        return minActivitiesForAnalysis;
    }

    public void setMinActivitiesForAnalysis(int minActivitiesForAnalysis) {
        this.minActivitiesForAnalysis = minActivitiesForAnalysis;
    }

    /**
     * Méthode equals pour la comparaison des objets
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehaviorConfig that = (BehaviorConfig) o;
        return timeWindowSec == that.timeWindowSec &&
                Double.compare(that.confidenceThreshold, confidenceThreshold) == 0 &&
                historySize == that.historySize &&
                analysisIntervalMs == that.analysisIntervalMs &&
                continuousAnalysis == that.continuousAnalysis &&
                anomalyDetectionEnabled == that.anomalyDetectionEnabled &&
                Double.compare(that.anomalyThreshold, anomalyThreshold) == 0 &&
                minActivitiesForAnalysis == that.minActivitiesForAnalysis &&
                Objects.equals(patternsDefinitionPath, that.patternsDefinitionPath) &&
                Objects.equals(fusionStrategy, that.fusionStrategy);
    }

    /**
     * Méthode hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(patternsDefinitionPath, timeWindowSec, confidenceThreshold, historySize,
                analysisIntervalMs, continuousAnalysis, anomalyDetectionEnabled, anomalyThreshold,
                fusionStrategy, minActivitiesForAnalysis);
    }

    /**
     * Méthode toString
     */
    @Override
    public String toString() {
        return "BehaviorConfig{" +
                "patternsDefinitionPath='" + patternsDefinitionPath + '\'' +
                ", timeWindowSec=" + timeWindowSec +
                ", confidenceThreshold=" + confidenceThreshold +
                ", historySize=" + historySize +
                ", analysisIntervalMs=" + analysisIntervalMs +
                ", continuousAnalysis=" + continuousAnalysis +
                ", anomalyDetectionEnabled=" + anomalyDetectionEnabled +
                ", anomalyThreshold=" + anomalyThreshold +
                ", fusionStrategy='" + fusionStrategy + '\'' +
                ", minActivitiesForAnalysis=" + minActivitiesForAnalysis +
                '}';
    }
}