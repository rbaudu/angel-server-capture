package com.rbaudu.angel.behavior.model;

import com.rbaudu.angel.analyzer.model.ActivityType;
import java.util.Objects;

/**
 * Représente une transition entre deux activités avec sa probabilité associée.
 */
public class ActivityTransition {
    
    /**
     * Activité de départ
     */
    private ActivityType fromActivity;
    
    /**
     * Activité d'arrivée
     */
    private ActivityType toActivity;
    
    /**
     * Probabilité de cette transition dans le pattern
     */
    private double probability;
    
    /**
     * Durée typique de cette transition (en secondes)
     */
    private int typicalDurationSec;

    /**
     * Constructeur par défaut
     */
    public ActivityTransition() {
    }

    /**
     * Constructeur avec tous les champs
     */
    public ActivityTransition(ActivityType fromActivity, ActivityType toActivity, 
                             double probability, int typicalDurationSec) {
        this.fromActivity = fromActivity;
        this.toActivity = toActivity;
        this.probability = probability;
        this.typicalDurationSec = typicalDurationSec;
    }

    /**
     * Getters et Setters
     */
    public ActivityType getFromActivity() {
        return fromActivity;
    }

    public void setFromActivity(ActivityType fromActivity) {
        this.fromActivity = fromActivity;
    }

    public ActivityType getToActivity() {
        return toActivity;
    }

    public void setToActivity(ActivityType toActivity) {
        this.toActivity = toActivity;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public int getTypicalDurationSec() {
        return typicalDurationSec;
    }

    public void setTypicalDurationSec(int typicalDurationSec) {
        this.typicalDurationSec = typicalDurationSec;
    }

    /**
     * Méthode equals pour la comparaison des objets
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityTransition that = (ActivityTransition) o;
        return Double.compare(that.probability, probability) == 0 &&
                typicalDurationSec == that.typicalDurationSec &&
                fromActivity == that.fromActivity &&
                toActivity == that.toActivity;
    }

    /**
     * Méthode hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(fromActivity, toActivity, probability, typicalDurationSec);
    }

    /**
     * Méthode toString
     */
    @Override
    public String toString() {
        return "ActivityTransition{" +
                "fromActivity=" + fromActivity +
                ", toActivity=" + toActivity +
                ", probability=" + probability +
                ", typicalDurationSec=" + typicalDurationSec +
                '}';
    }

    /**
     * Builder statique pour créer des instances de ActivityTransition
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ActivityType fromActivity;
        private ActivityType toActivity;
        private double probability;
        private int typicalDurationSec;

        public Builder fromActivity(ActivityType fromActivity) {
            this.fromActivity = fromActivity;
            return this;
        }

        public Builder toActivity(ActivityType toActivity) {
            this.toActivity = toActivity;
            return this;
        }

        public Builder probability(double probability) {
            this.probability = probability;
            return this;
        }

        public Builder typicalDurationSec(int typicalDurationSec) {
            this.typicalDurationSec = typicalDurationSec;
            return this;
        }

        public ActivityTransition build() {
            return new ActivityTransition(fromActivity, toActivity, probability, typicalDurationSec);
        }
    }
}