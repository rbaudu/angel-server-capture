package com.rbaudu.angel.behavior.event;

import com.rbaudu.angel.analyzer.model.AnalysisResult;
import com.rbaudu.angel.behavior.model.BehaviorResult;
import com.rbaudu.angel.behavior.service.BehaviorAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Écouteur d'événements qui réagit aux résultats d'analyse d'activités
 * et déclenche l'analyse de comportement.
 */
@Component
public class AnalysisResultEventListener {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisResultEventListener.class);
    
    private final BehaviorAnalysisService behaviorAnalysisService;
    
    /**
     * Constructeur avec injection de dépendances.
     */
    public AnalysisResultEventListener(BehaviorAnalysisService behaviorAnalysisService) {
        this.behaviorAnalysisService = behaviorAnalysisService;
    }
    
    /**
     * Réagit aux événements de résultat d'analyse d'activités.
     * 
     * @param result Résultat d'analyse d'activités
     */
    @EventListener
    public void handleAnalysisResultEvent(AnalysisResult result) {
        logger.debug("Réception d'un événement d'analyse d'activité: {}", result.getActivityType());
        
        // Transmettre le résultat au service d'analyse de comportement
        BehaviorResult behaviorResult = behaviorAnalysisService.processActivityResult(result);
        
        // Si une analyse de comportement a été déclenchée, publier ses résultats
        if (behaviorResult != null) {
            logger.info("Comportement détecté: {} (confiance: {})", 
                    behaviorResult.getBehaviorType(), behaviorResult.getConfidence());
            publishBehaviorResult(behaviorResult);
        }
    }
    
    /**
     * Publie un résultat d'analyse de comportement pour notification.
     * 
     * @param result Résultat à publier
     */
    private void publishBehaviorResult(BehaviorResult result) {
        // Cette méthode pourrait publier l'événement via le mécanisme d'événements Spring
        // ou via WebSocket pour notification en temps réel
        
        // Pour l'instant, implémentation minimale (juste logs)
        logger.info("Publication du résultat de comportement: {}", result.getBehaviorType());
        
        // TODO: Implémenter la publication d'événements Spring ou WebSocket
        // applicationEventPublisher.publishEvent(new BehaviorResultEvent(result));
    }
}