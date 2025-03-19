package com.rbaudu.angel.event;

import org.springframework.context.ApplicationEvent;

import com.rbaudu.angel.model.SynchronizedMedia;

import lombok.Getter;

/**
 * Événement déclenché lorsqu'un média synchronisé est créé.
 */
@Getter
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
}
