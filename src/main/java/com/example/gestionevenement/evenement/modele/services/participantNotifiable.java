package com.example.gestionevenement.evenement.modele.services;
import com.example.gestionevenement.evenement.modele.services;

/**
 * Adaptateur qui rend un Participant observable via une notification.
 * Respecte SRP : séparé de la logique métier de Participant.
 */
public class ParticipantNotifiable implements ParticipantObserver {
    private final Participant participant;
    private final NotificationService notificationService;

    public ParticipantNotifiable(Participant participant, NotificationService notificationService) {
        this.participant = participant;
        this.notificationService = notificationService;
    }

    @Override
    public void notifier(String message) {
        notificationService.envoyerNotification(
            "Notification pour " + participant.toString() + " : " + message
        );
    }
}

