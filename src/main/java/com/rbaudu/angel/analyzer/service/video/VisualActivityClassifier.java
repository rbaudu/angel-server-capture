package com.rbaudu.angel.analyzer.service.video;

import com.rbaudu.angel.analyzer.config.AnalyzerConfig;
import com.rbaudu.angel.analyzer.model.ActivityType;
import com.rbaudu.angel.analyzer.util.ModelLoader;
import com.rbaudu.angel.analyzer.util.VideoUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tensorflow.Result;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.types.TFloat32;

import jakarta.annotation.PostConstruct;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de classification d'activités basé sur l'analyse vidéo.
 */
@Service
public class VisualActivityClassifier {
    private static final Logger logger = LoggerFactory.getLogger(VisualActivityClassifier.class);
    
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
    public VisualActivityClassifier(ModelLoader modelLoader, VideoUtils videoUtils, AnalyzerConfig config) {
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
            if (config.getActivityRecognitionModel() != null) {
                this.model = modelLoader.loadModel(config.getActivityRecognitionModel());
                logger.info("Modèle de classification d'activités chargé avec succès");
            } else {
                logger.warn("Aucun modèle de classification d'activités configuré");
            }
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du modèle de classification d'activités", e);
        }
    }
    
    /**
     * Classifie l'activité visible dans l'image.
     * @param frame Image à analyser
     * @return Map des types d'activités avec leur score de confiance
     */
    public Map<ActivityType, Double> classifyActivity(Mat frame) {
        if (model == null) {
            logger.warn("Classification d'activités impossible : modèle non chargé");
            return new HashMap<>();
        }
        
        try {
            // Prétraitement de l'image
            Tensor imageTensor = videoUtils.prepareImageForModel(
                    frame, 
                    config.getInputImageWidth(),
                    config.getInputImageHeight());
            
            // Exécution de la classification avec TensorFlow
            Session.Runner runner = model.session().runner()
                    .feed("input", imageTensor)
                    .fetch("output");
            
            Result outputs = runner.run();
            TFloat32 resultTensor = (TFloat32)outputs.get(0);
            
            // Extraire les résultats du Tensor
            int numActivities = ActivityType.values().length - 1; // -1 pour exclure ABSENT
            float[] results = new float[numActivities];
            
            FloatBuffer resultBuffer = FloatBuffer.allocate(numActivities);
            resultTensor.copyTo(DataBuffers.of(resultBuffer));
            resultBuffer.position(0);  // Rewind the buffer
            resultBuffer.get(results);
            
            // Conversion des probabilités en map
            Map<ActivityType, Double> result = new HashMap<>();
            for (int i = 0; i < numActivities; i++) {
                ActivityType activity = mapIndexToActivityType(i);
                if (activity != ActivityType.ABSENT) { // On exclut ABSENT de la classification visuelle
                    double probability = results[i];
                    if (probability > config.getActivityConfidenceThreshold()) {
                        result.put(activity, probability);
                    }
                }
            }
            
            logger.debug("Activités classifiées: {}", result);
            return result;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la classification d'activités", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Mappe l'index de sortie du modèle à un type d'activité.
     * @param index Index dans le vecteur de sortie du modèle
     * @return Type d'activité correspondant
     */
    private ActivityType mapIndexToActivityType(int index) {
        // Mapping entre l'index de sortie du modèle et les types d'activités
        // À définir selon l'ordre des classes dans le modèle
        switch (index) {
            case 0: return ActivityType.PRESENT_INACTIVE;
            case 1: return ActivityType.SLEEPING;
            case 2: return ActivityType.EATING;
            case 3: return ActivityType.READING;
            case 4: return ActivityType.CLEANING;
            case 5: return ActivityType.WATCHING_TV;
            case 6: return ActivityType.CALLING;
            case 7: return ActivityType.KNITTING;
            case 8: return ActivityType.TALKING;
            case 9: return ActivityType.PLAYING;
            default: return ActivityType.UNKNOWN;
        }
    }
    
    /**
     * Détecte des caractéristiques visuelles spécifiques dans l'image,
     * comme posture, position des mains, objets présents, etc.
     * Cette méthode pourrait être utilisée comme alternative ou complément à la classification par deep learning.
     * 
     * @param frame Image à analyser
     * @return Map des caractéristiques visuelles détectées
     */
    public Map<String, Object> detectVisualFeatures(Mat frame) {
        // Cette méthode pourrait utiliser des techniques de vision par ordinateur classiques
        // pour détecter des caractéristiques spécifiques utiles à la classification d'activités
        
        // À implémenter si nécessaire
        
        return new HashMap<>();
    }
}
