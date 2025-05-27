package com.example.gestionevenement.evenement.exceptions;

/**
 * Levée lorsqu’on tente d’ajouter un événement portant un ID
 * déjà présent dans la collection d’événements.
 */
public class EvenementDejaExistantException extends RuntimeException {

    public EvenementDejaExistantException(String message) {
        super(message);
    }
}
