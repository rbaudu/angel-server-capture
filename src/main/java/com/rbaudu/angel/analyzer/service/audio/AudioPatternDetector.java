package com.rbaudu.angel.analyzer.service.audio;

import com.rbaudu.angel.analyzer.config.AnalyzerConfig;
import com.rbaudu.angel.analyzer.model.ActivityType;
import com.rbaudu.angel.analyzer.util.AudioUtils;
import com.rbaudu.angel.analyzer.util.ModelLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import jakarta.annotation.PostConstruct;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Service de détection de patterns audio pour l'identification d'activités.
 */
@Service
public class AudioPatternDetector {
    private static final Logger logger = LoggerFactory.getLogger(AudioPatternDetector.class);
    
    private final ModelLoader modelLoader;
    private final AudioUtils audioUtils;
    private final AnalyzerConfig config;
    
    private SavedModelBundle model;
    
    /**
     * Constructeur avec injection de dépendances.
     * @param modelLoader Chargeur de modèle TensorFlow
     * @param audioUtils Utilitaires audio
     * @param config Configuration de l'analyseur
     */
    public AudioPatternDetector(ModelLoader modelLoader, AudioUtils audioUtils, AnalyzerConfig config) {
        this.modelLoader = modelLoader;
        this.audioUtils = audioUtils;
        this.config = config;
    }
    
    /**
     * Initialisation du modèle après construction du bean.
     */
    @PostConstruct
    public void init() {
        if (!config.isAudioAnalysisEnabled()) {
            logger.info("Analyse audio désactivée dans la configuration");
            return;
        }
        
        try {
            if (config.getAudioClassificationModel() != null) {
                this.model = modelLoader.loadModel(config.getAudioClassificationModel());
                logger.info("Modèle de classification audio chargé avec succès");
            } else {
                logger.warn("Aucun modèle de classification audio configuré");
            }
        } catch (Exception e) {
            logger.error("Erreur lors du chargement du modèle de classification audio", e);
        }
    }
    
    /**
     * Détecte des patterns audio pour identifier les activités.
     * @param audioStream Flux audio à analyser
     * @return Map des types d'activités avec leur score de confiance
     */
    public Map<ActivityType, Double> detectAudioPatterns(AudioInputStream audioStream) {
        if (!config.isAudioAnalysisEnabled() || model == null) {
            logger.warn("Détection de patterns audio impossible : désactivée ou modèle non chargé");
            return new HashMap<>();
        }
        
        try {
            // Standardisation du format audio
            AudioFormat targetFormat = new AudioFormat(
                    config.getAudioSampleRate(),
                    16,    // bits par échantillon
                    1,     // mono
                    true,  // signé
                    false  // little endian
            );
            
            // Conversion du format si nécessaire
            AudioInputStream standardizedStream = audioUtils.convertAudioFormat(audioStream, targetFormat);
            
            // Extraction des caractéristiques MFCC
            byte[] audioData = new byte[(int) standardizedStream.getFrameLength() * targetFormat.getFrameSize()];
            standardizedStream.read(audioData);
            
            float[] mfcc = audioUtils.extractMFCC(audioData, targetFormat.getSampleRate(), 13);
            
            // Conversion en Tensor en utilisant les API modernes de TensorFlow
            Tensor featureTensor = audioUtils.audioToTensor(mfcc);
            
            // Exécution de l'inférence
            Session.Runner runner = model.session().runner()
                    .feed("input", featureTensor)
                    .fetch("output");
            
            Tensor resultTensor = runner.run().get(0);
            
            // Extraire les résultats du tensor
            int numClasses = 5; // Supposons 5 types de sons identifiables
            FloatBuffer resultBuffer = FloatBuffer.allocate(numClasses);
            resultTensor.asRawTensor().data().read(resultBuffer);
            resultBuffer.rewind();
            
            // Convertir les résultats en tableau
            float[] audioClasses = new float[numClasses];
            resultBuffer.get(audioClasses);
            
            // Conversion en Map d'activités
            Map<ActivityType, Double> result = new HashMap<>();
            mapAudioClassesToActivities(audioClasses, result);
            
            logger.debug("Patterns audio détectés: {}", result);
            return result;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la détection de patterns audio", e);
            return new HashMap<>();
        }
    }
    
    /**
     * Mappe les classes audio vers des types d'activités.
     * @param audioClasses Probabilités des classes audio
     * @param activities Map des activités à remplir
     */
    private void mapAudioClassesToActivities(float[] audioClasses, Map<ActivityType, Double> activities) {
        // Mapping des classes audio vers des activités
        // Les valeurs exactes dépendront du modèle utilisé
        
        // Classe 0 = Silence/Bruit de fond -> PRESENT_INACTIVE ou SLEEPING
        if (audioClasses[0] > 0.7) {
            activities.put(ActivityType.PRESENT_INACTIVE, (double) audioClasses[0]);
            activities.put(ActivityType.SLEEPING, (double) audioClasses[0] * 0.8);
        }
        
        // Classe 1 = Voix humaine -> TALKING ou CALLING
        if (audioClasses[1] > 0.5) {
            activities.put(ActivityType.TALKING, (double) audioClasses[1]);
            activities.put(ActivityType.CALLING, (double) audioClasses[1] * 0.7);
        }
        
        // Classe 2 = Sons TV/Radio -> WATCHING_TV
        if (audioClasses[2] > 0.5) {
            activities.put(ActivityType.WATCHING_TV, (double) audioClasses[2]);
        }
        
        // Classe 3 = Sons de cuisine/vaisselle -> EATING
        if (audioClasses[3] > 0.5) {
            activities.put(ActivityType.EATING, (double) audioClasses[3]);
        }
        
        // Classe 4 = Sons de nettoyage -> CLEANING
        if (audioClasses[4] > 0.5) {
            activities.put(ActivityType.CLEANING, (double) audioClasses[4]);
        }
    }
    
    /**
     * Détecte les caractéristiques audio basiques comme le niveau sonore,
     * la présence de voix, la présence de musique, etc.
     * Cette méthode pourrait être utilisée comme complément ou solution de secours
     * si le modèle de deep learning n'est pas disponible.
     * 
     * @param audioStream Flux audio à analyser
     * @return Map des caractéristiques audio détectées
     */
    public Map<String, Object> detectBasicAudioFeatures(AudioInputStream audioStream) {
        Map<String, Object> features = new HashMap<>();
        
        try {
            // Lecture des données audio
            byte[] audioData = new byte[(int) audioStream.getFrameLength() * audioStream.getFormat().getFrameSize()];
            audioStream.read(audioData);
            
            // Conversion en échantillons flottants
            float[] samples = audioUtils.pcmToFloat(audioData);
            
            // Calcul du niveau sonore (RMS)
            double rms = calculateRMS(samples);
            features.put("soundLevel", rms);
            
            // Détection de parole (simplifiée)
            boolean speechDetected = detectSpeech(samples, audioStream.getFormat().getSampleRate());
            features.put("speechDetected", speechDetected);
            
            // Autres caractéristiques à implémenter selon les besoins
            
            return features;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la détection des caractéristiques audio basiques", e);
            return features;
        }
    }
    
    /**
     * Calcule le niveau sonore (Root Mean Square) d'un signal audio.
     * @param samples Échantillons audio
     * @return Valeur RMS
     */
    private double calculateRMS(float[] samples) {
        double sum = 0.0;
        for (float sample : samples) {
            sum += sample * sample;
        }
        return Math.sqrt(sum / samples.length);
    }
    
    /**
     * Détecte la présence de parole dans un signal audio (implémentation simplifiée).
     * Une implémentation réelle utiliserait des techniques de traitement du signal plus avancées.
     * 
     * @param samples Échantillons audio
     * @param sampleRate Taux d'échantillonnage
     * @return true si de la parole est détectée, false sinon
     */
    private boolean detectSpeech(float[] samples, float sampleRate) {
        // Une implémentation simplifiée basée sur l'énergie et le taux de passage par zéro
        
        // Calcul de l'énergie
        double energy = calculateRMS(samples);
        
        // Calcul du taux de passage par zéro
        int zeroCrossings = 0;
        for (int i = 1; i < samples.length; i++) {
            if ((samples[i] > 0 && samples[i-1] <= 0) || 
                (samples[i] < 0 && samples[i-1] >= 0)) {
                zeroCrossings++;
            }
        }
        double zeroCrossingRate = zeroCrossings / (double) samples.length;
        
        // Heuristique simplifiée : la parole a généralement une énergie modérée
        // et un taux de passage par zéro dans une certaine plage
        return energy > 0.01 && energy < 0.2 && 
               zeroCrossingRate > 0.01 && zeroCrossingRate < 0.1;
    }
}