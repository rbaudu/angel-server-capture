package com.rbaudu.angel.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rbaudu.angel.config.AppConfig;
import com.rbaudu.angel.service.CaptureServiceManager;

/**
 * Contrôleur REST pour gérer les opérations de capture.
 */
@RestController
@RequestMapping("/api/capture")
public class CaptureController {

    private static final Logger log = LoggerFactory.getLogger(CaptureController.class);

    @Autowired
    private CaptureServiceManager captureServiceManager;
    
    @Autowired
    private AppConfig config;
    
    /**
     * Récupère l'état actuel des services de capture.
     * 
     * @return état des services
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        log.debug("Requête de statut des services de capture");
        
        Map<String, Object> status = new HashMap<>();
        status.put("running", captureServiceManager.isRunning());
        status.put("videoEnabled", config.isVideoEnabled());
        status.put("audioEnabled", config.isAudioEnabled());
        status.put("analysisEnabled", config.isAnalysisEnabled());
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * Démarre les services de capture.
     * 
     * @return résultat de l'opération
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startCapture() {
        log.info("Demande de démarrage des services de capture");
        
        boolean success = captureServiceManager.startServices();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("running", captureServiceManager.isRunning());
        result.put("message", success ? "Services démarrés avec succès" : "Échec du démarrage des services");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Arrête les services de capture.
     * 
     * @return résultat de l'opération
     */
    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stopCapture() {
        log.info("Demande d'arrêt des services de capture");
        
        captureServiceManager.stopServices();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("running", captureServiceManager.isRunning());
        result.put("message", "Services arrêtés avec succès");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Redémarre les services de capture.
     * 
     * @return résultat de l'opération
     */
    @PostMapping("/restart")
    public ResponseEntity<Map<String, Object>> restartCapture() {
        log.info("Demande de redémarrage des services de capture");
        
        boolean success = captureServiceManager.restartServices();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("running", captureServiceManager.isRunning());
        result.put("message", success ? "Services redémarrés avec succès" : "Échec du redémarrage des services");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Récupère la configuration actuelle de la capture.
     * 
     * @return configuration de capture
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        log.debug("Requête de configuration de capture");
        
        Map<String, Object> configMap = new HashMap<>();
        
        // Configuration vidéo
        Map<String, Object> videoConfig = new HashMap<>();
        videoConfig.put("enabled", config.isVideoEnabled());
        videoConfig.put("cameraIndex", config.getCameraIndex());
        videoConfig.put("width", config.getVideoWidth());
        videoConfig.put("height", config.getVideoHeight());
        videoConfig.put("fps", config.getVideoFps());
        configMap.put("video", videoConfig);
        
        // Configuration audio
        Map<String, Object> audioConfig = new HashMap<>();
        audioConfig.put("enabled", config.isAudioEnabled());
        audioConfig.put("deviceIndex", config.getAudioDeviceIndex());
        audioConfig.put("sampleRate", config.getAudioSampleRate());
        audioConfig.put("channels", config.getAudioChannels());
        configMap.put("audio", audioConfig);
        
        // Configuration de synchronisation
        Map<String, Object> syncConfig = new HashMap<>();
        syncConfig.put("bufferSize", config.getSyncBufferSize());
        syncConfig.put("maxDelayMs", config.getSyncMaxDelayMs());
        configMap.put("sync", syncConfig);
        
        // Configuration d'analyse
        Map<String, Object> analysisConfig = new HashMap<>();
        analysisConfig.put("enabled", config.isAnalysisEnabled());
        analysisConfig.put("motionDetection", config.isMotionDetectionEnabled());
        analysisConfig.put("personDetection", config.isPersonDetectionEnabled());
        analysisConfig.put("positionTracking", config.isPositionTrackingEnabled());
        configMap.put("analysis", analysisConfig);
        
        return ResponseEntity.ok(configMap);
    }
}