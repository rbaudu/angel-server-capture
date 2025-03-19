package com.rbaudu.angel.analyzer.service.video;

import com.rbaudu.angel.analyzer.config.AnalyzerConfig;
import com.rbaudu.angel.analyzer.util.ModelLoader;
import com.rbaudu.angel.analyzer.util.VideoUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Service responsable de la détection de présence humaine dans les images vidéo.
 */
@Service
public class PresenceDetector {
    private static final Logger logger = LoggerFactory.getLogger(PresenceDetector.class);
    
    private final ModelLoader modelLoader;
    private final VideoUtils videoUtils;
    private final AnalyzerConfig config;
    
    private SavedModelBundle model;
    
    /**
     * Constructeur avec injection de dépendances.
     * @param modelLoader Chargeur de modèle TensorFlow
     * @param videoUtils Utilitaires vidéo
     * @param config Configuration de l'analyseur
     */
    public PresenceDetector(ModelLoader modelLoader, VideoUtils videoUtils, AnalyzerConfig config) {
        this.modelLoader = modelLoader;
        this.videoUtils = videoUtils;
        this.config = config;
    }
    
    /**
     * Initialisation du modèle après construction du bean.
     */
    @PostConstruct
    public void init() {
        try {
            if (config.getHumanDetectionModel() != null) {
                this.model = modelLoader.loadModel(config.getHumanDetectionModel());
                logger.info("Modèle de détection de présence humaine chargé avec succès");
            } else {
                logger.warn("Aucun modèle de détection de présence humaine configuré");
            }
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du modèle de détection de présence humaine", e);
        }
    }
    
    /**
     * Détecte si une personne est présente dans l'image.
     * @param frame Image à analyser
     * @return true si une personne est détectée, false sinon
     */
    public boolean isPersonPresent(Mat frame) {
        if (model == null) {
            logger.warn("Détection de présence impossible : modèle non chargé");
            return false;
        }
        
        try {
            // Prétraitement de l'image
            Tensor<?> imageTensor = videoUtils.prepareImageForModel(
                    frame, 
                    config.getInputImageWidth(),
                    config.getInputImageHeight());
            
            // Exécution de la détection avec TensorFlow
            Tensor<?> resultTensor = model.session().runner()
                    .feed("input", imageTensor)
                    .fetch("detection_scores")
                    .run().get(0);
            
            // Récupération des scores
            float[][] scores = new float[1][100]; // Supposons un maximum de 100 détections
            resultTensor.copyTo(scores);
            
            // Détection des personnes (classe 1 dans les modèles COCO)
            for (float score : scores[0]) {
                if (score > config.getPresenceThreshold()) {
                    logger.debug("Personne détectée avec un score de confiance de {}", score);
                    return true;
                }
            }
            
            logger.debug("Aucune personne détectée dans l'image");
            return false;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la détection de présence", e);
            return false;
        }
    }
    
    /**
     * Alternative à l'utilisation de TensorFlow: détection basée sur HOG + SVM via OpenCV
     * @param frame Image à analyser
     * @return true si une personne est détectée, false sinon
     */
    public boolean detectPersonWithHOG(Mat frame) {
        // Cette méthode pourrait être implémentée comme solution de secours
        // en utilisant les détecteurs intégrés d'OpenCV si TensorFlow n'est pas disponible
        
        // À implémenter si nécessaire
        
        return false;
    }
}
