package com.rbaudu.angel.analyzer.service;

import com.rbaudu.angel.analyzer.config.AnalyzerConfig;
import com.rbaudu.angel.analyzer.model.ActivityType;
import com.rbaudu.angel.analyzer.model.AnalysisResult;
import com.rbaudu.angel.analyzer.service.audio.AudioPatternDetector;
import com.rbaudu.angel.analyzer.service.fusion.MultimodalFusion;
import com.rbaudu.angel.analyzer.service.video.PresenceDetector;
import com.rbaudu.angel.analyzer.service.video.VisualActivityClassifier;
import org.bytedeco.opencv.opencv_core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Service d'orchestration du processus d'analyse d'activités.
 * Coordonne l'exécution des différentes étapes de l'analyse.
 */
@Service
public class AnalysisOrchestrator {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisOrchestrator.class);
    
    private final PresenceDetector presenceDetector;
    private final VisualActivityClassifier visualClassifier;
    private final AudioPatternDetector audioDetector;
    private final MultimodalFusion fusion;
    private final AnalyzerConfig config;
    
    /**
     * Constructeur avec injection de dépendances.
     * @param presenceDetector Détecteur de présence
     * @param visualClassifier Classificateur d'activités visuelles
     * @param audioDetector Détecteur de patterns audio
     * @param fusion Service de fusion multimodale
     * @param config Configuration de l'analyseur
     */
    public AnalysisOrchestrator(PresenceDetector presenceDetector,
                              VisualActivityClassifier visualClassifier,
                              AudioPatternDetector audioDetector,
                              MultimodalFusion fusion,
                              AnalyzerConfig config) {
        this.presenceDetector = presenceDetector;
        this.visualClassifier = visualClassifier;
        this.audioDetector = audioDetector;
        this.fusion = fusion;
        this.config = config;
    }
    
    /**
     * Analyse une frame vidéo et un flux audio synchronisés pour détecter l'activité.
     * @param videoFrame Frame vidéo à analyser
     * @param audioStream Flux audio synchronisé avec la vidéo
     * @return Résultat de l'analyse
     */
    public AnalysisResult analyzeFrame(Mat videoFrame, AudioInputStream audioStream) {
        logger.debug("Début de l'analyse d'une nouvelle frame");
        
        try {
            // Étape 1: Vérifier si une personne est présente
            boolean isPersonPresent = presenceDetector.isPersonPresent(videoFrame);
            
            if (!isPersonPresent) {
                logger.debug("Aucune personne détectée dans la frame");
                return AnalysisResult.personAbsent();
            }
            
            // Étape 2: Classifier l'activité basée sur la vidéo
            Map<ActivityType, Double> videoClassification = visualClassifier.classifyActivity(videoFrame);
            
            // Étape 3: Détecter les patterns audio si l'analyse audio est activée
            Map<ActivityType, Double> audioClassification = new HashMap<>();
            if (config.isAudioAnalysisEnabled() && audioStream != null) {
                audioClassification = audioDetector.detectAudioPatterns(audioStream);
            }
            
            // Étape 4: Fusionner les résultats pour une classification finale
            AnalysisResult result = fusion.fuseResults(videoClassification, audioClassification);
            
            logger.debug("Analyse terminée avec succès: {}", result);
            return result;
            
        } catch (Exception e) {
            logger.error("Erreur pendant l'analyse de la frame", e);
            return AnalysisResult.unknownActivity();
        }
    }
    
    /**
     * Analyse une frame vidéo sans audio.
     * @param videoFrame Frame vidéo à analyser
     * @return Résultat de l'analyse
     */
    public AnalysisResult analyzeVideoOnly(Mat videoFrame) {
        logger.debug("Analyse vidéo uniquement");
        return analyzeFrame(videoFrame, null);
    }
    
    /**
     * Analyse synchrone qui attend le résultat complet.
     * @param videoFrame Frame vidéo à analyser
     * @param audioStream Flux audio synchronisé
     * @return Résultat de l'analyse
     */
    public AnalysisResult analyzeSynchronously(Mat videoFrame, AudioInputStream audioStream) {
        return analyzeFrame(videoFrame, audioStream);
    }
    
    /**
     * Planifie une analyse asynchrone qui sera exécutée en arrière-plan.
     * Le résultat sera envoyé via un événement ou un callback.
     * @param videoFrame Frame vidéo à analyser
     * @param audioStream Flux audio synchronisé
     * @param callback Callback pour recevoir le résultat
     */
    public void analyzeAsynchronously(Mat videoFrame, AudioInputStream audioStream, 
                                    java.util.function.Consumer<AnalysisResult> callback) {
        // Exécution dans un thread séparé pour ne pas bloquer l'appelant
        new Thread(() -> {
            AnalysisResult result = analyzeFrame(videoFrame, audioStream);
            callback.accept(result);
        }).start();
    }
}
