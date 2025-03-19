package com.rbaudu.angel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.rbaudu.angel.config.AppConfig;
import com.rbaudu.angel.service.CaptureServiceManager;

import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour gérer les vues de l'interface utilisateur.
 */
@Controller
@Slf4j
public class WebController {

    @Autowired
    private AppConfig config;
    
    @Autowired
    private CaptureServiceManager captureServiceManager;
    
    /**
     * Page d'accueil du serveur de capture.
     * 
     * @param model le modèle pour la vue
     * @return nom de la vue à afficher
     */
    @GetMapping("/")
    public String home(Model model) {
        log.debug("Affichage de la page d'accueil");
        
        model.addAttribute("title", "Angel Server - Accueil");
        model.addAttribute("videoEnabled", config.isVideoEnabled());
        model.addAttribute("audioEnabled", config.isAudioEnabled());
        model.addAttribute("analysisEnabled", config.isAnalysisEnabled());
        model.addAttribute("isRunning", captureServiceManager.isRunning());
        
        return "home";
    }
    
    /**
     * Page de visualisation en direct des flux.
     * 
     * @param model le modèle pour la vue
     * @return nom de la vue à afficher
     */
    @GetMapping("/live")
    public String live(Model model) {
        log.debug("Affichage de la page de visualisation en direct");
        
        model.addAttribute("title", "Angel Server - Visualisation en direct");
        model.addAttribute("videoEnabled", config.isVideoEnabled());
        model.addAttribute("audioEnabled", config.isAudioEnabled());
        model.addAttribute("videoWidth", config.getVideoWidth());
        model.addAttribute("videoHeight", config.getVideoHeight());
        model.addAttribute("isRunning", captureServiceManager.isRunning());
        
        return "live";
    }
    
    /**
     * Page de configuration du serveur.
     * 
     * @param model le modèle pour la vue
     * @return nom de la vue à afficher
     */
    @GetMapping("/config")
    public String config(Model model) {
        log.debug("Affichage de la page de configuration");
        
        model.addAttribute("title", "Angel Server - Configuration");
        model.addAttribute("config", config);
        
        return "config";
    }
    
    /**
     * Page à propos du serveur.
     * 
     * @param model le modèle pour la vue
     * @return nom de la vue à afficher
     */
    @GetMapping("/about")
    public String about(Model model) {
        log.debug("Affichage de la page à propos");
        
        model.addAttribute("title", "Angel Server - À propos");
        
        return "about";
    }
}