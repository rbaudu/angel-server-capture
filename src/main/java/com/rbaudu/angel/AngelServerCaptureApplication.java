package com.rbaudu.angel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principale pour démarrer l'application serveur de capture Angel.
 * Cette application capture, synchronise et analyse les flux vidéo et audio.
 * La phase 2 ajoute la reconnaissance de comportement à partir des activités détectées.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@ComponentScan({"com.rbaudu.angel", "com.rbaudu.angel.behavior"})
public class AngelServerCaptureApplication {

    /**
     * Point d'entrée principal de l'application.
     * 
     * @param args arguments de ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(AngelServerCaptureApplication.class, args);
    }

}