package com.example.gestionevenement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classe représentant une conférence dans le système de gestion d'événements.
 * 
 * Cette classe hérite de Evenement et ajoute des fonctionnalités spécifiques
 * aux conférences comme la gestion du thème et des intervenants.
 * 
 * Principes SOLID respectés :
 * - SRP : Gère uniquement les spécificités des conférences
 * - OCP : Étend Evenement sans le modifier
 * - LSP : Peut remplacer Evenement dans tous les contextes
 * - ISP : Utilise les interfaces appropriées
 * - DIP : Dépend de l'abstraction Evenement
 * 
 * @author Votre Nom
 * @version 1.0
 * @since 2025-05-27
 */
@XmlRootElement(name = "conference")
@JsonTypeName("conference")
public class Conference extends Evenement {
    
    @XmlElement
    @JsonProperty("theme")
    private String theme;
    
    @XmlElementWrapper(name = "intervenants")
    @XmlElement(name = "intervenant")
    @JsonProperty("intervenants")
    private List<Intervenant> intervenants;

    /**
     * Constructeur par défaut requis pour la sérialisation JSON/XML.
     */
    public Conference() {
        super();
        this.intervenants = new ArrayList<>();
    }

    /**
     * Constructeur principal pour créer une conférence.
     * 
     * @param id Identifiant unique de la conférence
     * @param nom Nom de la conférence
     * @param date Date et heure de la conférence
     * @param lieu Lieu de la conférence
     * @param capaciteMax Capacité maximale de participants
     * @param theme Thème de la conférence (ne peut pas être null ou vide)
     * @param intervenants Liste des intervenants (peut être null, sera initialisée)
     * @throws IllegalArgumentException si le thème est null ou vide
     */
    @JsonCreator
    public Conference(
            @JsonProperty("id") String id,
            @JsonProperty("nom") String nom,
            @JsonProperty("date") LocalDateTime date,
            @JsonProperty("lieu") String lieu,
            @JsonProperty("capaciteMax") int capaciteMax,
            @JsonProperty("theme") String theme,
            @JsonProperty("intervenants") List<Intervenant> intervenants) {
        
        super(id, nom, date, lieu, capaciteMax);
        
        // Validation du thème
        if (theme == null || theme.trim().isEmpty()) {
            throw new IllegalArgumentException("Le thème de la conférence ne peut pas être null ou vide");
        }
        
        this.theme = theme.trim();
        this.intervenants = intervenants != null ? new ArrayList<>(intervenants) : new ArrayList<>();
    }

    /**
     * Constructeur simplifié sans intervenants.
     * 
     * @param id Identifiant unique de la conférence
     * @param nom Nom de la conférence
     * @param date Date et heure de la conférence
     * @param lieu Lieu de la conférence
     * @param capaciteMax Capacité maximale de participants
     * @param theme Thème de la conférence
     */
    public Conference(String id, String nom, LocalDateTime date, String lieu, int capaciteMax, String theme) {
        this(id, nom, date, lieu, capaciteMax, theme, new ArrayList<>());
    }

    /**
     * Ajoute un intervenant à la conférence.
     * 
     * @param intervenant L'intervenant à ajouter (ne peut pas être null)
     * @throws IllegalArgumentException si l'intervenant est null
     * @throws IllegalStateException si la conférence est annulée
     */
    public void ajouterIntervenant(Intervenant intervenant) {
        if (intervenant == null) {
            throw new IllegalArgumentException("L'intervenant ne peut pas être null");
        }
        
        if (isAnnule()) {
            throw new IllegalStateException("Impossible d'ajouter un intervenant à une conférence annulée");
        }
        
        // Éviter les doublons
        if (!intervenants.contains(intervenant)) {
            intervenants.add(intervenant);
            
            // Notifier les observateurs de l'ajout d'un intervenant
            notifyObservers("Nouvel intervenant ajouté à la conférence '" + getNom() + "': " + 
                          intervenant.getNom() + " (" + intervenant.getSpecialite() + ")");
        }
    }

    /**
     * Retire un intervenant de la conférence.
     * 
     * @param intervenant L'intervenant à retirer
     * @throws IllegalArgumentException si l'intervenant est null
     * @throws IllegalStateException si l'intervenant n'est pas trouvé
     */
    public void retirerIntervenant(Intervenant intervenant) {
        if (intervenant == null) {
            throw new IllegalArgumentException("L'intervenant ne peut pas être null");
        }
        
        if (!intervenants.remove(intervenant)) {
            throw new IllegalStateException("L'intervenant '" + intervenant.getNom() + 
                                          "' ne fait pas partie de cette conférence");
        }
        
        // Notifier les observateurs du retrait d'un intervenant
        notifyObservers("Intervenant retiré de la conférence '" + getNom() + "': " + intervenant.getNom());
    }

    /**
     * Vérifie si la conférence a des intervenants.
     * 
     * @return true si la conférence a au moins un intervenant, false sinon
     */
    public boolean aDesIntervenants() {
        return !intervenants.isEmpty();
    }

    /**
     * Retourne le nombre d'intervenants.
     * 
     * @return le nombre d'intervenants
     */
    public int getNombreIntervenants() {
        return intervenants.size();
    }

    /**
     * Vérifie si un intervenant participe à cette conférence.
     * 
     * @param intervenant L'intervenant à rechercher
     * @return true si l'intervenant participe, false sinon
     */
    public boolean participeIntervenant(Intervenant intervenant) {
        return intervenant != null && intervenants.contains(intervenant);
    }

    /**
     * Implémentation de la méthode abstraite pour l'annulation spécifique aux conférences.
     * 
     * Cette méthode est appelée par la méthode annuler() de la classe parente
     * via le Template Method Pattern.
     */
    @Override
    protected void onAnnulation() {
        // Logique spécifique à l'annulation d'une conférence
        System.out.println("=== ANNULATION DE CONFÉRENCE ===");
        System.out.println("La conférence \"" + getNom() + "\" sur le thème \"" + theme + "\" a été annulée.");
        
        if (!intervenants.isEmpty()) {
            System.out.println("Intervenants concernés :");
            intervenants.forEach(intervenant -> 
                System.out.println("- " + intervenant.getNom() + " (" + intervenant.getSpecialite() + ")")
            );
        }
        
        System.out.println("Date prévue : " + getDate());
        System.out.println("Lieu : " + getLieu());
        System.out.println("Participants inscrits : " + getNombreParticipants());
        System.out.println("================================");
    }

    /**
     * Affiche les détails spécifiques à la conférence.
     * 
     * Implémentation de la méthode abstraite définie dans Evenement.
     */
    @Override
    protected void afficherDetailsSpecifiques() {
        System.out.println("Thème: " + theme);
        System.out.println("Nombre d'intervenants: " + intervenants.size());
        
        if (!intervenants.isEmpty()) {
            System.out.println("Intervenants:");
            intervenants.forEach(intervenant -> 
                System.out.println("  • " + intervenant.getNom() + 
                                 " - Spécialité: " + intervenant.getSpecialite() +
                                 " - Email: " + intervenant.getEmail())
            );
        } else {
            System.out.println("Aucun intervenant assigné pour le moment");
        }
    }

    /**
     * Génère un résumé de la conférence pour les notifications.
     * 
     * @return un résumé formaté de la conférence
     */
    public String genererResume() {
        StringBuilder resume = new StringBuilder();
        resume.append("CONFÉRENCE: ").append(getNom()).append("\n");
        resume.append("Thème: ").append(theme).append("\n");
        resume.append("Date: ").append(getDate()).append("\n");
        resume.append("Lieu: ").append(getLieu()).append("\n");
        resume.append("Participants: ").append(getNombreParticipants()).append("/").append(getCapaciteMax()).append("\n");
        
        if (!intervenants.isEmpty()) {
            resume.append("Intervenants: ");
            intervenants.forEach(intervenant -> 
                resume.append(intervenant.getNom()).append(" (").append(intervenant.getSpecialite()).append("), ")
            );
            // Supprimer la dernière virgule et espace
            resume.setLength(resume.length() - 2);
        }
        
        return resume.toString();
    }

    // Getters avec encapsulation appropriée
    public String getTheme() {
        return theme;
    }

    /**
     * Retourne une copie non modifiable de la liste des intervenants.
     * Respecte le principe d'encapsulation.
     * 
     * @return liste non modifiable des intervenants
     */
    public List<Intervenant> getIntervenants() {
        return Collections.unmodifiableList(intervenants);
    }

    // Setters protégés pour la sérialisation
    protected void setTheme(String theme) {
        this.theme = theme;
    }

    protected void setIntervenants(List<Intervenant> intervenants) {
        this.intervenants = intervenants != null ? intervenants : new ArrayList<>();
    }

    /**
     * Implémentation de equals incluant les attributs spécifiques à Conference.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        
        Conference conference = (Conference) obj;
        return Objects.equals(theme, conference.theme) &&
               Objects.equals(intervenants, conference.intervenants);
    }

    /**
     * Implémentation de hashCode incluant les attributs spécifiques à Conference.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), theme, intervenants);
    }

    /**
     * Représentation textuelle détaillée de la conférence.
     */
    @Override
    public String toString() {
        return String.format("Conference{id='%s', nom='%s', theme='%s', date=%s, lieu='%s', " +
                           "participants=%d/%d, intervenants=%d, annule=%s}",
                getId(), getNom(), theme, getDate(), getLieu(), 
                getNombreParticipants(), getCapaciteMax(), intervenants.size(), isAnnule());
    }
}