package com.rbaudu.angel.behavior.event;

import com.rbaudu.angel.behavior.model.BehaviorResult;
import com.rbaudu.angel.behavior.model.BehaviorResultDto;

/**
 * Événement représentant un résultat d'analyse de comportement.
 */
public class BehaviorResultEvent {
    private final BehaviorResultDto result;
    
    /**
     * Constructeur.
     * 
     * @param result Résultat d'analyse de comportement
     */
    public BehaviorResultEvent(BehaviorResult result) {
        this.result = BehaviorResultDto.fromResult(result);
    }
    
    /**
     * Récupère le résultat d'analyse de comportement.
     * 
     * @return Résultat d'analyse
     */
    public BehaviorResultDto getResult() {
        return result;
    }
}