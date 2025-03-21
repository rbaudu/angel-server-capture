package com.rbaudu.angel.analyzer.service.fusion;

import com.rbaudu.angel.analyzer.config.AnalyzerConfig;
import com.rbaudu.angel.analyzer.model.ActivityType;
import com.rbaudu.angel.analyzer.model.AnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * Service responsable de la fusion des résultats d'analyse audio et vidéo.
 */
@Service
public class MultimodalFusion {
    private static final Logger logger = LoggerFactory.getLogger(MultimodalFusion.class);
    
    private final AnalyzerConfig config;
    
    // Historique des résultats pour le lissage temporel
    private final List<AnalysisResult> resultsHistory = new ArrayList<>();
    
    /**
     * Constructeur avec injection de dépendances.
     * @param config Configuration de l'analyseur
     */
    public MultimodalFusion(AnalyzerConfig config) {
        this.config = config;
    }
    
    /**
     * Fusionne les résultats d'analyse audio et vidéo en un résultat final.
     * @param videoResults Résultats de l'analyse vidéo
     * @param audioResults Résultats de l'analyse audio
     * @return Résultat fusionné
     */
    public AnalysisResult fuseResults(Map<ActivityType, Double> videoResults, 
                                     Map<ActivityType, Double> audioResults) {
        
        // Fusion des scores selon les règles spécifiques
        Map<ActivityType, Double> fusedScores = computeFusedScores(videoResults, audioResults);
        
        // Sélection de l'activité la plus probable
        ActivityType bestActivity = ActivityType.UNKNOWN;
        double maxScore = config.getActivityConfidenceThreshold();
        
        for (Map.Entry<ActivityType, Double> entry : fusedScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                bestActivity = entry.getKey();
            }
        }
        
        // Création du résultat final
        AnalysisResult result = AnalysisResult.builder()
                .timestamp(Instant.now())  // Utilise Instant au lieu de LocalDateTime
                .activityType(bestActivity)
                .confidence(maxScore)
                .personPresent(bestActivity != ActivityType.ABSENT)
                .build();
        
        // Application du lissage temporel
        result = applyTemporalSmoothing(result);
        
        logger.debug("Résultat fusionné: {}", result);
        return result;
    }
    
    /**
     * Calcule les scores fusionnés en combinant les résultats audio et vidéo.
     * @param videoScores Scores de l'analyse vidéo
     * @param audioScores Scores de l'analyse audio
     * @return Map des scores fusionnés
     */
    private Map<ActivityType, Double> computeFusedScores(
            Map<ActivityType, Double> videoScores, 
            Map<ActivityType, Double> audioScores) {
        
        // Création d'une copie des scores vidéo comme base
        Map<ActivityType, Double> fusedScores = new HashMap<>(videoScores);
        
        // Pour chaque activité présente dans les résultats audio
        for (Map.Entry<ActivityType, Double> audioEntry : audioScores.entrySet()) {
            ActivityType activity = audioEntry.getKey();
            Double audioScore = audioEntry.getValue();
            
            // Si l'activité est aussi présente dans les résultats vidéo
            if (videoScores.containsKey(activity)) {
                Double videoScore = videoScores.get(activity);
                
                // Fusion avec poids différents selon l'activité
                double fusedScore;
                
                switch (activity) {
                    case TALKING:
                    case CALLING:
                        // Pour la parole, l'audio est plus important
                        fusedScore = audioScore * 0.7 + videoScore * 0.3;
                        break;
                        
                    case SLEEPING:
                    case READING:
                    case KNITTING:
                        // Pour ces activités, la vidéo est plus importante
                        fusedScore = audioScore * 0.2 + videoScore * 0.8;
                        break;
                        
                    case WATCHING_TV:
                    case EATING:
                        // Mix équilibré
                        fusedScore = audioScore * 0.5 + videoScore * 0.5;
                        break;
                        
                    default:
                        // Par défaut, moyenne simple
                        fusedScore = (audioScore + videoScore) / 2.0;
                        break;
                }
                
                fusedScores.put(activity, fusedScore);
            } else {
                // Si l'activité n'est pas dans les résultats vidéo, l'ajouter avec un score réduit
                fusedScores.put(activity, audioScore * 0.6);
            }
        }
        
        return fusedScores;
    }
    
    /**
     * Applique un lissage temporel pour éviter les changements brusques d'activité.
     * @param currentResult Résultat actuel
     * @return Résultat lissé
     */
    private AnalysisResult applyTemporalSmoothing(AnalysisResult currentResult) {
        // Ajouter le résultat courant à l'historique
        resultsHistory.add(currentResult);
        
        // Limiter la taille de l'historique
        while (resultsHistory.size() > config.getHistorySize()) {
            resultsHistory.remove(0);
        }
        
        // Si l'historique est trop petit, retourner le résultat actuel
        if (resultsHistory.size() < 3) {
            return currentResult;
        }
        
        // Comptage des activités dans l'historique récent
        Map<ActivityType, Integer> activityCounts = new HashMap<>();
        Map<ActivityType, Double> confidenceSum = new HashMap<>();
        
        for (AnalysisResult result : resultsHistory) {
            ActivityType activity = result.getActivityType();
            activityCounts.put(activity, activityCounts.getOrDefault(activity, 0) + 1);
            confidenceSum.put(activity, confidenceSum.getOrDefault(activity, 0.0) + result.getConfidence());
        }
        
        // Trouver l'activité la plus fréquente
        ActivityType mostFrequentActivity = null;
        int maxCount = 0;
        
        for (Map.Entry<ActivityType, Integer> entry : activityCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentActivity = entry.getKey();
            }
        }
        
        // Si l'activité courante est différente de la plus fréquente
        if (mostFrequentActivity != null && 
            mostFrequentActivity != currentResult.getActivityType() &&
            maxCount > resultsHistory.size() / 2) {
            
            // Calculer la confiance moyenne pour l'activité la plus fréquente
            double avgConfidence = confidenceSum.get(mostFrequentActivity) / maxCount;
            
            // Créer un nouveau résultat avec l'activité la plus fréquente
            return AnalysisResult.builder()
                    .timestamp(currentResult.getTimestamp())
                    .activityType(mostFrequentActivity)
                    .confidence(avgConfidence)
                    .personPresent(mostFrequentActivity != ActivityType.ABSENT)
                    .build();
        }
        
        // Sinon, garder le résultat actuel
        return currentResult;
    }
}