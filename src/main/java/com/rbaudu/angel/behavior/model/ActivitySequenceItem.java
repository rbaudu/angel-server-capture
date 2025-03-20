package com.rbaudu.angel.behavior.model;

import com.rbaudu.angel.analyzer.model.ActivityType;
import java.time.Instant;
import java.util.Objects;

/**
 * Représente une activité dans une séquence avec son horodatage
 * et sa durée.
 */
public class ActivitySequenceItem {
    
    /**
     * Type d'activité
     */
    private ActivityType activityType;
    
    /**
     * Horodatage de début
     */
    private Instant startTime;
    
    /**
     * Horodatage de fin (null si en cours)
     */
    private Instant endTime;
    
    /**
     * Score de confiance pour cette activité
     */
    private double confidence;
    
    /**
     * Constructeur par défaut
     */
    public ActivitySequenceItem() {
    }

    /**
     * Constructeur avec tous les champs
     */
    public ActivitySequenceItem(ActivityType activityType, Instant startTime, 
                               Instant endTime, double confidence) {
        this.activityType = activityType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.confidence = confidence;
    }

    /**
     * Getters et Setters
     */
    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    /**
     * Calcule la durée de l'activité en secondes
     */
    public int getDurationSec() {
        if (endTime == null) {
            return (int) (Instant.now().getEpochSecond() - startTime.getEpochSecond());
        }
        return (int) (endTime.getEpochSecond() - startTime.getEpochSecond());
    }
    
    /**
     * Indique si l'activité est toujours en cours
     */
    public boolean isOngoing() {
        return endTime == null;
    }

    /**
     * Méthode equals pour la comparaison des objets
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivitySequenceItem that = (ActivitySequenceItem) o;
        return Double.compare(that.confidence, confidence) == 0 &&
                activityType == that.activityType &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    /**
     * Méthode hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(activityType, startTime, endTime, confidence);
    }

    /**
     * Méthode toString
     */
    @Override
    public String toString() {
        return "ActivitySequenceItem{" +
                "activityType=" + activityType +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", confidence=" + confidence +
                '}';
    }

    /**
     * Builder statique pour créer des instances de ActivitySequenceItem
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ActivityType activityType;
        private Instant startTime;
        private Instant endTime;
        private double confidence;

        public Builder activityType(ActivityType activityType) {
            this.activityType = activityType;
            return this;
        }

        public Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder confidence(double confidence) {
            this.confidence = confidence;
            return this;
        }

        public ActivitySequenceItem build() {
            return new ActivitySequenceItem(activityType, startTime, endTime, confidence);
        }
    }
}