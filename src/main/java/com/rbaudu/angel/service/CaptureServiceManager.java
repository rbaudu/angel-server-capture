package com.rbaudu.angel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * Service gestionnaire qui coordonne le démarrage et l'arrêt des services de capture.
 */
@Service
@Slf4j
public class CaptureServiceManager {

    @Autowired
    private VideoCaptureService videoCaptureService;
    
    @Autowired
    private AudioCaptureService audioCaptureService;
    
    private boolean servicesStarted = false;
    
    /**
     * Démarre automatiquement les services de capture lorsque l'application est prête.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application prête, démarrage des services de capture...");
        startServices();
    }
    
    /**
     * Démarre les services de capture vidéo et audio.
     * 
     * @return true si les services ont été démarrés avec succès
     */
    public synchronized boolean startServices() {
        if (servicesStarted) {
            log.info("Les services sont déjà en cours d'exécution");
            return true;
        }
        
        try {
            log.info("Démarrage des services de capture...");
            
            // Démarrer le service de capture vidéo
            videoCaptureService.start();
            
            // Démarrer le service de capture audio
            audioCaptureService.start();
            
            servicesStarted = true;
            log.info("Services de capture démarrés avec succès");
            return true;
        } catch (Exception e) {
            log.error("Erreur lors du démarrage des services de capture", e);
            stopServices();
            return false;
        }
    }
    
    /**
     * Arrête les services de capture vidéo et audio.
     */
    public synchronized void stopServices() {
        if (!servicesStarted) {
            return;
        }
        
        try {
            log.info("Arrêt des services de capture...");
            
            // Arrêter le service de capture vidéo
            videoCaptureService.stop();
            
            // Arrêter le service de capture audio
            audioCaptureService.stop();
            
            servicesStarted = false;
            log.info("Services de capture arrêtés avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de l'arrêt des services de capture", e);
        }
    }
    
    /**
     * Redémarre les services de capture.
     * 
     * @return true si les services ont été redémarrés avec succès
     */
    public synchronized boolean restartServices() {
        log.info("Redémarrage des services de capture...");
        stopServices();
        return startServices();
    }
    
    /**
     * Vérifie si les services sont en cours d'exécution.
     * 
     * @return true si les services sont démarrés
     */
    public boolean isRunning() {
        return servicesStarted;
    }
    
    /**
     * Nettoie les ressources avant la destruction du bean.
     */
    @PreDestroy
    public void cleanup() {
        stopServices();
    }
}
