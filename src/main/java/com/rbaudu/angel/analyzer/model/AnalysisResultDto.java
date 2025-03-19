package com.rbaudu.angel.analyzer.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les résultats d'analyse, utilisé pour la sérialisation/désérialisation JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * Convertit un AnalysisResult en DTO
     * 
     * @param result Le résultat d'analyse à convertir
     * @return Le DTO correspondant
     */
    public static AnalysisResultDto fromAnalysisResult(AnalysisResult result) {
        return AnalysisResultDto.builder()
                .timestamp(result.getTimestamp())
                .activityType(result.getActivityType())
                .confidence(result.getConfidence())
                .personPresent(result.isPersonPresent())
                .build();
    }
}
