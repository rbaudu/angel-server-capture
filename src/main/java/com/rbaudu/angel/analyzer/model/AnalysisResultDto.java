package com.rbaudu.angel.analyzer.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * DTO pour les résultats d'analyse, utilisé pour la sérialisation/désérialisation JSON.
 */
public class AnalysisResultDto {
    
    /**
     * Date et heure de l'analyse
     */
    private LocalDateTime timestamp;
    
    /**
     * Type d'activité détectée
     */
    private ActivityType activityType;
    
    /**
     * Niveau de confiance de la détection (entre 0.0 et 1.0)
     */
    private double confidence;
    
    /**
     * Indique si une personne est présente dans la scène analysée
     */
    private boolean personPresent;
    
    /**
     * Constructeur par défaut
     */
    public AnalysisResultDto() {
    }
    
    /**
     * Constructeur avec tous les champs
     */
    public AnalysisResultDto(LocalDateTime timestamp, ActivityType activityType, 
                           double confidence, boolean personPresent) {
        this.timestamp = timestamp;
        this.activityType = activityType;
        this.confidence = confidence;
        this.personPresent = personPresent;
    }
    
    /**
     * Getter pour timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Setter pour timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Getter pour activityType
     */
    public ActivityType getActivityType() {
        return activityType;
    }
    
    /**
     * Setter pour activityType
     */
    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }
    
    /**
     * Getter pour confidence
     */
    public double getConfidence() {
        return confidence;
    }
    
    /**
     * Setter pour confidence
     */
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    /**
     * Getter pour personPresent
     */
    public boolean isPersonPresent() {
        return personPresent;
    }
    
    /**
     * Setter pour personPresent
     */
    public void setPersonPresent(boolean personPresent) {
        this.personPresent = personPresent;
    }
    
    /**
     * Méthode equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AnalysisResultDto that = (AnalysisResultDto) o;
        
        return Double.compare(that.confidence, confidence) == 0 &&
               personPresent == that.personPresent &&
               Objects.equals(timestamp, that.timestamp) &&
               activityType == that.activityType;
    }
    
    /**
     * Méthode hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(timestamp, activityType, confidence, personPresent);
    }
    
    /**
     * Méthode toString
     */
    @Override
    public String toString() {
        return "AnalysisResultDto{" +
               "timestamp=" + timestamp +
               ", activityType=" + activityType +
               ", confidence=" + confidence +
               ", personPresent=" + personPresent +
               '}';
    }
    
    /**
     * Pattern Builder pour créer des instances de AnalysisResultDto
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Convertit un AnalysisResult en DTO
     * 
     * @param result Le résultat d'analyse à convertir
     * @return Le DTO correspondant
     */
    public static AnalysisResultDto fromAnalysisResult(AnalysisResult result) {
        // Conversion de Instant à LocalDateTime
        LocalDateTime localDateTime = result.getTimestamp() != null 
            ? LocalDateTime.ofInstant(result.getTimestamp(), ZoneId.systemDefault()) 
            : null;
        
        return AnalysisResultDto.builder()
                .timestamp(localDateTime)
                .activityType(result.getActivityType())
                .confidence(result.getConfidence())
                .personPresent(result.isPersonPresent())
                .build();
    }
    
    /**
     * Classe Builder pour AnalysisResultDto
     */
    public static class Builder {
        private LocalDateTime timestamp;
        private ActivityType activityType;
        private double confidence;
        private boolean personPresent;
        
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        /**
         * Conversion d'Instant à LocalDateTime pour compatibilité
         */
        public Builder timestamp(Instant instant) {
            if (instant != null) {
                this.timestamp = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
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
        
        public AnalysisResultDto build() {
            return new AnalysisResultDto(timestamp, activityType, confidence, personPresent);
        }
    }
}