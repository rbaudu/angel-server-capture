package com.rbaudu.angel.behavior.service;

import com.rbaudu.angel.analyzer.model.ActivityType;
import com.rbaudu.angel.behavior.config.BehaviorConfig;
import com.rbaudu.angel.behavior.model.ActivitySequenceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsable de la détection d'anomalies dans les comportements.
 * Détecte les patterns inhabituels ou anormaux qui ne correspondent pas aux comportements attendus.
 */
@Service
public class AnomalyDetector {
    private static final Logger logger = LoggerFactory.getLogger(AnomalyDetector.class);
    
    private final BehaviorConfig config;
    
    // Statistiques des activités normales par heure de la journée
    private final Map<Integer, Map<ActivityType, Double>> activityStatsByHour = new HashMap<>();
    
    // Durées typiques des activités (en secondes)
    private final Map<ActivityType, List<Integer>> activityDurations = new HashMap<>();
    
    /**
     * Constructeur avec injection de dépendances.
     */
    public AnomalyDetector(BehaviorConfig config) {
        this.config = config;
        initializeBaselineStats();
    }
    
    /**
     * Initialise les statistiques de référence pour la détection d'anomalies.
     */
    private void initializeBaselineStats() {
        // Créer les statistiques par défaut pour chaque heure de la journée
        for (int hour = 0; hour < 24; hour++) {
            Map<ActivityType, Double> hourStats = new HashMap<>();
            
            // Initialiser avec des probabilités par défaut basées sur le moment de la journée
            if (hour >= 22 || hour < 6) {
                // Nuit (22h-6h)
                hourStats.put(ActivityType.SLEEPING, 0.7);
                hourStats.put(ActivityType.PRESENT_INACTIVE, 0.1);
                hourStats.put(ActivityType.READING, 0.1);
                hourStats.put(ActivityType.WATCHING_TV, 0.05);
                hourStats.put(ActivityType.EATING, 0.05);
            } else if (hour >= 6 && hour < 10) {
                // Matin (6h-10h)
                hourStats.put(ActivityType.EATING, 0.4);
                hourStats.put(ActivityType.PRESENT_INACTIVE, 0.2);
                hourStats.put(ActivityType.CLEANING, 0.1);
                hourStats.put(ActivityType.READING, 0.1);
                hourStats.put(ActivityType.SLEEPING, 0.2);
            } else if (hour >= 10 && hour < 12) {
                // Fin de matinée (10h-12h)
                hourStats.put(ActivityType.CLEANING, 0.3);
                hourStats.put(ActivityType.READING, 0.3);
                hourStats.put(ActivityType.PRESENT_INACTIVE, 0.2);
                hourStats.put(ActivityType.WATCHING_TV, 0.1);
                hourStats.put(ActivityType.KNITTING, 0.1);
            } else if (hour >= 12 && hour < 14) {
                // Midi (12h-14h)
                hourStats.put(ActivityType.EATING, 0.5);
                hourStats.put(ActivityType.WATCHING_TV, 0.2);
                hourStats.put(ActivityType.PRESENT_INACTIVE, 0.2);
                hourStats.put(ActivityType.TALKING, 0.1);
            } else if (hour >= 14 && hour < 18) {
                // Après-midi (14h-18h)
                hourStats.put(ActivityType.WATCHING_TV, 0.3);
                hourStats.put(ActivityType.READING, 0.2);
                hourStats.put(ActivityType.PRESENT_INACTIVE, 0.2);
                hourStats.put(ActivityType.KNITTING, 0.1);
                hourStats.put(ActivityType.CLEANING, 0.1);
                hourStats.put(ActivityType.TALKING, 0.1);
            } else if (hour >= 18 && hour < 22) {
                // Soirée (18h-22h)
                hourStats.put(ActivityType.EATING, 0.3);
                hourStats.put(ActivityType.WATCHING_TV, 0.3);
                hourStats.put(ActivityType.PRESENT_INACTIVE, 0.1);
                hourStats.put(ActivityType.TALKING, 0.1);
                hourStats.put(ActivityType.READING, 0.1);
                hourStats.put(ActivityType.CALLING, 0.1);
            }
            
            activityStatsByHour.put(hour, hourStats);
        }
        
        // Initialiser les durées typiques des activités
        initializeActivityDurations();
    }
    
    /**
     * Initialise les durées typiques des activités.
     */
    private void initializeActivityDurations() {
        activityDurations.put(ActivityType.SLEEPING, Arrays.asList(3600, 28800)); // 1h-8h
        activityDurations.put(ActivityType.EATING, Arrays.asList(900, 3600)); // 15min-1h
        activityDurations.put(ActivityType.READING, Arrays.asList(600, 7200)); // 10min-2h
        activityDurations.put(ActivityType.CLEANING, Arrays.asList(300, 3600)); // 5min-1h
        activityDurations.put(ActivityType.WATCHING_TV, Arrays.asList(900, 10800)); // 15min-3h
        activityDurations.put(ActivityType.CALLING, Arrays.asList(60, 1800)); // 1min-30min
        activityDurations.put(ActivityType.KNITTING, Arrays.asList(600, 7200)); // 10min-2h
        activityDurations.put(ActivityType.TALKING, Arrays.asList(60, 3600)); // 1min-1h
        activityDurations.put(ActivityType.PLAYING, Arrays.asList(600, 7200)); // 10min-2h
        activityDurations.put(ActivityType.PRESENT_INACTIVE, Arrays.asList(60, 1800)); // 1min-30min
    }
    
    /**
     * Détermine si une séquence d'activités représente une anomalie.
     * 
     * @param activities Séquence d'activités à analyser
     * @return true si la séquence est considérée comme anormale
     */
    public boolean isAnomaly(List<ActivitySequenceItem> activities) {
        if (activities.isEmpty()) {
            return false;
        }
        
        // Facteurs d'anomalie
        Map<String, Double> anomalyFactors = getAnomalyFactors(activities);
        
        // Score d'anomalie global
        double anomalyScore = anomalyFactors.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        
        boolean isAnomaly = anomalyScore > config.getAnomalyThreshold();
        logger.debug("Détection d'anomalie: {} (score: {})", isAnomaly, anomalyScore);
        
        return isAnomaly;
    }
    
    /**
     * Calcule les facteurs contribuant à la détection d'anomalies.
     * 
     * @param activities Séquence d'activités à analyser
     * @return Map des facteurs d'anomalie avec leur score
     */
    public Map<String, Double> getAnomalyFactors(List<ActivitySequenceItem> activities) {
        Map<String, Double> factors = new HashMap<>();
        
        // Facteur 1: Activités inhabituelles pour l'heure
        factors.put("unusual_activity_for_time", calculateTimeAnomalyFactor(activities));
        
        // Facteur 2: Durées anormales des activités
        factors.put("unusual_activity_duration", calculateDurationAnomalyFactor(activities));
        
        // Facteur 3: Transitions rapides entre activités
        factors.put("rapid_activity_transitions", calculateTransitionAnomalyFactor(activities));
        
        // Facteur 4: Activités répétitives
        factors.put("repetitive_activities", calculateRepetitionAnomalyFactor(activities));
        
        return factors;
    }
    
    /**
     * Calcule le facteur d'anomalie basé sur l'adéquation des activités à l'heure de la journée.
     * 
     * @param activities Séquence d'activités à analyser
     * @return Score d'anomalie (plus élevé = plus anormal)
     */
    private double calculateTimeAnomalyFactor(List<ActivitySequenceItem> activities) {
        // Regrouper les activités par heure
        Map<Integer, List<ActivitySequenceItem>> activitiesByHour = activities.stream()
                .collect(Collectors.groupingBy(item -> {
                    LocalTime time = LocalTime.ofInstant(item.getStartTime(), ZoneId.systemDefault());
                    return time.getHour();
                }));
        
        // Calculer le score d'anomalie pour chaque heure
        double totalScore = 0.0;
        int hourCount = 0;
        
        for (Map.Entry<Integer, List<ActivitySequenceItem>> entry : activitiesByHour.entrySet()) {
            int hour = entry.getKey();
            List<ActivitySequenceItem> hourActivities = entry.getValue();
            
            // Statistiques de référence pour cette heure
            Map<ActivityType, Double> hourStats = activityStatsByHour.get(hour);
            if (hourStats == null) {
                continue;
            }
            
            // Calculer le score d'anomalie pour cette heure
            double hourAnomalyScore = 0.0;
            for (ActivitySequenceItem activity : hourActivities) {
                ActivityType type = activity.getActivityType();
                
                // Probabilité de cette activité à cette heure (valeur par défaut = 0.05)
                double probability = hourStats.getOrDefault(type, 0.05);
                
                // Plus la probabilité est faible, plus l'anomalie est forte
                double activityAnomalyScore = 1.0 - probability;
                hourAnomalyScore += activityAnomalyScore;
            }
            
            // Normaliser le score pour cette heure
            if (!hourActivities.isEmpty()) {
                hourAnomalyScore /= hourActivities.size();
                totalScore += hourAnomalyScore;
                hourCount++;
            }
        }
        
        // Moyenne des scores d'anomalie pour toutes les heures
        return hourCount > 0 ? totalScore / hourCount : 0.0;
    }
    
    /**
     * Calcule le facteur d'anomalie basé sur les durées inhabituelles des activités.
     * 
     * @param activities Séquence d'activités à analyser
     * @return Score d'anomalie (plus élevé = plus anormal)
     */
    private double calculateDurationAnomalyFactor(List<ActivitySequenceItem> activities) {
        double totalAnomalyScore = 0.0;
        int activityCount = 0;
        
        for (ActivitySequenceItem activity : activities) {
            // Ignorer les activités en cours (durée non déterminable)
            if (activity.isOngoing()) {
                continue;
            }
            
            ActivityType type = activity.getActivityType();
            int durationSec = activity.getDurationSec();
            
            // Récupérer les durées typiques pour cette activité
            List<Integer> typicalDurations = activityDurations.get(type);
            if (typicalDurations == null || typicalDurations.size() < 2) {
                continue;
            }
            
            // Durée minimale et maximale typiques
            int minDuration = typicalDurations.get(0);
            int maxDuration = typicalDurations.get(1);
            
            double anomalyScore;
            if (durationSec < minDuration) {
                // Trop court
                double ratio = Math.max(0.0, (double) durationSec / minDuration);
                anomalyScore = 1.0 - ratio;
            } else if (durationSec > maxDuration) {
                // Trop long
                double excessRatio = (double) (durationSec - maxDuration) / maxDuration;
                anomalyScore = Math.min(1.0, excessRatio);
            } else {
                // Dans la plage normale
                anomalyScore = 0.0;
            }
            
            totalAnomalyScore += anomalyScore;
            activityCount++;
        }
        
        return activityCount > 0 ? totalAnomalyScore / activityCount : 0.0;
    }
    
    /**
     * Calcule le facteur d'anomalie basé sur des transitions rapides entre activités.
     * 
     * @param activities Séquence d'activités à analyser
     * @return Score d'anomalie (plus élevé = plus anormal)
     */
    private double calculateTransitionAnomalyFactor(List<ActivitySequenceItem> activities) {
        if (activities.size() < 2) {
            return 0.0;
        }
        
        // Trier les activités par heure de début
        List<ActivitySequenceItem> sortedActivities = activities.stream()
                .sorted(Comparator.comparing(ActivitySequenceItem::getStartTime))
                .collect(Collectors.toList());
        
        // Compter les transitions rapides (moins de 2 minutes)
        int rapidTransitions = 0;
        int totalTransitions = 0;
        
        for (int i = 0; i < sortedActivities.size() - 1; i++) {
            ActivitySequenceItem current = sortedActivities.get(i);
            ActivitySequenceItem next = sortedActivities.get(i + 1);
            
            // Ne compter que les transitions entre activités différentes
            if (current.getActivityType() != next.getActivityType()) {
                totalTransitions++;
                
                // Calcul de la durée entre la fin de l'activité actuelle et le début de la suivante
                Instant endTime = current.getEndTime() != null ? current.getEndTime() : next.getStartTime();
                long transitionDuration = Duration.between(endTime, next.getStartTime()).getSeconds();
                
                if (transitionDuration < 120) { // Moins de 2 minutes
                    rapidTransitions++;
                }
            }
        }
        
        return totalTransitions > 0 ? (double) rapidTransitions / totalTransitions : 0.0;
    }
    
    /**
     * Calcule le facteur d'anomalie basé sur des activités répétitives.
     * 
     * @param activities Séquence d'activités à analyser
     * @return Score d'anomalie (plus élevé = plus anormal)
     */
    private double calculateRepetitionAnomalyFactor(List<ActivitySequenceItem> activities) {
        if (activities.size() < 3) {
            return 0.0;
        }
        
        // Compter les occurrences de chaque activité
        Map<ActivityType, Integer> activityCounts = activities.stream()
                .collect(Collectors.groupingBy(ActivitySequenceItem::getActivityType, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().intValue()));
        
        // Chercher des séquences répétitives (même activité qui revient fréquemment)
        List<ActivityType> activitySequence = activities.stream()
                .sorted(Comparator.comparing(ActivitySequenceItem::getStartTime))
                .map(ActivitySequenceItem::getActivityType)
                .collect(Collectors.toList());
        
        // Compter les alternances entre deux activités
        int alternations = 0;
        for (int i = 0; i < activitySequence.size() - 2; i++) {
            if (activitySequence.get(i) == activitySequence.get(i + 2) && 
                activitySequence.get(i) != activitySequence.get(i + 1)) {
                alternations++;
            }
        }
        
        // Score d'alternance
        double alternationScore = activitySequence.size() > 2 ? 
                (double) alternations / (activitySequence.size() - 2) : 0.0;
        
        // Score de répétition basé sur la distribution des activités
        double repetitionScore = 0.0;
        if (!activityCounts.isEmpty()) {
            // Calcul de l'entropie (plus basse = plus répétitif)
            double totalActivities = activities.size();
            double entropy = activityCounts.values().stream()
                    .mapToDouble(count -> {
                        double probability = count / totalActivities;
                        return -probability * Math.log(probability);
                    })
                    .sum();
            
            // Normalisation de l'entropie (1 - entropie normalisée)
            double maxEntropy = Math.log(activityCounts.size());
            if (maxEntropy > 0) {
                repetitionScore = 1.0 - (entropy / maxEntropy);
            }
        }
        
        // Combiner les scores
        return Math.max(alternationScore, repetitionScore);
    }
}
