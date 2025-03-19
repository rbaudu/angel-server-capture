package com.rbaudu.angel.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rbaudu.angel.config.AppConfig;
import com.rbaudu.angel.model.VideoFrame;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;

/**
 * Service responsable de la capture des flux vidéo.
 * Utilise JavaCV et OpenCV pour capturer les flux de la caméra.
 */
@Service
@Slf4j
public class VideoCaptureService {

    @Autowired
    private AppConfig config;
    
    @Autowired
    private MediaEventPublisher eventPublisher;
    
    private OpenCVFrameGrabber grabber;
    private OpenCVFrameConverter.ToMat converter;
    private Java2DFrameConverter java2dConverter;
    private AtomicBoolean running;
    private AtomicLong frameCounter;
    private Thread captureThread;
    private AtomicBoolean cameraAvailable;
    private int cameraRetryCount;
    private static final int MAX_RETRY_COUNT = 3;

    
    /**
     * Initialise le service de capture vidéo.
     */
    @PostConstruct
    public void init() {
        if (!config.isVideoEnabled()) {
            log.info("Capture vidéo désactivée");
            return;
        }
        
        try {
            log.info("Initialisation du service de capture vidéo...");
            grabber = new OpenCVFrameGrabber(config.getCameraIndex());
            grabber.setImageWidth(config.getVideoWidth());
            grabber.setImageHeight(config.getVideoHeight());
            grabber.setFrameRate(config.getVideoFps());
            converter = new OpenCVFrameConverter.ToMat();
            java2dConverter = new Java2DFrameConverter();
            running = new AtomicBoolean(false);
            cameraAvailable = new AtomicBoolean(true);
            cameraRetryCount = 0;
            frameCounter = new AtomicLong(0);
            log.info("Service de capture vidéo initialisé");
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation du service de capture vidéo", e);
            cameraAvailable.set(false);

        }
    }
    
    /**
     * Démarre la capture vidéo dans un thread séparé.
     */
    public void start() {
        if (!config.isVideoEnabled() || running.get() || !cameraAvailable.get()) {
            if (!cameraAvailable.get()) {
                log.warn("Démarrage de la vidéo impossible: caméra non disponible");
            }
            return;
        }
        
        try {
            log.info("Démarrage de la capture vidéo...");
            try {
                grabber.start();
                cameraRetryCount = 0;
            } catch (Exception e) {
                cameraRetryCount++;
                if (cameraRetryCount >= MAX_RETRY_COUNT) {
                    cameraAvailable.set(false);
                    log.error("La caméra n'est pas disponible après {} tentatives. Désactivation de la capture vidéo.", MAX_RETRY_COUNT);
                    return;
                }
                log.warn("Erreur lors du démarrage de la caméra (tentative {}/{}), nouvelle tentative...", 
                        cameraRetryCount, MAX_RETRY_COUNT);
                return;
            }
            
            running.set(true);
            
            captureThread = new Thread(() -> {
                captureLoop();
            }, "video-capture-thread");
            captureThread.setDaemon(true);
            captureThread.start();
            
            log.info("Capture vidéo démarrée");
        } catch (Exception e) {
            log.error("Erreur lors du démarrage de la capture vidéo", e);
        }
    }
    
    /**
     * Arrête la capture vidéo.
     */
    public void stop() {
        if (!running.get()) {
            return;
        }
        
        log.info("Arrêt de la capture vidéo...");
        running.set(false);
        
        try {
            if (captureThread != null) {
                captureThread.interrupt();
                captureThread.join(1000);
            }
            
            if (grabber != null) {
                grabber.stop();
            }
            
            log.info("Capture vidéo arrêtée");
        } catch (Exception e) {
            log.error("Erreur lors de l'arrêt de la capture vidéo", e);
        }
    }
    
    /**
     * Boucle principale de capture vidéo.
     */
    private void captureLoop() {
        int errorCount = 0;
        final int MAX_ERRORS = 10;
        
        try {
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    Frame frame = grabber.grab();
                    
                    if (frame != null && frame.image != null) {
                        errorCount = 0;  // Réinitialiser le compteur d'erreurs en cas de réussite
                        processFrame(frame);
                    }
                } catch (Exception e) {
                    errorCount++;
                    log.warn("Erreur de capture de trame: {} ({}/{})", e.getMessage(), errorCount, MAX_ERRORS);
                    
                    if (errorCount >= MAX_ERRORS) {
                        log.error("Trop d'erreurs consécutives de capture. Arrêt de la boucle.");
                        break;
                    }
                }
                
                // Respecter la cadence configurée
                TimeUnit.MILLISECONDS.sleep(1000 / config.getVideoFps());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("Thread de capture vidéo interrompu");
        } catch (Exception e) {
            log.error("Erreur dans la boucle de capture vidéo", e);
        } finally {
            running.set(false);
        }
    }
    
    /**
     * Traite une trame capturée et publie un événement.
     * 
     * @param frame trame capturée
     */
    private void processFrame(Frame frame) {
        try {
            // Convertir la trame en Mat pour le traitement OpenCV
            Mat mat = converter.convert(frame);
            
            // Convertir la trame en BufferedImage pour l'encodage
            BufferedImage bufferedImage = java2dConverter.convert(frame);
            
            // Encoder l'image en base64
            String imageData = encodeToBase64(bufferedImage, "jpg");
            
            // Créer l'objet VideoFrame
            VideoFrame videoFrame = VideoFrame.builder()
                    .imageData(imageData)
                    .timestamp(Instant.now())
                    .sequenceNumber(frameCounter.incrementAndGet())
                    .width(frame.imageWidth)
                    .height(frame.imageHeight)
                    .format("jpg")
                    .motionDetected(false) // Sera mis à jour par le service d'analyse
                    .personDetected(false) // Sera mis à jour par le service d'analyse
                    .build();
            
            // Publier l'événement
            eventPublisher.publishVideoFrame(videoFrame);
            
        } catch (Exception e) {
            log.error("Erreur lors du traitement de la trame vidéo", e);
        }
    }
    
    /**
     * Encode une image BufferedImage en chaîne base64.
     * 
     * @param image image à encoder
     * @param format format de l'image
     * @return image encodée en base64
     * @throws Exception en cas d'erreur d'encodage
     */
    private String encodeToBase64(BufferedImage image, String format) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
    
    /**
     * Indique si la caméra est disponible.
     * 
     * @return true si la caméra est disponible, false sinon
     */
    public boolean isCameraAvailable() {
        return cameraAvailable.get();
    }
    
    /**
     * Nettoyage des ressources avant la destruction du bean.
     */
    @PreDestroy
    public void cleanup() {
        stop();
        
        try {
            if (grabber != null) {
                grabber.release();
            }
        } catch (Exception e) {
            log.error("Erreur lors du nettoyage du service de capture vidéo", e);
        }
    }
}