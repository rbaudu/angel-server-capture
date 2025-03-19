package com.rbaudu.angel.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Résultat d'une analyse d'activité.
 * Cette classe contient les informations sur l'activité détectée, la présence d'une personne,
 * le niveau de confiance de la détection et le timestamp de l'analyse.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
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
     * Méthode utilitaire pour créer rapidement un résultat indiquant l'absence de personne
     * @return Un objet AnalysisResult configuré pour indiquer l'absence
     */
    public static AnalysisResult personAbsent() {
        return AnalysisResult.builder()
                .timestamp(LocalDateTime.now())
                .activityType(ActivityType.ABSENT)
                .personPresent(false)
                .confidence(1.0)
                .build();
    }
    
    /**
     * Méthode utilitaire pour créer rapidement un résultat avec un type d'activité inconnu
     * @return Un objet AnalysisResult configuré avec une activité inconnue
     */
    public static AnalysisResult unknownActivity() {
        return AnalysisResult.builder()
                .timestamp(LocalDateTime.now())
                .activityType(ActivityType.UNKNOWN)
                .personPresent(true)
                .confidence(0.5)
                .build();
    }
}
