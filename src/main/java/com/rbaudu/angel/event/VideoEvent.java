package com.rbaudu.angel.event;

import org.springframework.context.ApplicationEvent;

import com.rbaudu.angel.model.VideoFrame;

/**
 * Événement déclenché lorsqu'une nouvelle trame vidéo est capturée.
 */
public class VideoEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * La trame vidéo capturée.
     */
    private final VideoFrame videoFrame;
    
    /**
     * Crée un nouvel événement vidéo.
     * 
     * @param source la source de l'événement
     * @param videoFrame la trame vidéo capturée
     */
    public VideoEvent(Object source, VideoFrame videoFrame) {
        super(source);
        this.videoFrame = videoFrame;
    }
    
    /**
     * Récupère la trame vidéo associée à cet événement.
     * 
     * @return la trame vidéo
     */
    public VideoFrame getVideoFrame() {
        return videoFrame;
    }
}
