package com.rbaudu.angel.event;

import org.springframework.context.ApplicationEvent;

import com.rbaudu.angel.model.SynchronizedMedia;

/**
 * Événement déclenché lorsqu'un média synchronisé est créé.
 */
public class SynchronizedMediaEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Le média synchronisé.
     */
    private final SynchronizedMedia synchronizedMedia;
    
    /**
     * Crée un nouvel événement de média synchronisé.
     * 
     * @param source la source de l'événement
     * @param synchronizedMedia le média synchronisé
     */
    public SynchronizedMediaEvent(Object source, SynchronizedMedia synchronizedMedia) {
        super(source);
        this.synchronizedMedia = synchronizedMedia;
    }
    
    /**
     * Récupère le média synchronisé associé à cet événement.
     * 
     * @return le média synchronisé
     */
    public SynchronizedMedia getSynchronizedMedia() {
        return synchronizedMedia;
    }
}
