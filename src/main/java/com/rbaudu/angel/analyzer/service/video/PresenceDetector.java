package com.rbaudu.angel.analyzer.service.video;

import com.rbaudu.angel.analyzer.config.AnalyzerConfig;
import com.rbaudu.angel.analyzer.util.ModelLoader;
import com.rbaudu.angel.analyzer.util.VideoUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_objdetect.HOGDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

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
    private List<String> personClasses = Arrays.asList("person");
    
    /**
     * Constructeur avec injection de dépendances.
     */
    @Autowired
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
            String modelPath = config.getHumanDetectionModel();
            if (modelPath != null && !modelPath.isEmpty()) {
                logger.info("Chargement du modèle de détection de présence humaine: {}", modelPath);
                this.model = modelLoader.loadModel(modelPath);
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
            // Redimensionner et prétraiter l'image
            Mat processedFrame = videoUtils.resizeFrame(frame, 320, 320);
            Tensor imageTensor = videoUtils.prepareImageForModel(processedFrame, 320, 320);
            
            // Exécuter l'inférence
            Session.Runner runner = model.session().runner()
                    .feed("serving_default_input_tensor", imageTensor)
                    .fetch("StatefulPartitionedCall");
            
            Tensor resultTensor = runner.run().get(0);
            
            // Créer un buffer pour recevoir les résultats
            // Format typique: [batch, num_detections, [y1, x1, y2, x2, score, class, ?]]
            float[][][] result = new float[1][100][7]; 
            
            // Copier les résultats du tensor dans le buffer
            FloatBuffer resultBuffer = FloatBuffer.allocate(1 * 100 * 7);
            resultTensor.asRawTensor().data().read(resultBuffer);
            resultBuffer.rewind();
            
            // Extraire les résultats du buffer
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 7; j++) {
                    result[0][i][j] = resultBuffer.get();
                }
            }
            
            // Chercher les détections de personnes
            for (int i = 0; i < 100; i++) {
                float score = result[0][i][4];
                int classId = (int) result[0][i][5];
                
                // Classe 1 pour "personne" dans COCO
                if (classId == 1 && score > config.getPresenceThreshold()) {
                    logger.debug("Personne détectée avec un score de {}", score);
                    return true;
                }
            }
            
            logger.debug("Aucune personne détectée");
            return false;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la détection de présence", e);
            return false;
        }
    }
    
    /**
     * Alternative basée sur OpenCV pour la détection de personnes.
     * Utilisé comme solution de secours si TensorFlow ne fonctionne pas.
     */
    public boolean detectPersonWithHOG(Mat frame) {
        try {
            HOGDescriptor hog = new HOGDescriptor();
            
            // Utiliser une matrice pour les descripteurs HOG au lieu de FloatPointer
            Mat hogDescriptors = new Mat();
            HOGDescriptor.getDefaultPeopleDetector().copyTo(hogDescriptors);
            hog.setSVMDetector(hogDescriptors);
            
            // Remplacer DoubleVector par un format compatible
            MatVector foundLocations = new MatVector();
            Mat weights = new Mat();
            
            // Redimensionner pour de meilleures performances
            Mat resizedFrame = videoUtils.resizeFrame(frame, 640, 480);
            
            hog.detectMultiScale(resizedFrame, foundLocations, weights);
            
            return foundLocations.size() > 0;
        } catch (Exception e) {
            logger.error("Erreur lors de la détection de personne avec HOG", e);
            return false;
        }
    }
}