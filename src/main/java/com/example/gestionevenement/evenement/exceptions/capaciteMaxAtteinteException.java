package com.example.gestionevenement.evenement.exceptions;
  
/**
 * Levée lorsqu’on essaie d’inscrire un participant alors que la capacité
 * maximale de l’événement est déjà atteinte.
 */
public class CapaciteMaxAtteinteException extends RuntimeException {

    public CapaciteMaxAtteinteException(String message) {
        super(message);
    }
}
