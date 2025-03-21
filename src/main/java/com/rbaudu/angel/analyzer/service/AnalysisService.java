package com.rbaudu.angel.analyzer.service;

import java.io.ByteArrayInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.bytedeco.opencv.opencv_core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbaudu.angel.analyzer.config.AnalyzerConfig;
import com.rbaudu.angel.analyzer.model.AnalysisResult;
import com.rbaudu.angel.analyzer.model.AnalysisResultDto;
import com.rbaudu.angel.event.SynchronizedMediaEvent;
import com.rbaudu.angel.model.AudioChunk;
import com.rbaudu.angel.model.SynchronizedMedia;
import com.rbaudu.angel.model.VideoFrame;
import com.rbaudu.angel.service.MediaEventPublisher;

/**
 * Service responsable d'analyser les médias synchronisés pour détecter
 * la présence humaine et les activités.
 */
@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    @Autowired
    private AnalysisOrchestrator analysisOrchestrator;
    
    @Autowired
    private AnalyzerConfig config;
    
    @Autowired
    private MediaEventPublisher eventPublisher;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Écoute les événements de média synchronisé pour lancer l'analyse.
     * 
     * @param event l'événement de média synchronisé
     */
    @EventListener
    public void handleSynchronizedMediaEvent(SynchronizedMediaEvent event) {
        SynchronizedMedia media = event.getSynchronizedMedia();
        
        // Ignorer les médias déjà analysés ou si l'analyse est désactivée dans la configuration
        if (media.getAnalysisResults() != null || !config.isAudioAnalysisEnabled()) {
            return;
        }
        
        try {
            // Lancer l'analyse de manière asynchrone pour ne pas bloquer le flux d'événements
            analyzeMediaAsync(media);
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation de l'analyse du média synchronisé", e);
        }
    }
    
    /**
     * Lance une analyse asynchrone du média synchronisé.
     * 
     * @param media le média synchronisé à analyser
     */
    private void analyzeMediaAsync(SynchronizedMedia media) {
        // Lancer l'analyse dans un thread séparé pour ne pas bloquer
        new Thread(() -> {
            try {
                AnalysisResult result = analyzeSynchronizedMedia(media);
                updateMediaWithAnalysisResults(media, result);
            } catch (Exception e) {
                log.error("Erreur lors de l'analyse du média synchronisé", e);
            }
        }).start();
    }
    
    /**
     * Analyse un média synchronisé.
     * 
     * @param media le média synchronisé à analyser
     * @return le résultat de l'analyse
     */
    private AnalysisResult analyzeSynchronizedMedia(SynchronizedMedia media) {
        VideoFrame videoFrame = media.getVideoFrame();
        AudioChunk audioChunk = media.getAudioChunk();
        
        // Si le média n'a pas de vidéo, on ne peut pas faire d'analyse
        if (!media.isHasVideo() || videoFrame == null) {
            log.debug("Pas d'analyse possible : le média ne contient pas de vidéo");
            return AnalysisResult.unknownActivity(0.0);
        }
        
        // Extraire la frame vidéo de l'objet VideoFrame
        Mat frame = videoFrame.getFrameMat();
        
        // Si la matrice d'image n'est pas disponible, on ne peut pas faire d'analyse
        if (frame == null) {
            log.debug("Pas d'analyse possible : la matrice d'image n'est pas disponible");
            return AnalysisResult.unknownActivity(0.0);
        }
        
        // Préparer l'audio si disponible
        AudioInputStream audioStream = null;
        if (media.isHasAudio() && audioChunk != null && audioChunk.getAudioData() != null) {
            audioStream = convertToAudioStream(audioChunk);
        }
        
        // Appeler l'orchestrateur d'analyse avec la frame vidéo et l'audio (qui peut être null)
        log.debug("Lancement de l'analyse pour le média synchronisé {}", media.getId());
        return analysisOrchestrator.analyzeFrame(frame, audioStream);
    }
    
    /**
     * Convertit un AudioChunk en AudioInputStream pour l'analyse.
     * 
     * @param audioChunk le segment audio à convertir
     * @return un flux audio pour l'analyse
     */
    private AudioInputStream convertToAudioStream(AudioChunk audioChunk) {
        try {
            // Utiliser Base64.getDecoder().decode() pour convertir la chaîne Base64 en bytes
            byte[] audioData = java.util.Base64.getDecoder().decode(audioChunk.getAudioData());
            if (audioData == null || audioData.length == 0) {
                return null;
            }
            
            // Créer un format audio basé sur les propriétés fixes car AudioChunk n'a pas ces méthodes
            AudioFormat format = new AudioFormat(
                    audioChunk.getSampleRate(),
                    16, // bits par échantillon - valeur fixe pour PCM 16 bits
                    audioChunk.getChannels(),
                    true, // signé
                    false // little endian (PCM standard)
            );
            
            // Créer un AudioInputStream à partir des données brutes
            ByteArrayInputStream bis = new ByteArrayInputStream(audioData);
            return new AudioInputStream(
                    bis,
                    format,
                    audioData.length / format.getFrameSize()
            );
        } catch (Exception e) {
            log.error("Erreur lors de la conversion de l'AudioChunk en AudioInputStream", e);
            return null;
        }
    }
    
    /**
     * Met à jour le média synchronisé avec les résultats de l'analyse.
     * 
     * @param media le média synchronisé à mettre à jour
     * @param result le résultat de l'analyse
     */
    private void updateMediaWithAnalysisResults(SynchronizedMedia media, AnalysisResult result) {
        try {
            // Mettre à jour les propriétés de détection sur VideoFrame si disponible
            if (media.isHasVideo() && media.getVideoFrame() != null) {
                VideoFrame videoFrame = media.getVideoFrame();
                videoFrame.setPersonDetected(result.isPersonPresent());
            }
            
            // Convertir le résultat d'analyse en DTO
            AnalysisResultDto resultDto = AnalysisResultDto.fromAnalysisResult(result);
            
            // Convertir le DTO en JSON
            String analysisJson = objectMapper.writeValueAsString(resultDto);
            
            // Mettre à jour le média avec les résultats d'analyse
            media.setAnalysisResults(analysisJson);
            
            // Republier le média mis à jour
            eventPublisher.publishSynchronizedMedia(media);
            
            log.debug("Média {} mis à jour avec les résultats d'analyse: {}", 
                    media.getId(), result.getActivityType());
        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la conversion des résultats d'analyse en JSON", e);
        }
    }
}