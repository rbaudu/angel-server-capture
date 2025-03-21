package com.rbaudu.angel.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.rbaudu.angel.config.AppConfig;
import com.rbaudu.angel.service.CaptureServiceManager;

/**
 * Contrôleur pour gérer les vues de l'interface utilisateur.
 */
@Controller
public class WebController {

    private static final Logger log = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private AppConfig config;
    
    @Autowired
    private CaptureServiceManager captureServiceManager;
    
    /**
     * Page d'accueil du serveur de capture.
     * 
     * @param model le modèle pour la vue
     * @param request la requête HTTP
     * @return nom de la vue à afficher
     */
    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        log.debug("Affichage de la page d'accueil");
        
        model.addAttribute("title", "Angel Server - Accueil");
        model.addAttribute("videoEnabled", config.isVideoEnabled());
        model.addAttribute("audioEnabled", config.isAudioEnabled());
        model.addAttribute("analysisEnabled", config.isAnalysisEnabled());
        model.addAttribute("isRunning", captureServiceManager.isRunning());
        model.addAttribute("httpServletRequest", request);        
        return "home";
    }
    
    /**
     * Page de visualisation en direct des flux.
     * 
     * @param model le modèle pour la vue
     * @param request la requête HTTP
     * @return nom de la vue à afficher
     */
    @GetMapping("/live")
    public String live(Model model, HttpServletRequest request) {
        log.debug("Affichage de la page de visualisation en direct");
        
        model.addAttribute("title", "Angel Server - Visualisation en direct");
        model.addAttribute("videoEnabled", config.isVideoEnabled());
        model.addAttribute("audioEnabled", config.isAudioEnabled());
        model.addAttribute("videoWidth", config.getVideoWidth());
        model.addAttribute("videoHeight", config.getVideoHeight());
        model.addAttribute("isRunning", captureServiceManager.isRunning());
        model.addAttribute("httpServletRequest", request);
       
        return "live";
    }
    
    /**
     * Page de configuration du serveur.
     * 
     * @param model le modèle pour la vue
     * @param request la requête HTTP
     * @return nom de la vue à afficher
     */
    @GetMapping("/config")
    public String config(Model model, HttpServletRequest request) {
        log.debug("Affichage de la page de configuration");
        
        model.addAttribute("title", "Angel Server - Configuration");
        model.addAttribute("config", config);
        model.addAttribute("httpServletRequest", request);        
        return "config";
    }
    
    /**
     * Page à propos du serveur.
     * 
     * @param model le modèle pour la vue
     * @param request la requête HTTP
     * @return nom de la vue à afficher
     */
    @GetMapping("/about")
    public String about(Model model, HttpServletRequest request) {
        log.debug("Affichage de la page à propos");
        
        model.addAttribute("title", "Angel Server - À propos");
        model.addAttribute("httpServletRequest", request);        
        return "about";
    }
}