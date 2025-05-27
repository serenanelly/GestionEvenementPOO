package com.example.gestionevenement.evenement.modele.services;


/**
 * Interface représentant un observateur (abonné à un événement).
 * Principe DIP : dépend de l'abstraction.
 */
public interface ParticipantObserver {
    void notifier(String message);
}
