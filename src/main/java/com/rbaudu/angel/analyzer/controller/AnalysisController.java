package com.rbaudu.angel.analyzer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbaudu.angel.analyzer.config.AnalyzerConfig;
import com.rbaudu.angel.analyzer.model.ActivityType;
import com.rbaudu.angel.analyzer.model.AnalysisResultDto;
import com.rbaudu.angel.event.SynchronizedMediaEvent;
import com.rbaudu.angel.model.SynchronizedMedia;

import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur REST pour l'accès aux fonctionnalités d'analyse d'activités.
 */
@RestController
@RequestMapping("/api/analysis")
@Slf4j
public class AnalysisController {
    
    @Autowired
    private AnalyzerConfig config;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Stockage des derniers résultats d'analyse pour l'API
    private final Map<String, AnalysisResultDto> latestResults = new ConcurrentHashMap<>();
    private final List<AnalysisResultDto> recentAnalyses = new ArrayList<>();
    private static final int MAX_RECENT_ANALYSES = 100;
    
    /**
     * Récupère la configuration actuelle de l'analyseur.
     * 
     * @return la configuration
     */
    @GetMapping("/config")
    public ResponseEntity<AnalyzerConfig> getConfig() {
        return ResponseEntity.ok(config);
    }
    
    /**
     * Met à jour partiellement la configuration de l'analyseur.
     * 
     * @param configUpdates modifications à appliquer
     * @return la configuration mise à jour
     */
    @PostMapping("/config")
    public ResponseEntity<AnalyzerConfig> updateConfig(@RequestBody Map<String, Object> configUpdates) {
        try {
            // Exemple simple de mise à jour des propriétés
            if (configUpdates.containsKey("audioAnalysisEnabled")) {
                config.setAudioAnalysisEnabled((Boolean) configUpdates.get("audioAnalysisEnabled"));
            }
            
            if (configUpdates.containsKey("presenceThreshold")) {
                config.setPresenceThreshold((Double) configUpdates.get("presenceThreshold"));
            }
            
            if (configUpdates.containsKey("activityConfidenceThreshold")) {
                config.setActivityConfidenceThreshold((Double) configUpdates.get("activityConfidenceThreshold"));
            }
            
            // Autres propriétés...
            
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de la configuration", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Récupère le dernier résultat d'analyse.
     * 
     * @return le dernier résultat d'analyse
     */
    @GetMapping("/latest")
    public ResponseEntity<AnalysisResultDto> getLatestAnalysis() {
        if (latestResults.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        // Retourner le résultat le plus récent
        return ResponseEntity.ok(recentAnalyses.get(0));
    }
    
    /**
     * Récupère les derniers résultats d'analyse.
     * 
     * @param limit nombre maximum de résultats à retourner
     * @return les derniers résultats d'analyse
     */
    @GetMapping("/recent")
    public ResponseEntity<List<AnalysisResultDto>> getRecentAnalyses(
            @RequestParam(defaultValue = "10") int limit) {
        
        if (recentAnalyses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        int resultLimit = Math.min(limit, recentAnalyses.size());
        return ResponseEntity.ok(recentAnalyses.subList(0, resultLimit));
    }
    
    /**
     * Récupère les résultats d'analyse pour une activité spécifique.
     * 
     * @param activityType type d'activité à rechercher
     * @return les résultats d'analyse correspondants
     */
    @GetMapping("/activity/{activityType}")
    public ResponseEntity<List<AnalysisResultDto>> getAnalysesByActivity(
            @PathVariable ActivityType activityType) {
        
        List<AnalysisResultDto> results = new ArrayList<>();
        
        for (AnalysisResultDto result : recentAnalyses) {
            if (result.getActivityType() == activityType) {
                results.add(result);
            }
        }
        
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(results);
    }
    
    /**
     * Écoute les événements de média synchronisé pour stocker les résultats d'analyse.
     * 
     * @param event événement de média synchronisé
     */
    @EventListener
    public void handleSynchronizedMediaEvent(SynchronizedMediaEvent event) {
        SynchronizedMedia media = event.getSynchronizedMedia();
        
        // Si le média n'a pas de résultats d'analyse, ignorer
        if (media.getAnalysisResults() == null) {
            return;
        }
        
        try {
            // Convertir les résultats JSON en DTO
            AnalysisResultDto result = objectMapper.readValue(
                    media.getAnalysisResults(), 
                    AnalysisResultDto.class);
            
            // Stocker le résultat
            latestResults.put(media.getId(), result);
            
            // Ajouter au début de la liste des résultats récents
            synchronized (recentAnalyses) {
                recentAnalyses.add(0, result);
                
                // Limiter la taille de la liste
                if (recentAnalyses.size() > MAX_RECENT_ANALYSES) {
                    recentAnalyses.remove(recentAnalyses.size() - 1);
                }
            }
            
        } catch (Exception e) {
            log.error("Erreur lors du traitement des résultats d'analyse", e);
        }
    }
}
