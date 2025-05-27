package com.example.gestionevenement.model;

import com.example.gestionevenement.observer.EvenementObservable;
import com.example.gestionevenement.exception.CapaciteMaxAtteinteException;
import com.example.gestionevenement.exception.ParticipantNonTrouveException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classe abstraite représentant un événement dans le système de gestion d'événements.
 * 
 * Cette classe implémente les principes SOLID :
 * - SRP : Responsabilité unique de gérer les informations d'un événement
 * - OCP : Ouverte à l'extension via l'héritage, fermée à la modification
 * - LSP : Les sous-classes peuvent remplacer cette classe abstraite
 * - ISP : Interface séparée pour l'observable
 * - DIP : Dépend de l'abstraction EvenementObservable
 * 
 * @author Votre Nom
 * @version 1.0
 * @since 2025-05-27
 */
@XmlRootElement
@XmlSeeAlso({Conference.class, Concert.class})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Conference.class, name = "conference"),
    @JsonSubTypes.Type(value = Concert.class, name = "concert")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Evenement extends EvenementObservable {
    
    // Attributs protégés pour permettre l'accès aux sous-classes
    protected String id;
    protected String nom;
    protected LocalDateTime date;
    protected String lieu;
    protected int capaciteMax;
    protected List<Participant> participants;
    protected boolean annule;

    /**
     * Constructeur par défaut requis pour la sérialisation JSON/XML.
     */
    protected Evenement() {
        this.participants = new ArrayList<>();
        this.annule = false;
    }

    /**
     * Constructeur principal pour créer un événement.
     * 
     * @param id Identifiant unique de l'événement (ne peut pas être null ou vide)
     * @param nom Nom de l'événement (ne peut pas être null ou vide)
     * @param date Date et heure de l'événement (ne peut pas être null ou dans le passé)
     * @param lieu Lieu de l'événement (ne peut pas être null ou vide)
     * @param capaciteMax Capacité maximale (doit être positive)
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public Evenement(String id, String nom, LocalDateTime date, String lieu, int capaciteMax) {
        this();
        validateConstructorParameters(id, nom, date, lieu, capaciteMax);
        
        this.id = id;
        this.nom = nom;
        this.date = date;
        this.lieu = lieu;
        this.capaciteMax = capaciteMax;
    }

    /**
     * Valide les paramètres du constructeur selon les règles métier.
     * 
     * @param id Identifiant à valider
     * @param nom Nom à valider
     * @param date Date à valider
     * @param lieu Lieu à valider
     * @param capaciteMax Capacité à valider
     * @throws IllegalArgumentException si un paramètre est invalide
     */
    private void validateConstructorParameters(String id, String nom, LocalDateTime date, String lieu, int capaciteMax) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant de l'événement ne peut pas être null ou vide");
        }
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'événement ne peut pas être null ou vide");
        }
        if (date == null) {
            throw new IllegalArgumentException("La date de l'événement ne peut pas être null");
        }
        if (date.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de l'événement ne peut pas être dans le passé");
        }
        if (lieu == null || lieu.trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu de l'événement ne peut pas être null ou vide");
        }
        if (capaciteMax <= 0) {
            throw new IllegalArgumentException("La capacité maximale doit être positive");
        }
    }

    /**
     * Ajoute un participant à l'événement.
     * 
     * Respecte le principe de responsabilité unique en se concentrant uniquement
     * sur l'ajout d'un participant avec validation.
     * 
     * @param participant Le participant à ajouter (ne peut pas être null)
     * @throws CapaciteMaxAtteinteException si la capacité maximale est atteinte
     * @throws IllegalArgumentException si le participant est null
     * @throws IllegalStateException si l'événement est annulé
     */
    public final void ajouterParticipant(Participant participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Le participant ne peut pas être null");
        }
        
        if (annule) {
            throw new IllegalStateException("Impossible d'ajouter un participant à un événement annulé");
        }
        
        if (participants.size() >= capaciteMax) {
            throw new CapaciteMaxAtteinteException(
                "Capacité maximale atteinte pour l'événement '" + nom + "'. " +
                "Capacité: " + capaciteMax + ", Participants actuels: " + participants.size()
            );
        }
        
        // Éviter les doublons
        if (!participants.contains(participant)) {
            participants.add(participant);
            // Notifier les observateurs de l'ajout d'un participant
            notifyObservers("Nouveau participant ajouté à l'événement '" + nom + "': " + participant.getNom());
        }
    }

    /**
     * Retire un participant de l'événement.
     * 
     * @param participant Le participant à retirer
     * @throws ParticipantNonTrouveException si le participant n'est pas inscrit
     * @throws IllegalArgumentException si le participant est null
     */
    public final void retirerParticipant(Participant participant) {
        if (participant == null) {
            throw new IllegalArgumentException("Le participant ne peut pas être null");
        }
        
        if (!participants.remove(participant)) {
            throw new ParticipantNonTrouveException(
                "Le participant '" + participant.getNom() + "' n'est pas inscrit à l'événement '" + nom + "'"
            );
        }
        
        // Notifier les observateurs du retrait d'un participant
        notifyObservers("Participant retiré de l'événement '" + nom + "': " + participant.getNom());
    }

    /**
     * Affiche les détails de l'événement.
     * Méthode finale pour assurer la cohérence de l'affichage.
     */
    public final void afficherDetails() {
        System.out.println("=== DÉTAILS DE L'ÉVÉNEMENT ===");
        System.out.println("ID: " + id);
        System.out.println("Nom: " + nom);
        System.out.println("Type: " + this.getClass().getSimpleName());
        System.out.println("Date: " + date);
        System.out.println("Lieu: " + lieu);
        System.out.println("Capacité maximale: " + capaciteMax);
        System.out.println("Participants inscrits: " + participants.size());
        System.out.println("Statut: " + (annule ? "ANNULÉ" : "ACTIF"));
        
        // Affichage des détails spécifiques via une méthode abstraite
        afficherDetailsSpecifiques();
        
        System.out.println("==============================");
    }

    /**
     * Méthode abstraite pour afficher les détails spécifiques à chaque type d'événement.
     * Respecte le principe ouvert/fermé - les sous-classes étendent le comportement.
     */
    protected abstract void afficherDetailsSpecifiques();

    /**
     * Annule l'événement et notifie tous les participants observateurs.
     * 
     * Utilise le Template Method Pattern pour permettre aux sous-classes
     * de personnaliser le processus d'annulation.
     */
    public final void annuler() {
        if (annule) {
            throw new IllegalStateException("L'événement '" + nom + "' est déjà annulé");
        }
        
        // Logique commune d'annulation
        this.annule = true;
        
        // Permettre aux sous-classes de personnaliser l'annulation
        onAnnulation();
        
        // Notifier tous les observateurs
        notifyObservers("L'événement '" + nom + "' a été annulé. Date prévue: " + date);
    }

    /**
     * Hook method pour permettre aux sous-classes de personnaliser l'annulation.
     * Par défaut, ne fait rien.
     */
    protected void onAnnulation() {
        // Implémentation par défaut vide
        // Les sous-classes peuvent redéfinir cette méthode
    }

    // Getters avec validation
    public String getId() { return id; }
    public String getNom() { return nom; }
    public LocalDateTime getDate() { return date; }
    public String getLieu() { return lieu; }
    public int getCapaciteMax() { return capaciteMax; }
    public boolean isAnnule() { return annule; }
    
    /**
     * Retourne une copie non modifiable de la liste des participants.
     * Respecte le principe d'encapsulation.
     */
    public List<Participant> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    /**
     * Retourne le nombre de participants inscrits.
     */
    public int getNombreParticipants() {
        return participants.size();
    }

    /**
     * Vérifie s'il reste des places disponibles.
     */
    public boolean aDesPlacesDisponibles() {
        return participants.size() < capaciteMax && !annule;
    }

    /**
     * Retourne le nombre de places disponibles.
     */
    public int getPlacesDisponibles() {
        return Math.max(0, capaciteMax - participants.size());
    }

    // Setters protégés pour la sérialisation
    protected void setId(String id) { this.id = id; }
    protected void setNom(String nom) { this.nom = nom; }
    protected void setDate(LocalDateTime date) { this.date = date; }
    protected void setLieu(String lieu) { this.lieu = lieu; }
    protected void setCapaciteMax(int capaciteMax) { this.capaciteMax = capaciteMax; }
    protected void setParticipants(List<Participant> participants) { 
        this.participants = participants != null ? participants : new ArrayList<>(); 
    }
    protected void setAnnule(boolean annule) { this.annule = annule; }

    /**
     * Implémentation de equals basée sur l'ID unique.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Evenement evenement = (Evenement) obj;
        return Objects.equals(id, evenement.id);
    }

    /**
     * Implémentation de hashCode basée sur l'ID unique.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Représentation textuelle de l'événement.
     */
    @Override
    public String toString() {
        return String.format("%s{id='%s', nom='%s', date=%s, lieu='%s', participants=%d/%d, annule=%s}",
                this.getClass().getSimpleName(), id, nom, date, lieu, participants.size(), capaciteMax, annule);
    }
}