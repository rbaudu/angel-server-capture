package com.rbaudu.angel.behavior.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DTO pour transférer les résultats d'analyse de comportement via l'API REST.
 */
public class BehaviorResultDto {
    
    /**
     * Identifiant unique du résultat
     */
    private String id;
    
    /**
     * Horodatage de l'analyse au format ISO-8601
     */
    private String timestamp;
    
    /**
     * Horodatage de début du comportement observé au format ISO-8601
     */
    private String startTime;
    
    /**
     * Type de comportement principal détecté
     */
    private String behaviorType;
    
    /**
     * Score de confiance (entre 0.0 et 1.0)
     */
    private double confidence;
    
    /**
     * Patterns de comportement détectés avec leur score, simplifiés pour l'API
     */
    private Map<String, Double> detectedPatterns;
    
    /**
     * Durée observée du comportement (en secondes)
     */
    private int durationSec;
    
    /**
     * Indique si le comportement est toujours en cours
     */
    private boolean ongoing;

    /**
     * Constructeur par défaut
     */
    public BehaviorResultDto() {
    }

    /**
     * Constructeur avec tous les champs
     */
    public BehaviorResultDto(String id, String timestamp, String startTime, String behaviorType,
                            double confidence, Map<String, Double> detectedPatterns,
                            int durationSec, boolean ongoing) {
        this.id = id;
        this.timestamp = timestamp;
        this.startTime = startTime;
        this.behaviorType = behaviorType;
        this.confidence = confidence;
        this.detectedPatterns = detectedPatterns;
        this.durationSec = durationSec;
        this.ongoing = ongoing;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(String behaviorType) {
        this.behaviorType = behaviorType;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Map<String, Double> getDetectedPatterns() {
        return detectedPatterns;
    }

    public void setDetectedPatterns(Map<String, Double> detectedPatterns) {
        this.detectedPatterns = detectedPatterns;
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
    
    /**
     * Converti un BehaviorResult en BehaviorResultDto
     * @param result Le résultat à convertir
     * @return Le DTO correspondant
     */
    public static BehaviorResultDto fromResult(BehaviorResult result) {
        if (result == null) {
            return null;
        }
        
        BehaviorResultDto dto = new BehaviorResultDto();
        dto.setId(result.getId());
        dto.setTimestamp(result.getTimestamp() != null ? result.getTimestamp().toString() : null);
        dto.setStartTime(result.getStartTime() != null ? result.getStartTime().toString() : null);
        dto.setBehaviorType(result.getBehaviorType().name());
        dto.setConfidence(result.getConfidence());
        dto.setDurationSec(result.getDurationSec());
        dto.setOngoing(result.isOngoing());
        
        // Convertir la map complexe en map de noms et scores
        Map<String, Double> patternMap = new HashMap<>();
        if (result.getDetectedPatterns() != null) {
            patternMap = result.getDetectedPatterns().entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().getName(),
                            Map.Entry::getValue
                    ));
        }
        dto.setDetectedPatterns(patternMap);
        
        return dto;
    }

    /**
     * Méthode equals pour la comparaison des objets
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehaviorResultDto that = (BehaviorResultDto) o;
        return Double.compare(that.confidence, confidence) == 0 &&
                durationSec == that.durationSec &&
                ongoing == that.ongoing &&
                Objects.equals(id, that.id) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(behaviorType, that.behaviorType) &&
                Objects.equals(detectedPatterns, that.detectedPatterns);
    }

    /**
     * Méthode hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, startTime, behaviorType, confidence, detectedPatterns, durationSec, ongoing);
    }

    /**
     * Méthode toString
     */
    @Override
    public String toString() {
        return "BehaviorResultDto{" +
                "id='" + id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", startTime='" + startTime + '\'' +
                ", behaviorType='" + behaviorType + '\'' +
                ", confidence=" + confidence +
                ", detectedPatterns=" + detectedPatterns +
                ", durationSec=" + durationSec +
                ", ongoing=" + ongoing +
                '}';
    }

    /**
     * Builder statique pour créer des instances de BehaviorResultDto
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String timestamp;
        private String startTime;
        private String behaviorType;
        private double confidence;
        private Map<String, Double> detectedPatterns;
        private int durationSec;
        private boolean ongoing;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder startTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder behaviorType(String behaviorType) {
            this.behaviorType = behaviorType;
            return this;
        }

        public Builder confidence(double confidence) {
            this.confidence = confidence;
            return this;
        }

        public Builder detectedPatterns(Map<String, Double> detectedPatterns) {
            this.detectedPatterns = detectedPatterns;
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

        public BehaviorResultDto build() {
            return new BehaviorResultDto(id, timestamp, startTime, behaviorType, confidence,
                    detectedPatterns, durationSec, ongoing);
        }
    }
}