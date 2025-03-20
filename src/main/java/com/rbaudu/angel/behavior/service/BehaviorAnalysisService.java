package com.rbaudu.angel.behavior.service;

import com.rbaudu.angel.analyzer.model.ActivityType;
import com.rbaudu.angel.analyzer.model.AnalysisResult;
import com.rbaudu.angel.behavior.config.BehaviorConfig;
import com.rbaudu.angel.behavior.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Service principal pour l'analyse des comportements à partir
 * des activités détectées.
 */
@Service
public class BehaviorAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(BehaviorAnalysisService.class);
    
    private final BehaviorConfig config;
    private final PatternRepository patternRepository;
    private final SequenceAnalyzer sequenceAnalyzer;
    private final AnomalyDetector anomalyDetector;
    
    // File d'attente des résultats d'analyse d'activités récents
    private final Queue<ActivitySequenceItem> recentActivities = new ConcurrentLinkedQueue<>();
    
    // Historique des comportements détectés
    private final Queue<BehaviorResult> behaviorsHistory = new ConcurrentLinkedQueue<>();
    
    // Instant de la dernière analyse de comportement
    private Instant lastAnalysisTime = Instant.EPOCH;
    
    /**
     * Constructeur avec injection de dépendances.
     */
    public BehaviorAnalysisService(BehaviorConfig config, 
                                 PatternRepository patternRepository,
                                 SequenceAnalyzer sequenceAnalyzer,
                                 AnomalyDetector anomalyDetector) {
        this.config = config;
        this.patternRepository = patternRepository;
        this.sequenceAnalyzer = sequenceAnalyzer;
        this.anomalyDetector = anomalyDetector;
    }
    
    /**
     * Initialisation après construction du bean.
     */
    @PostConstruct
    public void init() {
        logger.info("Initialisation du service d'analyse de comportement");
    }
    
    /**
     * Traite un nouveau résultat d'analyse d'activité et met à jour la séquence d'activités.
     * Déclenche une analyse de comportement si les conditions sont remplies.
     * 
     * @param analysisResult Résultat d'analyse d'activité à traiter
     * @return Résultat d'analyse de comportement (peut être null si aucune analyse n'a été déclenchée)
     */
    public BehaviorResult processActivityResult(AnalysisResult analysisResult) {
        if (analysisResult == null) {
            return null;
        }
        
        // Convertir le résultat d'analyse en élément de séquence
        ActivitySequenceItem newItem = ActivitySequenceItem.builder()
                .activityType(analysisResult.getActivityType())
                .startTime(analysisResult.getTimestamp())
                .confidence(analysisResult.getConfidence())
                .build();
        
        // Mettre à jour la séquence d'activités
        updateActivitySequence(newItem);
        
        // Vérifier si une analyse doit être déclenchée
        if (shouldTriggerAnalysis()) {
            return analyzeBehavior();
        }
        
        return null;
    }
    
    /**
     * Met à jour la séquence d'activités en ajoutant un nouvel élément
     * et en fermant l'élément précédent si nécessaire.
     * 
     * @param newItem Nouvel élément à ajouter
     */
    private void updateActivitySequence(ActivitySequenceItem newItem) {
        // Fermer l'activité précédente si elle existe et est différente
        ActivitySequenceItem lastItem = getLastActivity();
        if (lastItem != null && lastItem.isOngoing() 
                && !lastItem.getActivityType().equals(newItem.getActivityType())) {
            lastItem.setEndTime(newItem.getStartTime());
        }
        
        // Ajouter le nouvel élément
        recentActivities.add(newItem);
        
        // Supprimer les activités trop anciennes
        pruneOldActivities();
        
        logger.debug("Séquence d'activités mise à jour, {} éléments dans la fenêtre", recentActivities.size());
    }
    
    /**
     * Supprime les activités qui sont en dehors de la fenêtre temporelle configurée.
     */
    private void pruneOldActivities() {
        Instant cutoffTime = Instant.now().minusSeconds(config.getTimeWindowSec());
        recentActivities.removeIf(item -> item.getStartTime().isBefore(cutoffTime));
    }
    
    /**
     * Récupère la dernière activité de la séquence.
     * 
     * @return Dernière activité ou null si la séquence est vide
     */
    private ActivitySequenceItem getLastActivity() {
        if (recentActivities.isEmpty()) {
            return null;
        }
        return recentActivities.stream()
                .max(Comparator.comparing(ActivitySequenceItem::getStartTime))
                .orElse(null);
    }
    
    /**
     * Détermine si une analyse de comportement doit être déclenchée
     * basé sur le temps écoulé et le nombre d'activités.
     * 
     * @return true si une analyse doit être déclenchée
     */
    private boolean shouldTriggerAnalysis() {
        // Vérifier si assez de temps s'est écoulé depuis la dernière analyse
        boolean timeElapsed = Instant.now().isAfter(
                lastAnalysisTime.plusMillis(config.getAnalysisIntervalMs()));
        
        // Vérifier s'il y a assez d'activités pour une analyse
        boolean enoughActivities = recentActivities.size() >= config.getMinActivitiesForAnalysis();
        
        return timeElapsed && enoughActivities && config.isContinuousAnalysis();
    }
    
    /**
     * Analyse les activités récentes pour identifier des comportements.
     * 
     * @return Résultat de l'analyse de comportement
     */
    public BehaviorResult analyzeBehavior() {
        logger.debug("Début de l'analyse de comportement");
        lastAnalysisTime = Instant.now();
        
        try {
            // Récupérer tous les patterns de comportement
            List<BehaviorPattern> patterns = patternRepository.getAllPatterns();
            
            // Copier la liste des activités pour l'analyse
            List<ActivitySequenceItem> activitiesToAnalyze = new ArrayList<>(recentActivities);
            
            // Si pas assez d'activités, retourner un comportement inconnu
            if (activitiesToAnalyze.size() < config.getMinActivitiesForAnalysis()) {
                logger.debug("Pas assez d'activités pour l'analyse ({} < {})", 
                        activitiesToAnalyze.size(), config.getMinActivitiesForAnalysis());
                return BehaviorResult.unknownBehavior();
            }
            
            // Analyser les correspondances avec les patterns connus
            Map<BehaviorPattern, Double> patternMatches = new HashMap<>();
            for (BehaviorPattern pattern : patterns) {
                double matchScore = sequenceAnalyzer.calculateMatchScore(activitiesToAnalyze, pattern);
                if (matchScore >= config.getConfidenceThreshold()) {
                    patternMatches.put(pattern, matchScore);
                }
            }
            
            // Déterminer le comportement principal
            BehaviorResult result;
            if (patternMatches.isEmpty()) {
                // Si aucun pattern ne correspond, vérifier s'il s'agit d'une anomalie
                if (config.isAnomalyDetectionEnabled() && anomalyDetector.isAnomaly(activitiesToAnalyze)) {
                    result = createAnomalyResult(activitiesToAnalyze);
                } else {
                    // Comportement normal mais non reconnu
                    result = BehaviorResult.normalBehavior(0.7);
                }
            } else {
                // Créer le résultat à partir du meilleur match
                result = createResultFromBestMatch(patternMatches, activitiesToAnalyze);
            }
            
            // Enregistrer le résultat dans l'historique
            storeBehaviorResult(result);
            
            logger.debug("Analyse de comportement terminée: {}", result.getBehaviorType());
            return result;
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'analyse de comportement", e);
            return BehaviorResult.unknownBehavior();
        }
    }
    
    /**
     * Crée un résultat d'analyse pour un comportement anormal.
     * 
     * @param activities Activités analysées
     * @return Résultat de l'analyse
     */
    private BehaviorResult createAnomalyResult(List<ActivitySequenceItem> activities) {
        // Créer une analyse pour un comportement anormal
        ActivitySequenceItem firstActivity = activities.stream()
                .min(Comparator.comparing(ActivitySequenceItem::getStartTime))
                .orElseThrow();  
        
        Map<String, Double> factors = anomalyDetector.getAnomalyFactors(activities);
        
        return BehaviorResult.builder()
                .id(UUID.randomUUID().toString())
                .behaviorType(BehaviorType.UNUSUAL)
                .confidence(0.8)
                .startTime(firstActivity.getStartTime())
                .timestamp(Instant.now())
                .activityHistory(new ArrayList<>(activities))
                .ongoing(activities.stream().anyMatch(ActivitySequenceItem::isOngoing))
                .contributingFactors(factors)
                .build();
    }
    
    /**
     * Crée un résultat d'analyse à partir du meilleur pattern correspondant.
     * 
     * @param patternMatches Correspondances de patterns avec leur score
     * @param activities Activités analysées
     * @return Résultat de l'analyse
     */
    private BehaviorResult createResultFromBestMatch(
            Map<BehaviorPattern, Double> patternMatches, 
            List<ActivitySequenceItem> activities) {
        
        // Trouver le meilleur match
        Map.Entry<BehaviorPattern, Double> bestMatch = patternMatches.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();  
        
        BehaviorPattern bestPattern = bestMatch.getKey();
        double bestScore = bestMatch.getValue();
        
        // Déterminer le début du comportement
        ActivitySequenceItem firstActivity = activities.stream()
                .min(Comparator.comparing(ActivitySequenceItem::getStartTime))
                .orElseThrow();  
        
        // Calculer la durée
        ActivitySequenceItem lastActivity = activities.stream()
                .max(Comparator.comparing(a -> a.getEndTime() != null ? a.getEndTime() : Instant.now()))
                .orElseThrow();  
        
        int durationSec = (int) (lastActivity.getEndTime() != null 
                ? lastActivity.getEndTime().getEpochSecond() - firstActivity.getStartTime().getEpochSecond()
                : Instant.now().getEpochSecond() - firstActivity.getStartTime().getEpochSecond());
        
        // Créer le résultat
        return BehaviorResult.builder()
                .id(UUID.randomUUID().toString())
                .behaviorType(bestPattern.getType())
                .confidence(bestScore)
                .startTime(firstActivity.getStartTime())
                .timestamp(Instant.now())
                .detectedPatterns(patternMatches)
                .activityHistory(new ArrayList<>(activities))
                .durationSec(durationSec)
                .ongoing(activities.stream().anyMatch(ActivitySequenceItem::isOngoing))
                .build();
    }
    
    /**
     * Stocke un résultat d'analyse de comportement dans l'historique
     * et maintient la taille de l'historique selon la configuration.
     * 
     * @param result Résultat à stocker
     */
    private void storeBehaviorResult(BehaviorResult result) {
        behaviorsHistory.add(result);
        
        // Maintenir la taille de l'historique
        while (behaviorsHistory.size() > config.getHistorySize()) {
            behaviorsHistory.poll();
        }
    }
    
    /**
     * Récupère tous les résultats d'analyse de comportement récents.
     * 
     * @return Liste des résultats d'analyse
     */
    public List<BehaviorResult> getRecentBehaviors() {
        return new ArrayList<>(behaviorsHistory);
    }
    
    /**
     * Récupère les N derniers résultats d'analyse de comportement.
     * 
     * @param limit Nombre maximal de résultats à récupérer
     * @return Liste des résultats d'analyse
     */
    public List<BehaviorResult> getRecentBehaviors(int limit) {
        return behaviorsHistory.stream()
                .sorted(Comparator.comparing(BehaviorResult::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les résultats d'analyse pour un type de comportement spécifique.
     * 
     * @param type Type de comportement à rechercher
     * @return Liste des résultats d'analyse
     */
    public List<BehaviorResult> getBehaviorsByType(BehaviorType type) {
        return behaviorsHistory.stream()
                .filter(result -> result.getBehaviorType() == type)
                .sorted(Comparator.comparing(BehaviorResult::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère le dernier résultat d'analyse de comportement.
     * 
     * @return Dernier résultat ou null si aucun résultat n'est disponible
     */
    public BehaviorResult getLatestBehavior() {
        return behaviorsHistory.stream()
                .max(Comparator.comparing(BehaviorResult::getTimestamp))
                .orElse(null);
    }
}