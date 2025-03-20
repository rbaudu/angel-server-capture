package com.rbaudu.angel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.rbaudu.angel.model.AudioChunk;
import com.rbaudu.angel.model.SynchronizedMedia;
import com.rbaudu.angel.model.VideoFrame;
import com.rbaudu.angel.service.CaptureServiceManager;
import com.rbaudu.angel.service.MediaEventPublisher;

/**
 * Contrôleur WebSocket pour la communication en temps réel.
 */
@Controller
public class WebSocketController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private CaptureServiceManager captureServiceManager;
    
    @Autowired
    private MediaEventPublisher eventPublisher;
    
    /**
     * Point d'entrée pour démarrer ou arrêter la capture depuis le client WebSocket.
     * 
     * @param action l'action à effectuer (start, stop, restart)
     * @return résultat de l'action
     */
    @MessageMapping("/capture/control")
    @SendTo("/topic/status")
    public String controlCapture(String action) {
        log.info("Requête WebSocket pour contrôler la capture: {}", action);
        
        String result;
        
        switch (action.toLowerCase()) {
            case "start":
                boolean startSuccess = captureServiceManager.startServices();
                result = startSuccess ? "started" : "error";
                break;
                
            case "stop":
                captureServiceManager.stopServices();
                result = "stopped";
                break;
                
            case "restart":
                boolean restartSuccess = captureServiceManager.restartServices();
                result = restartSuccess ? "restarted" : "error";
                break;
                
            default:
                result = "unknown";
                break;
        }
        
        return result;
    }
    
    /**
     * Gérer les abonnements aux flux vidéo.
     * Cette méthode est appelée lorsqu'un client s'abonne au flux vidéo.
     * 
     * @param message message du client
     */
    @MessageMapping("/video/subscribe")
    public void subscribeToVideo(String message) {
        log.debug("Client abonné au flux vidéo: {}", message);
        // La logique d'envoi de vidéo est gérée par le service de publication d'événements
    }
    
    /**
     * Gérer les abonnements aux flux audio.
     * Cette méthode est appelée lorsqu'un client s'abonne au flux audio.
     * 
     * @param message message du client
     */
    @MessageMapping("/audio/subscribe")
    public void subscribeToAudio(String message) {
        log.debug("Client abonné au flux audio: {}", message);
        // La logique d'envoi d'audio est gérée par le service de publication d'événements
    }
    
    /**
     * Gérer les abonnements aux flux synchronisés.
     * Cette méthode est appelée lorsqu'un client s'abonne au flux synchronisé.
     * 
     * @param message message du client
     */
    @MessageMapping("/synchronized/subscribe")
    public void subscribeToSynchronized(String message) {
        log.debug("Client abonné au flux synchronisé: {}", message);
        // La logique d'envoi de médias synchronisés est gérée par le service de publication d'événements
    }
    
    /**
     * Envoie manuellement une trame vidéo à tous les clients abonnés.
     * 
     * @param videoFrame la trame vidéo à envoyer
     */
    public void sendVideoFrame(VideoFrame videoFrame) {
        messagingTemplate.convertAndSend("/topic/video", videoFrame);
    }
    
    /**
     * Envoie manuellement un segment audio à tous les clients abonnés.
     * 
     * @param audioChunk le segment audio à envoyer
     */
    public void sendAudioChunk(AudioChunk audioChunk) {
        messagingTemplate.convertAndSend("/topic/audio", audioChunk);
    }
    
    /**
     * Envoie manuellement un média synchronisé à tous les clients abonnés.
     * 
     * @param media le média synchronisé à envoyer
     */
    public void sendSynchronizedMedia(SynchronizedMedia media) {
        messagingTemplate.convertAndSend("/topic/synchronized", media);
    }
}