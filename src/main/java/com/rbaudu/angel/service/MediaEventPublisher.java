package com.rbaudu.angel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.rbaudu.angel.event.AudioEvent;
import com.rbaudu.angel.event.SynchronizedMediaEvent;
import com.rbaudu.angel.event.VideoEvent;
import com.rbaudu.angel.model.AudioChunk;
import com.rbaudu.angel.model.SynchronizedMedia;
import com.rbaudu.angel.model.VideoFrame;

import lombok.extern.slf4j.Slf4j;

/**
 * Service responsable de la publication d'événements médias.
 * Permet la communication entre les différents composants de l'application.
 */
@Service
@Slf4j
public class MediaEventPublisher {

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Publie un événement pour une trame vidéo.
     * 
     * @param videoFrame la trame vidéo
     */
    public void publishVideoFrame(VideoFrame videoFrame) {
        try {
            // Publier un événement Spring pour la trame vidéo
            VideoEvent event = new VideoEvent(this, videoFrame);
            eventPublisher.publishEvent(event);
            
            // Publier la trame via WebSocket pour l'affichage temps réel
            messagingTemplate.convertAndSend("/topic/video", videoFrame);
            
            log.debug("Trame vidéo publiée: {}", videoFrame.getSequenceNumber());
        } catch (Exception e) {
            log.error("Erreur lors de la publication de la trame vidéo", e);
        }
    }
    
    /**
     * Publie un événement pour un segment audio.
     * 
     * @param audioChunk le segment audio
     */
    public void publishAudioChunk(AudioChunk audioChunk) {
        try {
            // Publier un événement Spring pour le segment audio
            AudioEvent event = new AudioEvent(this, audioChunk);
            eventPublisher.publishEvent(event);
            
            // Publier le segment via WebSocket pour l'affichage temps réel
            messagingTemplate.convertAndSend("/topic/audio", audioChunk);
            
            log.debug("Segment audio publié: {}", audioChunk.getSequenceNumber());
        } catch (Exception e) {
            log.error("Erreur lors de la publication du segment audio", e);
        }
    }
    
    /**
     * Publie un événement pour un média synchronisé.
     * 
     * @param media le média synchronisé
     */
    public void publishSynchronizedMedia(SynchronizedMedia media) {
        try {
            // Publier un événement Spring pour le média synchronisé
            SynchronizedMediaEvent event = new SynchronizedMediaEvent(this, media);
            eventPublisher.publishEvent(event);
            
            // Publier le média via WebSocket pour l'affichage temps réel
            messagingTemplate.convertAndSend("/topic/synchronized", media);
            
            log.debug("Média synchronisé publié: {}", media.getId());
        } catch (Exception e) {
            log.error("Erreur lors de la publication du média synchronisé", e);
        }
    }
}
