package com.rbaudu.angel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.Getter;

/**
 * Configuration générale de l'application.
 * Cette classe centralise la configuration des différents composants et paramètres.
 */
@Configuration
@Getter
public class AppConfig implements WebMvcConfigurer {

    // Configuration de la capture vidéo
    @Value("${angel.capture.video.enabled:true}")
    private boolean videoEnabled;

    @Value("${angel.capture.video.camera-index:0}")
    private int cameraIndex;

    @Value("${angel.capture.video.width:640}")
    private int videoWidth;

    @Value("${angel.capture.video.height:480}")
    private int videoHeight;

    @Value("${angel.capture.video.fps:30}")
    private int videoFps;

    // Configuration de la capture audio
    @Value("${angel.capture.audio.enabled:true}")
    private boolean audioEnabled;

    @Value("${angel.capture.audio.device-index:0}")
    private int audioDeviceIndex;

    @Value("${angel.capture.audio.sample-rate:44100}")
    private int audioSampleRate;

    @Value("${angel.capture.audio.channels:2}")
    private int audioChannels;

    // Configuration de la synchronisation
    @Value("${angel.sync.buffer-size:10}")
    private int syncBufferSize;

    @Value("${angel.sync.max-delay-ms:100}")
    private int syncMaxDelayMs;

    // Configuration de l'analyse
    @Value("${angel.analysis.enabled:true}")
    private boolean analysisEnabled;

    @Value("${angel.analysis.motion-detection:true}")
    private boolean motionDetectionEnabled;

    @Value("${angel.analysis.person-detection:false}")
    private boolean personDetectionEnabled;

    @Value("${angel.analysis.position-tracking:false}")
    private boolean positionTrackingEnabled;

    /**
     * Configure le pool de threads pour les tâches asynchrones.
     * 
     * @return ThreadPoolTaskExecutor configuré
     */
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("angel-async-");
        executor.initialize();
        return executor;
    }

    /**
     * Configure le support asynchrone pour les contrôleurs.
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(taskExecutor());
        configurer.setDefaultTimeout(30000);
    }

    /**
     * Configure les gestionnaires de ressources statiques.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
