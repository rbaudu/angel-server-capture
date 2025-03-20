package com.rbaudu.angel.behavior.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbaudu.angel.behavior.config.BehaviorConfig;
import com.rbaudu.angel.behavior.model.BehaviorPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsable du chargement et de la gestion des patterns de comportement.
 */
@Service
public class PatternRepository {
    private static final Logger logger = LoggerFactory.getLogger(PatternRepository.class);
    
    private final BehaviorConfig config;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    
    // Cache des patterns chargés
    private final ConcurrentHashMap<String, BehaviorPattern> patternsById = new ConcurrentHashMap<>();
    
    /**
     * Constructeur avec injection de dépendances.
     */
    public PatternRepository(BehaviorConfig config, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.config = config;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Initialisation après construction du bean.
     * Charge les patterns depuis le fichier de configuration.
     */
    @PostConstruct
    public void init() {
        try {
            loadPatterns();
            logger.info("Patterns de comportement chargés avec succès: {} patterns", patternsById.size());
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des patterns de comportement", e);
        }
    }
    
    /**
     * Charge les patterns depuis le fichier de configuration.
     */
    private void loadPatterns() throws IOException {
        Resource resource = resourceLoader.getResource(config.getPatternsDefinitionPath());
        
        if (!resource.exists()) {
            logger.warn("Fichier de définition des patterns non trouvé: {}", config.getPatternsDefinitionPath());
            return;
        }
        
        try (InputStream is = resource.getInputStream()) {
            List<BehaviorPattern> patterns = objectMapper.readValue(is, new TypeReference<List<BehaviorPattern>>() {});
            
            // Stocker les patterns dans le cache
            patterns.forEach(pattern -> patternsById.put(pattern.getId(), pattern));
            logger.debug("Chargement de {} patterns de comportement", patterns.size());
        }
    }
    
    /**
     * Récupère tous les patterns de comportement.
     * 
     * @return Liste de tous les patterns
     */
    public List<BehaviorPattern> getAllPatterns() {
        return new ArrayList<>(patternsById.values());
    }
    
    /**
     * Récupère un pattern par son identifiant.
     * 
     * @param id Identifiant du pattern
     * @return Pattern correspondant ou null si non trouvé
     */
    public BehaviorPattern getPatternById(String id) {
        return patternsById.get(id);
    }
    
    /**
     * Ajoute ou met à jour un pattern dans le référentiel.
     * 
     * @param pattern Pattern à ajouter ou mettre à jour
     * @return Pattern mis à jour
     */
    public BehaviorPattern savePattern(BehaviorPattern pattern) {
        patternsById.put(pattern.getId(), pattern);
        return pattern;
    }
    
    /**
     * Supprime un pattern du référentiel.
     * 
     * @param id Identifiant du pattern à supprimer
     * @return true si le pattern a été supprimé, false sinon
     */
    public boolean deletePattern(String id) {
        return patternsById.remove(id) != null;
    }
    
    /**
     * Recharge les patterns depuis le fichier de configuration.
     * 
     * @return Nombre de patterns chargés
     */
    public int reloadPatterns() {
        patternsById.clear();
        try {
            loadPatterns();
            return patternsById.size();
        } catch (Exception e) {
            logger.error("Erreur lors du rechargement des patterns", e);
            return 0;
        }
    }
}