package com.rbaudu.angel.behavior.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * DTO pour transférer les résultats d'analyse de comportement via l'API REST.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * Converti un BehaviorResult en BehaviorResultDto
     * @param result Le résultat à convertir
     * @return Le DTO correspondant
     */
    public static BehaviorResultDto fromResult(BehaviorResult result) {
        BehaviorResultDto dto = new BehaviorResultDto();
        dto.setId(result.getId());
        dto.setTimestamp(result.getTimestamp().toString());
        dto.setStartTime(result.getStartTime() != null ? result.getStartTime().toString() : null);
        dto.setBehaviorType(result.getBehaviorType().name());
        dto.setConfidence(result.getConfidence());
        dto.setDurationSec(result.getDurationSec());
        dto.setOngoing(result.isOngoing());
        
        // Convertir la map complexe en map de noms et scores
        Map<String, Double> patternMap = result.getDetectedPatterns().entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        Map.Entry::getValue
                ));
        dto.setDetectedPatterns(patternMap);
        
        return dto;
    }
}
