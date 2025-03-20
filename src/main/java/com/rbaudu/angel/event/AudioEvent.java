package com.rbaudu.angel.event;

import org.springframework.context.ApplicationEvent;

import com.rbaudu.angel.model.AudioChunk;

/**
 * Événement déclenché lorsqu'un nouveau segment audio est capturé.
 */
public class AudioEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Le segment audio capturé.
     */
    private final AudioChunk audioChunk;
    
    /**
     * Crée un nouvel événement audio.
     * 
     * @param source la source de l'événement
     * @param audioChunk le segment audio capturé
     */
    public AudioEvent(Object source, AudioChunk audioChunk) {
        super(source);
        this.audioChunk = audioChunk;
    }
    
    /**
     * Récupère le segment audio associé à cet événement.
     * 
     * @return le segment audio
     */
    public AudioChunk getAudioChunk() {
        return audioChunk;
    }
}