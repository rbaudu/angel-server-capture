package com.rbaudu.angel.analyzer.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Résultat d'une analyse d'activité.
 */
public class AnalysisResult {

    /**
     * Identifiant unique du résultat
     */
    private String id;
    
    /**
     * Horodatage de l'analyse
     */
    private Instant timestamp;
    
    /**
     * Type d'activité détecté
     */
    private ActivityType activityType;
    
    /**
     * Score de confiance (entre 0.0 et 1.0)
     */
    private double confidence;
    
    /**
     * Indique si une personne est présente
     */
    private boolean personPresent;

    /**
     * Constructeur par défaut
     */
    public AnalysisResult() {
    }

    /**
     * Constructeur avec tous les champs
     */
    public AnalysisResult(String id, Instant timestamp, ActivityType activityType, 
                         double confidence, boolean personPresent) {
        this.id = id;
        this.timestamp = timestamp;
        this.activityType = activityType;
        this.confidence = confidence;
        this.personPresent = personPresent;
    }

    /**
     * Getters et Setters
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public boolean isPersonPresent() {
        return personPresent;
    }

    public void setPersonPresent(boolean personPresent) {
        this.personPresent = personPresent;
    }

    /**
     * Factory method pour créer un résultat d'analyse sans personne présente
     */
    public static AnalysisResult personAbsent() {
        return AnalysisResult.builder()
                .id(UUID.randomUUID().toString())
                .activityType(ActivityType.ABSENT)
                .confidence(1.0)
                .personPresent(false)
                .timestamp(Instant.now())
                .build();
    }
    
    /**
     * Factory method pour créer un résultat d'analyse avec activité inconnue
     */
    public static AnalysisResult unknownActivity(double confidence) {
        return AnalysisResult.builder()
                .id(UUID.randomUUID().toString())
                .activityType(ActivityType.UNKNOWN)
                .confidence(confidence)
                .personPresent(true)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Méthode equals pour la comparaison des objets
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalysisResult that = (AnalysisResult) o;
        return Double.compare(that.confidence, confidence) == 0 &&
                personPresent == that.personPresent &&
                Objects.equals(id, that.id) &&
                Objects.equals(timestamp, that.timestamp) &&
                activityType == that.activityType;
    }

    /**
     * Méthode hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, activityType, confidence, personPresent);
    }

    /**
     * Méthode toString
     */
    @Override
    public String toString() {
        return "AnalysisResult{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", activityType=" + activityType +
                ", confidence=" + confidence +
                ", personPresent=" + personPresent +
                '}';
    }

    /**
     * Builder statique pour créer des instances de AnalysisResult
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Instant timestamp;
        private ActivityType activityType;
        private double confidence;
        private boolean personPresent;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder activityType(ActivityType activityType) {
            this.activityType = activityType;
            return this;
        }

        public Builder confidence(double confidence) {
            this.confidence = confidence;
            return this;
        }

        public Builder personPresent(boolean personPresent) {
            this.personPresent = personPresent;
            return this;
        }

        public AnalysisResult build() {
            return new AnalysisResult(id, timestamp, activityType, confidence, personPresent);
        }
    }
}