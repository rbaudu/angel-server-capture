package com.rbaudu.angel.behavior.controller;

import com.rbaudu.angel.behavior.config.BehaviorConfig;
import com.rbaudu.angel.behavior.model.BehaviorPattern;
import com.rbaudu.angel.behavior.model.BehaviorResult;
import com.rbaudu.angel.behavior.model.BehaviorResultDto;
import com.rbaudu.angel.behavior.model.BehaviorType;
import com.rbaudu.angel.behavior.service.BehaviorAnalysisService;
import com.rbaudu.angel.behavior.service.PatternRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion et l'interrogation des analyses de comportement.
 */
@RestController
@RequestMapping("/api/behavior")
public class BehaviorController {
    private static final Logger logger = LoggerFactory.getLogger(BehaviorController.class);
    
    private final BehaviorAnalysisService analysisService;
    private final PatternRepository patternRepository;
    private final BehaviorConfig config;
    
    /**
     * Constructeur avec injection de dépendances.
     */
    public BehaviorController(BehaviorAnalysisService analysisService, 
                             PatternRepository patternRepository,
                             BehaviorConfig config) {
        this.analysisService = analysisService;
        this.patternRepository = patternRepository;
        this.config = config;
    }
    
    /**
     * Récupère le dernier résultat d'analyse de comportement.
     * 
     * @return Dernier résultat d'analyse ou 404 si aucun résultat disponible
     */
    @GetMapping("/latest")
    public ResponseEntity<BehaviorResultDto> getLatestBehavior() {
        BehaviorResult latestResult = analysisService.getLatestBehavior();
        if (latestResult == null) {
            return ResponseEntity.notFound().build();
        }
        
        BehaviorResultDto dto = BehaviorResultDto.fromResult(latestResult);
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Récupère les N derniers résultats d'analyse de comportement.
     * 
     * @param limit Nombre maximal de résultats à récupérer (optionnel, défaut = 10)
     * @return Liste des résultats d'analyse
     */
    @GetMapping("/recent")
    public ResponseEntity<List<BehaviorResultDto>> getRecentBehaviors(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        List<BehaviorResult> recentResults = analysisService.getRecentBehaviors(limit);
        List<BehaviorResultDto> dtos = recentResults.stream()
                .map(BehaviorResultDto::fromResult)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Récupère les résultats d'analyse pour un type de comportement spécifique.
     * 
     * @param type Type de comportement à rechercher
     * @return Liste des résultats d'analyse
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<BehaviorResultDto>> getBehaviorsByType(
            @PathVariable("type") String type) {
        
        BehaviorType behaviorType;
        try {
            behaviorType = BehaviorType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        
        List<BehaviorResult> results = analysisService.getBehaviorsByType(behaviorType);
        List<BehaviorResultDto> dtos = results.stream()
                .map(BehaviorResultDto::fromResult)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Déclenche manuellement une analyse de comportement.
     * 
     * @return Résultat de l'analyse déclenchée
     */
    @PostMapping("/analyze")
    public ResponseEntity<BehaviorResultDto> triggerAnalysis() {
        BehaviorResult result = analysisService.analyzeBehavior();
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        
        BehaviorResultDto dto = BehaviorResultDto.fromResult(result);
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Récupère tous les patterns de comportement définis.
     * 
     * @return Liste des patterns de comportement
     */
    @GetMapping("/patterns")
    public ResponseEntity<List<BehaviorPattern>> getAllPatterns() {
        List<BehaviorPattern> patterns = patternRepository.getAllPatterns();
        return ResponseEntity.ok(patterns);
    }
    
    /**
     * Récupère la configuration actuelle du module de comportement.
     * 
     * @return Configuration actuelle
     */
    @GetMapping("/config")
    public ResponseEntity<BehaviorConfig> getConfig() {
        return ResponseEntity.ok(config);
    }
    
    /**
     * Met à jour la configuration du module de comportement.
     * 
     * @param configChanges Map des paramètres à mettre à jour
     * @return Configuration mise à jour
     */
    @PostMapping("/config")
    public ResponseEntity<BehaviorConfig> updateConfig(
            @RequestBody Map<String, Object> configChanges) {
        
        // Appliquer les changements de configuration
        // Note: Cette méthode est simplifiée et devrait être améliorée pour gérer tous les types de paramètres
        if (configChanges.containsKey("confidenceThreshold")) {
            config.setConfidenceThreshold(((Number) configChanges.get("confidenceThreshold")).doubleValue());
        }
        
        if (configChanges.containsKey("continuousAnalysis")) {
            config.setContinuousAnalysis((Boolean) configChanges.get("continuousAnalysis"));
        }
        
        if (configChanges.containsKey("anomalyDetectionEnabled")) {
            config.setAnomalyDetectionEnabled((Boolean) configChanges.get("anomalyDetectionEnabled"));
        }
        
        if (configChanges.containsKey("timeWindowSec")) {
            config.setTimeWindowSec(((Number) configChanges.get("timeWindowSec")).intValue());
        }
        
        if (configChanges.containsKey("historySize")) {
            config.setHistorySize(((Number) configChanges.get("historySize")).intValue());
        }
        
        // Retourner la configuration mise à jour
        return ResponseEntity.ok(config);
    }
    
    /**
     * Recharge les patterns de comportement depuis le fichier de configuration.
     * 
     * @return Nombre de patterns chargés
     */
    @PostMapping("/patterns/reload")
    public ResponseEntity<Map<String, Integer>> reloadPatterns() {
        int patternsLoaded = patternRepository.reloadPatterns();
        return ResponseEntity.ok(Map.of("patternsLoaded", patternsLoaded));
    }
    
    /**
     * Ajoute ou met à jour un pattern de comportement.
     * 
     * @param pattern Pattern à ajouter ou mettre à jour
     * @return Pattern enregistré
     */
    @PostMapping("/patterns")
    public ResponseEntity<BehaviorPattern> savePattern(@RequestBody BehaviorPattern pattern) {
        BehaviorPattern saved = patternRepository.savePattern(pattern);
        return ResponseEntity.ok(saved);
    }
    
    /**
     * Supprime un pattern de comportement.
     * 
     * @param id Identifiant du pattern à supprimer
     * @return 200 OK si supprimé, 404 si non trouvé
     */
    @DeleteMapping("/patterns/{id}")
    public ResponseEntity<Void> deletePattern(@PathVariable("id") String id) {
        boolean deleted = patternRepository.deletePattern(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}