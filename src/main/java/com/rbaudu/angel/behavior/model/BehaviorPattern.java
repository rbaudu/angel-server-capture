package com.rbaudu.angel.behavior.model;

import com.rbaudu.angel.analyzer.model.ActivityType;

import java.util.List;
import java.util.Objects;

/**
 * Représente un motif de comportement défini par une séquence d'activités
 * et d'autres caractéristiques (durée, transitions, etc.).
 */
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

    /**
     * Constructeur par défaut
     */
    public BehaviorPattern() {
    }

    /**
     * Constructeur avec tous les champs
     */
    public BehaviorPattern(String id, BehaviorType type, String name, String description, 
                          List<ActivityType> activitySequence, Integer minDurationSec, Integer maxDurationSec, 
                          boolean strictOrderRequired, double baselineScore, 
                          List<ActivityTransition> transitions, List<Integer> typicalHours) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.activitySequence = activitySequence;
        this.minDurationSec = minDurationSec;
        this.maxDurationSec = maxDurationSec;
        this.strictOrderRequired = strictOrderRequired;
        this.baselineScore = baselineScore;
        this.transitions = transitions;
        this.typicalHours = typicalHours;
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

    public BehaviorType getType() {
        return type;
    }

    public void setType(BehaviorType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ActivityType> getActivitySequence() {
        return activitySequence;
    }

    public void setActivitySequence(List<ActivityType> activitySequence) {
        this.activitySequence = activitySequence;
    }

    public Integer getMinDurationSec() {
        return minDurationSec;
    }

    public void setMinDurationSec(Integer minDurationSec) {
        this.minDurationSec = minDurationSec;
    }

    public Integer getMaxDurationSec() {
        return maxDurationSec;
    }

    public void setMaxDurationSec(Integer maxDurationSec) {
        this.maxDurationSec = maxDurationSec;
    }

    public boolean isStrictOrderRequired() {
        return strictOrderRequired;
    }

    public void setStrictOrderRequired(boolean strictOrderRequired) {
        this.strictOrderRequired = strictOrderRequired;
    }

    public double getBaselineScore() {
        return baselineScore;
    }

    public void setBaselineScore(double baselineScore) {
        this.baselineScore = baselineScore;
    }

    public List<ActivityTransition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<ActivityTransition> transitions) {
        this.transitions = transitions;
    }

    public List<Integer> getTypicalHours() {
        return typicalHours;
    }

    public void setTypicalHours(List<Integer> typicalHours) {
        this.typicalHours = typicalHours;
    }

    /**
     * Méthode equals pour la comparaison des objets
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehaviorPattern that = (BehaviorPattern) o;
        return strictOrderRequired == that.strictOrderRequired &&
                Double.compare(that.baselineScore, baselineScore) == 0 &&
                Objects.equals(id, that.id) &&
                type == that.type &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(activitySequence, that.activitySequence) &&
                Objects.equals(minDurationSec, that.minDurationSec) &&
                Objects.equals(maxDurationSec, that.maxDurationSec) &&
                Objects.equals(transitions, that.transitions) &&
                Objects.equals(typicalHours, that.typicalHours);
    }

    /**
     * Méthode hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, description, activitySequence, minDurationSec,
                maxDurationSec, strictOrderRequired, baselineScore, transitions, typicalHours);
    }

    /**
     * Méthode toString
     */
    @Override
    public String toString() {
        return "BehaviorPattern{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", activitySequence=" + activitySequence +
                ", minDurationSec=" + minDurationSec +
                ", maxDurationSec=" + maxDurationSec +
                ", strictOrderRequired=" + strictOrderRequired +
                ", baselineScore=" + baselineScore +
                ", transitions=" + transitions +
                ", typicalHours=" + typicalHours +
                '}';
    }

    /**
     * Builder statique pour créer des instances de BehaviorPattern
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private BehaviorType type;
        private String name;
        private String description;
        private List<ActivityType> activitySequence;
        private Integer minDurationSec;
        private Integer maxDurationSec;
        private boolean strictOrderRequired;
        private double baselineScore;
        private List<ActivityTransition> transitions;
        private List<Integer> typicalHours;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder type(BehaviorType type) {
            this.type = type;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder activitySequence(List<ActivityType> activitySequence) {
            this.activitySequence = activitySequence;
            return this;
        }

        public Builder minDurationSec(Integer minDurationSec) {
            this.minDurationSec = minDurationSec;
            return this;
        }

        public Builder maxDurationSec(Integer maxDurationSec) {
            this.maxDurationSec = maxDurationSec;
            return this;
        }

        public Builder strictOrderRequired(boolean strictOrderRequired) {
            this.strictOrderRequired = strictOrderRequired;
            return this;
        }

        public Builder baselineScore(double baselineScore) {
            this.baselineScore = baselineScore;
            return this;
        }

        public Builder transitions(List<ActivityTransition> transitions) {
            this.transitions = transitions;
            return this;
        }

        public Builder typicalHours(List<Integer> typicalHours) {
            this.typicalHours = typicalHours;
            return this;
        }

        public BehaviorPattern build() {
            return new BehaviorPattern(id, type, name, description, activitySequence, minDurationSec,
                    maxDurationSec, strictOrderRequired, baselineScore, transitions, typicalHours);
        }
    }
}