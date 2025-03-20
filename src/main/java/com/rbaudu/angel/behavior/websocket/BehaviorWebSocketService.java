package com.rbaudu.angel.behavior.websocket;

import com.rbaudu.angel.behavior.event.BehaviorResultEvent;
import com.rbaudu.angel.behavior.model.BehaviorResult;
import com.rbaudu.angel.behavior.model.BehaviorResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service de notification WebSocket pour les résultats d'analyse de comportement.
 */
@Service
public class BehaviorWebSocketService {
    private static final Logger logger = LoggerFactory.getLogger(BehaviorWebSocketService.class);
    
    private static final String BEHAVIOR_TOPIC = "/topic/behavior";
    private static final String UNUSUAL_BEHAVIOR_TOPIC = "/topic/behavior/unusual";
    
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Constructeur avec injection de dépendances.
     */
    public BehaviorWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * Écoute les événements de résultat d'analyse de comportement
     * et les diffuse via WebSocket.
     * 
     * @param event Événement de résultat d'analyse
     */
    @EventListener
    public void handleBehaviorResultEvent(BehaviorResultEvent event) {
        logger.debug("Diffusion d'un résultat d'analyse de comportement via WebSocket");
        
        BehaviorResultDto result = event.getResult();
        
        // Envoyer à tous les clients abonnés au topic général
        messagingTemplate.convertAndSend(BEHAVIOR_TOPIC, result);
        
        // Si comportement inhabituel, envoyer aussi sur le topic spécifique
        if (result.getBehaviorType().equals("UNUSUAL")) {
            logger.info("Comportement inhabituel détecté, notification spécifique envoyée");
            messagingTemplate.convertAndSend(UNUSUAL_BEHAVIOR_TOPIC, result);
        }
    }
    
    /**
     * Diffuse un résultat d'analyse de comportement manuellement.
     * 
     * @param result Résultat à diffuser
     */
    public void sendBehaviorResult(BehaviorResult result) {
        if (result == null) {
            return;
        }
        
        BehaviorResultDto dto = BehaviorResultDto.fromResult(result);
        messagingTemplate.convertAndSend(BEHAVIOR_TOPIC, dto);
        
        // Si comportement inhabituel, envoyer aussi sur le topic spécifique
        if (result.getBehaviorType().toString().equals("UNUSUAL")) {
            messagingTemplate.convertAndSend(UNUSUAL_BEHAVIOR_TOPIC, dto);
        }
    }
}