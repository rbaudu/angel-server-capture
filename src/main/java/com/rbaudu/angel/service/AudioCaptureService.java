package com.rbaudu.angel.service;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rbaudu.angel.config.AppConfig;
import com.rbaudu.angel.model.AudioChunk;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsable de la capture des flux audio.
 * Utilise JavaSound et FFmpeg pour capturer l'audio du système.
 */
@Service
@Slf4j
public class AudioCaptureService {

    @Autowired
    private AppConfig config;
    
    @Autowired
    private MediaEventPublisher eventPublisher;
    
    private TargetDataLine line;
    private AudioFormat format;
    private AtomicBoolean running;
    private AtomicLong chunkCounter;
    private Thread captureThread;
    private FFmpegFrameRecorder recorder;
    private ByteBuffer audioBuffer;
    private int bufferSize;
    private int chunkDurationMs = 100; // Durée d'un segment audio en ms
    
    /**
     * Initialise le service de capture audio.
     */
    @PostConstruct
    public void init() {
        if (!config.isAudioEnabled()) {
            log.info("Capture audio désactivée");
            return;
        }
        
        try {
            log.info("Initialisation du service de capture audio...");
            
            // Configurer le format audio
            format = new AudioFormat(
                    config.getAudioSampleRate(), // Fréquence d'échantillonnage
                    16, // Bits par échantillon
                    config.getAudioChannels(), // Nombre de canaux
                    true, // Signé
                    false // Little Endian
            );
            
            // Calculer la taille du buffer en fonction de la durée du chunk
            int bytesPerSample = format.getSampleSizeInBits() / 8;
            int bytesPerFrame = bytesPerSample * format.getChannels();
            int bytesPerSecond = bytesPerFrame * (int)format.getSampleRate();
            bufferSize = (bytesPerSecond * chunkDurationMs) / 1000;
            
            // Initialiser les variables d'état
            running = new AtomicBoolean(false);
            chunkCounter = new AtomicLong(0);
            audioBuffer = ByteBuffer.allocate(bufferSize);
            
            log.info("Service de capture audio initialisé");
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation du service de capture audio", e);
        }
    }
    
    /**
     * Démarre la capture audio dans un thread séparé.
     */
    public void start() {
        if (!config.isAudioEnabled() || running.get()) {
            return;
        }
        
        try {
            log.info("Démarrage de la capture audio...");
            
            // Obtenir la ligne audio d'entrée
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, bufferSize);
            line.start();
            
            // Configurer le recorder FFmpeg pour encoder l'audio
            recorder = new FFmpegFrameRecorder("dummy.wav", config.getAudioChannels());
            recorder.setAudioOption("crf", "0");
            recorder.setAudioQuality(0);
            recorder.setAudioBitrate(192000);
            recorder.setSampleRate(config.getAudioSampleRate());
            recorder.setAudioChannels(config.getAudioChannels());
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
            recorder.start();
            
            running.set(true);
            
            captureThread = new Thread(() -> {
                captureLoop();
            }, "audio-capture-thread");
            captureThread.setDaemon(true);
            captureThread.start();
            
            log.info("Capture audio démarrée");
        } catch (Exception e) {
            log.error("Erreur lors du démarrage de la capture audio", e);
        }
    }
    
    /**
     * Arrête la capture audio.
     */
    public void stop() {
        if (!running.get()) {
            return;
        }
        
        log.info("Arrêt de la capture audio...");
        running.set(false);
        
        try {
            if (captureThread != null) {
                captureThread.interrupt();
                captureThread.join(1000);
            }
            
            if (line != null) {
                line.stop();
                line.close();
            }
            
            if (recorder != null) {
                recorder.stop();
                recorder.release();
            }
            
            log.info("Capture audio arrêtée");
        } catch (Exception e) {
            log.error("Erreur lors de l'arrêt de la capture audio", e);
        }
    }
    
    /**
     * Boucle principale de capture audio.
     */
    private void captureLoop() {
        try {
            byte[] buffer = new byte[bufferSize];
            
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                
                if (bytesRead > 0) {
                    processAudioChunk(buffer, bytesRead);
                }
                
                // Pause courte pour éviter de surcharger le CPU
                TimeUnit.MILLISECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("Thread de capture audio interrompu");
        } catch (Exception e) {
            log.error("Erreur dans la boucle de capture audio", e);
        }
    }
    
    /**
     * Traite un segment audio capturé et publie un événement.
     * 
     * @param buffer buffer audio
     * @param bytesRead nombre d'octets lus
     */
    private void processAudioChunk(byte[] buffer, int bytesRead) {
        try {
            // Convertir les octets en échantillons audio (16 bits signés)
            ShortBuffer shortBuffer = ByteBuffer.wrap(buffer, 0, bytesRead).asShortBuffer();
            short[] samples = new short[shortBuffer.limit()];
            shortBuffer.get(samples);
            
            // Calculer le niveau sonore moyen et maximal
            double maxLevel = 0;
            double sumLevel = 0;
            double scale = 32768.0; // Échelle pour normaliser les échantillons 16 bits
            
            for (short sample : samples) {
                double normalizedSample = Math.abs(sample) / scale;
                sumLevel += normalizedSample;
                maxLevel = Math.max(maxLevel, normalizedSample);
            }
            
            double avgLevel = sumLevel / samples.length;
            
            // Convertir en dB
            double maxLevelDb = 20 * Math.log10(maxLevel);
            double avgLevelDb = 20 * Math.log10(avgLevel);
            
            // Encoder l'audio avec FFmpeg
            ByteBuffer encodedBuffer = ByteBuffer.allocate(bytesRead);
            encodedBuffer.put(buffer, 0, bytesRead);
            encodedBuffer.flip();
            recorder.recordSamples(config.getAudioSampleRate(), config.getAudioChannels(), encodedBuffer);
            
            // Créer l'objet AudioChunk
            AudioChunk audioChunk = AudioChunk.builder()
                    .audioData(Base64.getEncoder().encodeToString(buffer))
                    .timestamp(Instant.now())
                    .sequenceNumber(chunkCounter.incrementAndGet())
                    .sampleRate(config.getAudioSampleRate())
                    .channels(config.getAudioChannels())
                    .format("PCM")
                    .durationMs(chunkDurationMs)
                    .maxSoundLevel(maxLevelDb)
                    .avgSoundLevel(avgLevelDb)
                    .soundDetected(maxLevelDb > -30) // Seuil de détection de son
                    .build();
            
            // Publier l'événement
            eventPublisher.publishAudioChunk(audioChunk);
            
        } catch (Exception e) {
            log.error("Erreur lors du traitement du segment audio", e);
        }
    }
    
    /**
     * Nettoyage des ressources avant la destruction du bean.
     */
    @PreDestroy
    public void cleanup() {
        stop();
    }
}