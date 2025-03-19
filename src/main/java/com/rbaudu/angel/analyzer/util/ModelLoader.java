package com.rbaudu.angel.analyzer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.tensorflow.SavedModelBundle;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utilitaire pour charger les modèles TensorFlow.
 */
@Component
public class ModelLoader {
    private static final Logger logger = LoggerFactory.getLogger(ModelLoader.class);
    private final ResourceLoader resourceLoader;
    
    /**
     * Constructeur avec injection du ResourceLoader de Spring
     * @param resourceLoader Le ResourceLoader de Spring injecté automatiquement
     */
    public ModelLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    /**
     * Charge un modèle TensorFlow SavedModel à partir d'un chemin
     * @param modelPath Chemin vers le répertoire du modèle (relatif aux ressources ou absolu)
     * @return Le modèle TensorFlow chargé
     * @throws RuntimeException Si le chargement échoue
     */
    public SavedModelBundle loadModel(String modelPath) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + modelPath);
            Path path = Paths.get(resource.getURI());
            logger.info("Chargement du modèle depuis: {}", path);
            return SavedModelBundle.load(path.toString(), "serve");
        } catch (IOException e) {
            logger.error("Erreur lors du chargement du modèle: {}", e.getMessage(), e);
            throw new RuntimeException("Échec du chargement du modèle TensorFlow", e);
        }
    }
}
