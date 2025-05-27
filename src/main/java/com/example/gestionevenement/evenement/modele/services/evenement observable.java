package com.example.gestionevenement.evenement.modele.services;


import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite qui représente un événement observable.
 * Elle permet d'ajouter, retirer, et notifier des observateurs.
 */
public abstract class EvenementObservable {
    private final List<ParticipantObserver> observateurs = new ArrayList<>();

    /**
     * Ajoute un participant à la liste des observateurs.
     */
    public void ajouterObservateur(ParticipantObserver observer) {
        observateurs.add(observer);
    }

    /**
     * Retire un participant de la liste des observateurs.
     */
    public void retirerObservateur(ParticipantObserver observer) {
        observateurs.remove(observer);
    }

    /**
     * Notifie tous les observateurs avec un message.
     */
    public void notifierObservateurs(String message) {
        for (ParticipantObserver observer : observateurs) {
            observer.notifier(message);
        }
    }
}
