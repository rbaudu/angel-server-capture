package com.rbaudu.angel.behavior.service;

import com.rbaudu.angel.analyzer.model.ActivityType;
import com.rbaudu.angel.behavior.config.BehaviorConfig;
import com.rbaudu.angel.behavior.model.ActivitySequenceItem;
import com.rbaudu.angel.behavior.model.ActivityTransition;
import com.rbaudu.angel.behavior.model.BehaviorPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsable de l'analyse des séquences d'activités
 * pour détecter des patterns de comportement.
 */
@Service
public class SequenceAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(SequenceAnalyzer.class);
    
    private final BehaviorConfig config;
    
    /**
     * Constructeur avec injection de dépendances.
     */
    public SequenceAnalyzer(BehaviorConfig config) {
        this.config = config;
    }
    
    /**
     * Calcule le score de correspondance entre une séquence d'activités
     * et un pattern de comportement.
     * 
     * @param activities Séquence d'activités observées
     * @param pattern Pattern de comportement à comparer
     * @return Score de correspondance (entre 0.0 et 1.0)
     */
    public double calculateMatchScore(List<ActivitySequenceItem> activities, BehaviorPattern pattern) {
        if (activities.isEmpty() || pattern.getActivitySequence().isEmpty()) {
            return 0.0;
        }
        
        // Extraire juste les types d'activités pour la comparaison de séquence
        List<ActivityType> observedSequence = activities.stream()
                .map(ActivitySequenceItem::getActivityType)
                .collect(Collectors.toList());
        
        // Composants du score avec leurs poids
        double sequenceScore = calculateSequenceScore(observedSequence, pattern);
        double durationScore = calculateDurationScore(activities, pattern);
        double transitionScore = calculateTransitionScore(activities, pattern);
        double timeOfDayScore = calculateTimeOfDayScore(activities, pattern);
        
        // Pondération des différents facteurs
        double weightedScore = (sequenceScore * 0.5) + 
                              (durationScore * 0.2) + 
                              (transitionScore * 0.2) + 
                              (timeOfDayScore * 0.1);
        
        logger.debug("Score de correspondance pour pattern '{}': {}. (Seq: {}, Dur: {}, Trans: {}, Time: {})", 
                pattern.getName(), weightedScore, sequenceScore, durationScore, transitionScore, timeOfDayScore);
        
        return weightedScore;
    }
    
    /**
     * Calcule la correspondance entre deux séquences d'activités.
     * Utilise l'algorithme de la plus longue sous-séquence commune (LCS)
     * si l'ordre est important, ou un simple score de présence sinon.
     * 
     * @param observed Séquence observée
     * @param pattern Pattern de comportement
     * @return Score de correspondance (entre 0.0 et 1.0)
     */
    private double calculateSequenceScore(List<ActivityType> observed, BehaviorPattern pattern) {
        List<ActivityType> patternSequence = pattern.getActivitySequence();
        
        if (pattern.isStrictOrderRequired()) {
            // Utiliser la plus longue sous-séquence commune
            int lcsLength = longestCommonSubsequence(observed, patternSequence);
            return (double) lcsLength / patternSequence.size();
        } else {
            // Compter les activités communes sans tenir compte de l'ordre
            Set<ActivityType> observedSet = new HashSet<>(observed);
            Set<ActivityType> patternSet = new HashSet<>(patternSequence);
            
            // Intersection des deux ensembles
            Set<ActivityType> intersection = new HashSet<>(observedSet);
            intersection.retainAll(patternSet);
            
            return (double) intersection.size() / patternSet.size();
        }
    }
    
    /**
     * Calcule la longueur de la plus longue sous-séquence commune
     * entre deux séquences d'activités.
     * 
     * @param seq1 Première séquence
     * @param seq2 Deuxième séquence
     * @return Longueur de la plus longue sous-séquence commune
     */
    private int longestCommonSubsequence(List<ActivityType> seq1, List<ActivityType> seq2) {
        int m = seq1.size();
        int n = seq2.size();
        
        int[][] dp = new int[m + 1][n + 1];
        
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                } else if (seq1.get(i - 1) == seq2.get(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        
        return dp[m][n];
    }
    
    /**
     * Calcule la correspondance de durée entre les activités observées
     * et le pattern de comportement.
     * 
     * @param activities Activités observées
     * @param pattern Pattern de comportement
     * @return Score de correspondance pour la durée (entre 0.0 et 1.0)
     */
    private double calculateDurationScore(List<ActivitySequenceItem> activities, BehaviorPattern pattern) {
        // Si le pattern n'a pas de durée définie, score neutre
        if (pattern.getMinDurationSec() == null || pattern.getMaxDurationSec() == null) {
            return 0.5;
        }
        
        // Calculer la durée totale des activités
        long startTimestamp = activities.stream()
                .min(Comparator.comparing(ActivitySequenceItem::getStartTime))
                .map(item -> item.getStartTime().getEpochSecond())
                .orElse(0L);
        
        long endTimestamp = activities.stream()
                .filter(item -> item.getEndTime() != null)
                .max(Comparator.comparing(ActivitySequenceItem::getEndTime))
                .map(item -> item.getEndTime().getEpochSecond())
                .orElse(System.currentTimeMillis() / 1000);
        
        int durationSec = (int) (endTimestamp - startTimestamp);
        
        // Calculer le score en fonction de la plage de durée acceptable
        if (durationSec < pattern.getMinDurationSec()) {
            // Durée trop courte
            double ratio = (double) durationSec / pattern.getMinDurationSec();
            return Math.max(0.0, ratio);
        } else if (durationSec > pattern.getMaxDurationSec()) {
            // Durée trop longue
            double excessRatio = (double) (durationSec - pattern.getMaxDurationSec()) / pattern.getMaxDurationSec();
            return Math.max(0.0, 1.0 - excessRatio);
        } else {
            // Durée idéale, dans la plage
            return 1.0;
        }
    }
    
    /**
     * Calcule la correspondance des transitions entre activités
     * par rapport aux transitions attendues dans le pattern.
     * 
     * @param activities Activités observées
     * @param pattern Pattern de comportement
     * @return Score de correspondance pour les transitions (entre 0.0 et 1.0)
     */
    private double calculateTransitionScore(List<ActivitySequenceItem> activities, BehaviorPattern pattern) {
        // Si pas de transitions définies dans le pattern, score neutre
        if (pattern.getTransitions() == null || pattern.getTransitions().isEmpty()) {
            return 0.5;
        }
        
        // Créer un map des transitions définies dans le pattern
        Map<String, ActivityTransition> patternTransitions = pattern.getTransitions().stream()
                .collect(Collectors.toMap(
                        trans -> trans.getFromActivity() + "->" + trans.getToActivity(),
                        trans -> trans
                ));
        
        // Analyser les transitions observées
        int matchCount = 0;
        int totalTransitions = 0;
        
        for (int i = 0; i < activities.size() - 1; i++) {
            ActivityType from = activities.get(i).getActivityType();
            ActivityType to = activities.get(i + 1).getActivityType();
            
            // Ne compter que les transitions où l'activité change
            if (from != to) {
                totalTransitions++;
                
                String transKey = from + "->" + to;
                if (patternTransitions.containsKey(transKey)) {
                    matchCount++;
                }
            }
        }
        
        // Éviter la division par zéro
        if (totalTransitions == 0) {
            return 0.5; // Score neutre si pas de transitions
        }
        
        return (double) matchCount / totalTransitions;
    }
    
    /**
     * Calcule la correspondance de l'heure de la journée
     * par rapport aux heures typiques du pattern.
     * 
     * @param activities Activités observées
     * @param pattern Pattern de comportement
     * @return Score de correspondance pour l'heure (entre 0.0 et 1.0)
     */
    private double calculateTimeOfDayScore(List<ActivitySequenceItem> activities, BehaviorPattern pattern) {
        // Si pas d'heures typiques définies, score neutre
        if (pattern.getTypicalHours() == null || pattern.getTypicalHours().isEmpty()) {
            return 0.5;
        }
        
        // Déterminer l'heure médiane des activités
        int medianHour = activities.stream()
                .map(item -> LocalTime.ofInstant(item.getStartTime(), ZoneId.systemDefault()).getHour())
                .sorted()
                .collect(Collectors.toList())
                .get(activities.size() / 2);
        
        // Vérifier si l'heure est dans la liste des heures typiques
        if (pattern.getTypicalHours().contains(medianHour)) {
            return 1.0;
        }
        
        // Calculer la distance par rapport à l'heure typique la plus proche
        int minDistance = pattern.getTypicalHours().stream()
                .mapToInt(hour -> Math.min(
                        Math.abs(hour - medianHour),
                        Math.min(
                                Math.abs((hour + 24) - medianHour),
                                Math.abs(hour - (medianHour + 24))
                        )
                ))
                .min()
                .orElse(12); // 12 heures = distance maximale
        
        // Convertir la distance en score (0 = distance max, 1 = pas de distance)
        return Math.max(0.0, 1.0 - (minDistance / 12.0));
    }
}