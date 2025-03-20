package com.rbaudu.angel.behavior.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Résultat d'une analyse de comportement.
 */
public class BehaviorResult {
    
    /**
     * Identifiant unique du résultat
     */
    private String id;
    
    /**
     * Horodatage de l'analyse
     */
    private Instant timestamp;
    
    /**
     * Horodatage de début du comportement observé
     */
    private Instant startTime;
    
    /**
     * Type de comportement principal détecté
     */
    private BehaviorType behaviorType;
    
    /**
     * Score de confiance (entre 0.0 et 1.0)
     */
    private double confidence;
    
    /**
     * Patterns de comportement détectés avec leur score
     */
    private Map<BehaviorPattern, Double> detectedPatterns;
    
    /**
     * Historique des activités qui ont mené à cette analyse
     */
    private List<ActivitySequenceItem> activityHistory;
    
    /**
     * Durée observée du comportement (en secondes)
     */
    private int durationSec;
    
    /**
     * Indique si le comportement est toujours en cours
     */
    private boolean ongoing;
    
    /**
     * Facteurs contribuant à cette détection
     */
    private Map<String, Double> contributingFactors;
    
    /**
     * Constructeur par défaut
     */
    public BehaviorResult() {
    }

    /**
     * Constructeur avec tous les champs
     */
    public BehaviorResult(String id, Instant timestamp, Instant startTime, BehaviorType behaviorType,
                         double confidence, Map<BehaviorPattern, Double> detectedPatterns,
                         List<ActivitySequenceItem> activityHistory, int durationSec, boolean ongoing,
                         Map<String, Double> contributingFactors) {
        this.id = id;
        this.timestamp = timestamp;
        this.startTime = startTime;
        this.behaviorType = behaviorType;
        this.confidence = confidence;
        this.detectedPatterns = detectedPatterns;
        this.activityHistory = activityHistory;
        this.durationSec = durationSec;
        this.ongoing = ongoing;
        this.contributingFactors = contributingFactors;
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

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public BehaviorType getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(BehaviorType behaviorType) {
        this.behaviorType = behaviorType;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Map<BehaviorPattern, Double> getDetectedPatterns() {
        return detectedPatterns;
    }

    public void setDetectedPatterns(Map<BehaviorPattern, Double> detectedPatterns) {
        this.detectedPatterns = detectedPatterns;
    }

    public List<ActivitySequenceItem> getActivityHistory() {
        return activityHistory;
    }

    public void setActivityHistory(List<ActivitySequenceItem> activityHistory) {
        this.activityHistory = activityHistory;
    }

    public int getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(int durationSec) {
        this.durationSec = durationSec;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }

    public Map<String, Double> getContributingFactors() {
        return contributingFactors;
    }

    public void setContributingFactors(Map<String, Double> contributingFactors) {
        this.contributingFactors = contributingFactors;
    }

    /**
     * Factory method pour créer un résultat de comportement inconnu
     */
    public static BehaviorResult unknownBehavior() {
        return BehaviorResult.builder()
                .id(UUID.randomUUID().toString())
                .behaviorType(BehaviorType.UNKNOWN)
                .confidence(0.0)
                .timestamp(Instant.now())
                .build();
    }
    
    /**
     * Factory method pour créer un résultat de comportement normal
     */
    public static BehaviorResult normalBehavior(double confidence) {
        return BehaviorResult.builder()
                .id(UUID.randomUUID().toString())
                .behaviorType(BehaviorType.NORMAL)
                .confidence(confidence)
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
        BehaviorResult that = (BehaviorResult) o;
        return Double.compare(that.confidence, confidence) == 0 &&
                durationSec == that.durationSec &&
                ongoing == that.ongoing &&
                Objects.equals(id, that.id) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(startTime, that.startTime) &&
                behaviorType == that.behaviorType &&
                Objects.equals(detectedPatterns, that.detectedPatterns) &&
                Objects.equals(activityHistory, that.activityHistory) &&
                Objects.equals(contributingFactors, that.contributingFactors);
    }

    /**
     * Méthode hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, startTime, behaviorType, confidence, 
                detectedPatterns, activityHistory, durationSec, ongoing, contributingFactors);
    }

    /**
     * Méthode toString
     */
    @Override
    public String toString() {
        return "BehaviorResult{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", startTime=" + startTime +
                ", behaviorType=" + behaviorType +
                ", confidence=" + confidence +
                ", detectedPatterns=" + detectedPatterns +
                ", activityHistory=" + activityHistory +
                ", durationSec=" + durationSec +
                ", ongoing=" + ongoing +
                ", contributingFactors=" + contributingFactors +
                '}';
    }

    /**
     * Builder statique pour créer des instances de BehaviorResult
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Instant timestamp;
        private Instant startTime;
        private BehaviorType behaviorType;
        private double confidence;
        private Map<BehaviorPattern, Double> detectedPatterns;
        private List<ActivitySequenceItem> activityHistory;
        private int durationSec;
        private boolean ongoing;
        private Map<String, Double> contributingFactors;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder behaviorType(BehaviorType behaviorType) {
            this.behaviorType = behaviorType;
            return this;
        }

        public Builder confidence(double confidence) {
            this.confidence = confidence;
            return this;
        }

        public Builder detectedPatterns(Map<BehaviorPattern, Double> detectedPatterns) {
            this.detectedPatterns = detectedPatterns;
            return this;
        }

        public Builder activityHistory(List<ActivitySequenceItem> activityHistory) {
            this.activityHistory = activityHistory;
            return this;
        }

        public Builder durationSec(int durationSec) {
            this.durationSec = durationSec;
            return this;
        }

        public Builder ongoing(boolean ongoing) {
            this.ongoing = ongoing;
            return this;
        }

        public Builder contributingFactors(Map<String, Double> contributingFactors) {
            this.contributingFactors = contributingFactors;
            return this;
        }

        public BehaviorResult build() {
            return new BehaviorResult(id, timestamp, startTime, behaviorType, confidence,
                    detectedPatterns, activityHistory, durationSec, ongoing, contributingFactors);
        }
    }
}