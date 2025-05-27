package com.example.gestionevenement.evenement.modele.services;


/**
 * Interface de service pour envoyer des notifications.
 * Respecte ISP : interface petite, claire et spécifique.
 */
public interface NotificationService {
    void envoyerNotification(String message);
}

