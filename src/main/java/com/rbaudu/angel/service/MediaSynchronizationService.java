package com.rbaudu.angel.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.rbaudu.angel.config.AppConfig;
import com.rbaudu.angel.event.AudioEvent;
import com.rbaudu.angel.event.VideoEvent;
import com.rbaudu.angel.model.AudioChunk;
import com.rbaudu.angel.model.SynchronizedMedia;
import com.rbaudu.angel.model.VideoFrame;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsable de la synchronisation des flux audio et vidéo.
 * Ce service écoute les événements de capture et synchronise les flux.
 */
@Service
@Slf4j
public class MediaSynchronizationService {

    @Autowired
    private AppConfig config;
    
    @Autowired
    private MediaEventPublisher eventPublisher;
    
    private final Map<Long, VideoFrame> videoFramesBuffer = new ConcurrentHashMap<>();
    private final Map<Long, AudioChunk> audioChunksBuffer = new ConcurrentHashMap<>();
    
    private ScheduledExecutorService cleanupExecutor;
    
    /**
     * Initialise le service de synchronisation.
     */
    @PostConstruct
    public void init() {
        log.info("Initialisation du service de synchronisation des médias...");
        
        // Planifier un nettoyage périodique des buffers
        cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        cleanupExecutor.scheduleAtFixedRate(
                this::cleanupOldBuffers,
                10,
                10,
                TimeUnit.SECONDS
        );
        
        log.info("Service de synchronisation des médias initialisé");
    }
    
    /**
     * Écoute les événements de trame vidéo.
     * 
     * @param event l'événement de trame vidéo
     */
    @EventListener
    public void handleVideoEvent(VideoEvent event) {
        VideoFrame videoFrame = event.getVideoFrame();
        
        // Stocker la trame dans le buffer
        videoFramesBuffer.put(videoFrame.getSequenceNumber(), videoFrame);
        
        // Essayer de trouver un segment audio correspondant
        findMatchingAudioAndSynchronize(videoFrame);
        
        // Publier un média synchronisé même s'il n'y a pas d'audio
        if (!config.isAudioEnabled()) {
            publishVideoOnly(videoFrame);
        }
    }
    
    /**
     * Écoute les événements de segment audio.
     * 
     * @param event l'événement de segment audio
     */
    @EventListener
    public void handleAudioEvent(AudioEvent event) {
        AudioChunk audioChunk = event.getAudioChunk();
        
        // Stocker le segment dans le buffer
        audioChunksBuffer.put(audioChunk.getSequenceNumber(), audioChunk);
        
        // Essayer de trouver une trame vidéo correspondante
        findMatchingVideoAndSynchronize(audioChunk);
        
        // Publier un média synchronisé même s'il n'y a pas de vidéo
        if (!config.isVideoEnabled()) {
            publishAudioOnly(audioChunk);
        }
    }
    
    /**
     * Cherche un segment audio correspondant à une trame vidéo et les synchronise.
     * 
     * @param videoFrame la trame vidéo
     */
    private void findMatchingAudioAndSynchronize(VideoFrame videoFrame) {
        Instant videoTimestamp = videoFrame.getTimestamp();
        
        // Trouver le segment audio le plus proche en temps
        AudioChunk bestMatch = null;
        long minTimeDifference = Long.MAX_VALUE;
        
        for (AudioChunk audioChunk : audioChunksBuffer.values()) {
            Instant audioTimestamp = audioChunk.getTimestamp();
            long timeDiff = Math.abs(ChronoUnit.MILLIS.between(videoTimestamp, audioTimestamp));
            
            if (timeDiff < minTimeDifference && timeDiff <= config.getSyncMaxDelayMs()) {
                minTimeDifference = timeDiff;
                bestMatch = audioChunk;
            }
        }
        
        // Si un segment audio correspondant est trouvé, créer un média synchronisé
        if (bestMatch != null) {
            createSynchronizedMedia(videoFrame, bestMatch, minTimeDifference);
        }
    }
    
    /**
     * Cherche une trame vidéo correspondant à un segment audio et les synchronise.
     * 
     * @param audioChunk le segment audio
     */
    private void findMatchingVideoAndSynchronize(AudioChunk audioChunk) {
        Instant audioTimestamp = audioChunk.getTimestamp();
        
        // Trouver la trame vidéo la plus proche en temps
        VideoFrame bestMatch = null;
        long minTimeDifference = Long.MAX_VALUE;
        
        for (VideoFrame videoFrame : videoFramesBuffer.values()) {
            Instant videoTimestamp = videoFrame.getTimestamp();
            long timeDiff = Math.abs(ChronoUnit.MILLIS.between(audioTimestamp, videoTimestamp));
            
            if (timeDiff < minTimeDifference && timeDiff <= config.getSyncMaxDelayMs()) {
                minTimeDifference = timeDiff;
                bestMatch = videoFrame;
            }
        }
        
        // Si une trame vidéo correspondante est trouvée, créer un média synchronisé
        if (bestMatch != null) {
            createSynchronizedMedia(bestMatch, audioChunk, minTimeDifference);
        }
    }
    
    /**
     * Crée et publie un média synchronisé à partir d'une trame vidéo et d'un segment audio.
     * 
     * @param videoFrame la trame vidéo
     * @param audioChunk le segment audio
     * @param syncDelayMs le délai de synchronisation en millisecondes
     */
    private void createSynchronizedMedia(VideoFrame videoFrame, AudioChunk audioChunk, long syncDelayMs) {
        // Déterminer le timestamp de synchronisation (le plus récent des deux)
        Instant syncTimestamp = videoFrame.getTimestamp().isAfter(audioChunk.getTimestamp()) 
                ? videoFrame.getTimestamp() 
                : audioChunk.getTimestamp();
        
        // Créer l'objet de média synchronisé
        SynchronizedMedia synchronizedMedia = SynchronizedMedia.builder()
                .id("sync-" + System.currentTimeMillis())
                .videoFrame(videoFrame)
                .audioChunk(audioChunk)
                .syncTimestamp(syncTimestamp)
                .hasVideo(true)
                .hasAudio(true)
                .syncDelayMs(syncDelayMs)
                .build();
        
        // Publier l'événement
        eventPublisher.publishSynchronizedMedia(synchronizedMedia);
        
        log.debug("Média synchronisé créé: vidéo={}, audio={}, délai={}ms", 
                videoFrame.getSequenceNumber(), audioChunk.getSequenceNumber(), syncDelayMs);
    }
    
    /**
     * Publie un média synchronisé avec seulement une trame vidéo.
     * 
     * @param videoFrame la trame vidéo
     */
    private void publishVideoOnly(VideoFrame videoFrame) {
        SynchronizedMedia media = SynchronizedMedia.ofVideo(videoFrame);
        eventPublisher.publishSynchronizedMedia(media);
        log.debug("Média vidéo-seulement publié: {}", videoFrame.getSequenceNumber());
    }
    
    /**
     * Publie un média synchronisé avec seulement un segment audio.
     * 
     * @param audioChunk le segment audio
     */
    private void publishAudioOnly(AudioChunk audioChunk) {
        SynchronizedMedia media = SynchronizedMedia.ofAudio(audioChunk);
        eventPublisher.publishSynchronizedMedia(media);
        log.debug("Média audio-seulement publié: {}", audioChunk.getSequenceNumber());
    }
    
    /**
     * Nettoie les buffers des médias obsolètes.
     */
    private void cleanupOldBuffers() {
        Instant cutoffTime = Instant.now().minus(30, ChronoUnit.SECONDS);
        
        // Nettoyer le buffer vidéo
        videoFramesBuffer.entrySet().removeIf(entry -> 
            entry.getValue().getTimestamp().isBefore(cutoffTime));
        
        // Nettoyer le buffer audio
        audioChunksBuffer.entrySet().removeIf(entry -> 
            entry.getValue().getTimestamp().isBefore(cutoffTime));
        
        log.debug("Nettoyage des buffers effectué. Vidéo: {}, Audio: {}", 
                videoFramesBuffer.size(), audioChunksBuffer.size());
    }
    
    /**
     * Nettoyage des ressources avant la destruction du bean.
     */
    @PreDestroy
    public void cleanup() {
        if (cleanupExecutor != null) {
            cleanupExecutor.shutdown();
            try {
                if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    cleanupExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                cleanupExecutor.shutdownNow();
            }
        }
        
        videoFramesBuffer.clear();
        audioChunksBuffer.clear();
    }
}