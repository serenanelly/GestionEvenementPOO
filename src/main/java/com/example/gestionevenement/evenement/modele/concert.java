package com.example.gestionevenement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Classe représentant un concert dans le système de gestion d'événements.
 * 
 * Cette classe hérite de Evenement et ajoute des fonctionnalités spécifiques
 * aux concerts comme la gestion de l'artiste et du genre musical.
 * 
 * Principes SOLID respectés :
 * - SRP : Gère uniquement les spécificités des concerts
 * - OCP : Étend Evenement sans le modifier
 * - LSP : Peut remplacer Evenement dans tous les contextes
 * - ISP : Utilise les interfaces appropriées
 * - DIP : Dépend de l'abstraction Evenement
 * 
 * @author Votre Nom
 * @version 1.0
 * @since 2025-05-27
 */
@XmlRootElement(name = "concert")
@JsonTypeName("concert")
public class Concert extends Evenement {
    
    @XmlElement
    @JsonProperty("artiste")
    private String artiste;
    
    @XmlElement
    @JsonProperty("genreMusical")
    private String genreMusical;

    /**
     * Constructeur par défaut requis pour la sérialisation JSON/XML.
     */
    public Concert() {
        super();
    }

    /**
     * Constructeur principal pour créer un concert.
     * 
     * @param id Identifiant unique du concert
     * @param nom Nom du concert
     * @param date Date et heure du concert
     * @param lieu Lieu du concert
     * @param capaciteMax Capacité maximale de spectateurs
     * @param artiste Nom de l'artiste/groupe (ne peut pas être null ou vide)
     * @param genreMusical Genre musical du concert (ne peut pas être null ou vide)
     * @throws IllegalArgumentException si l'artiste ou le genre musical est null ou vide
     */
    @JsonCreator
    public Concert(
            @JsonProperty("id") String id,
            @JsonProperty("nom") String nom,
            @JsonProperty("date") LocalDateTime date,
            @JsonProperty("lieu") String lieu,
            @JsonProperty("capaciteMax") int capaciteMax,
            @JsonProperty("artiste") String artiste,
            @JsonProperty("genreMusical") String genreMusical) {
        
        super(id, nom, date, lieu, capaciteMax);
        
        // Validation de l'artiste
        if (artiste == null || artiste.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'artiste ne peut pas être null ou vide");
        }
        
        // Validation du genre musical
        if (genreMusical == null || genreMusical.trim().isEmpty()) {
            throw new IllegalArgumentException("Le genre musical ne peut pas être null ou vide");
        }
        
        this.artiste = artiste.trim();
        this.genreMusical = genreMusical.trim();
    }

    /**
     * Vérifie si le concert correspond à un genre musical spécifique.
     * 
     * @param genre Le genre à vérifier (comparaison insensible à la casse)
     * @return true si le concert correspond au genre, false sinon
     */
    public boolean estDuGenre(String genre) {
        return genre != null && this.genreMusical.equalsIgnoreCase(genre.trim());
    }

    /**
     * Vérifie if le concert est interprété par un artiste spécifique.
     * 
     * @param nomArtiste Le nom de l'artiste à vérifier (comparaison insensible à la casse)
     * @return true si l'artiste correspond, false sinon
     */
    public boolean estInterpretePar(String nomArtiste) {
        return nomArtiste != null && this.artiste.equalsIgnoreCase(nomArtiste.trim());
    }

    /**
     * Génère une description marketing du concert.
     * 
     * @return une description formatée du concert
     */
    public String genererDescriptionMarketing() {
        StringBuilder description = new StringBuilder();
        description.append("🎵 ").append(getNom()).append("\n");
        description.append("🎤 Avec ").append(artiste).append("\n");
        description.append("🎸 Genre : ").append(genreMusical).append("\n");
        description.append("📅 Le ").append(getDate().toLocalDate()).append(" à ").append(getDate().toLocalTime()).append("\n");
        description.append("📍 ").append(getLieu()).append("\n");
        description.append("🎟️ Places disponibles : ").append(getPlacesDisponibles()).append("/").append(getCapaciteMax());
        
        if (isAnnule()) {
            description.append("\n❌ CONCERT ANNULÉ");
        }
        
        return description.toString();
    }

    /**
     * Calcule le prix suggéré basé sur la popularité du genre musical.
     * (Méthode exemple pour démontrer la logique métier spécifique aux concerts)
     * 
     * @return prix suggéré en euros
     */
    public double calculerPrixSuggere() {
        // Prix de base
        double prixBase = 25.0;
        
        // Ajustement selon le genre musical
        switch (genreMusical.toLowerCase()) {
            case "rock":
            case "pop":
                return prixBase * 1.5;
            case "classique":
            case "jazz":
                return prixBase * 1.8;
            case "électronique":
            case "techno":
                return prixBase * 1.3;
            case "folk":
            case "acoustique":
                return prixBase * 1.1;
            default:
                return prixBase;
        }
    }

    /**
     * Implémentation de la méthode abstraite pour l'annulation spécifique aux concerts.
     * 
     * Cette méthode est appelée par la méthode annuler() de la classe parente
     * via le Template Method Pattern.
     */
    @Override
    protected void onAnnulation() {
        // Logique spécifique à l'annulation d'un concert
        System.out.println("=== ANNULATION DE CONCERT ===");
        System.out.println("Le concert \"" + getNom() + "\" de " + artiste + " a été annulé.");
        System.out.println("Genre musical : " + genreMusical);
        System.out.println("Date prévue : " + getDate());
        System.out.println("Lieu : " + getLieu());
        System.out.println("Spectateurs concernés : " + getNombreParticipants());
        
        if (getNombreParticipants() > 0) {
            System.out.println("⚠️  Remboursement automatique en cours pour tous les spectateurs.");
            System.out.println("📧 Notifications d'annulation envoyées à tous les participants.");
        }
        
        System.out.println("==============================");
    }

    /**
     * Affiche les détails spécifiques au concert.
     * 
     * Implémentation de la méthode abstraite définie dans Evenement.
     */
    @Override
    protected void afficherDetailsSpecifiques() {
        System.out.println("Artiste/Groupe: " + artiste);
        System.out.println("Genre musical: " + genreMusical);
        System.out.println("Prix suggéré: " + String.format("%.2f €", calculerPrixSuggere()));
        
        // Affichage d'informations additionnelles selon le taux de remplissage
        double tauxRemplissage = (double) getNombreParticipants() / getCapaciteMax() * 100;
        System.out.println("Taux de remplissage: " + String.format("%.1f%%", tauxRemplissage));
        
        if (tauxRemplissage >= 90) {
            System.out.println("🔥 CONCERT PRESQUE COMPLET !");
        } else if (tauxRemplissage >= 75) {
            System.out.println("⭐ Forte demande - Réservez vite !");
        } else if (tauxRemplissage < 25) {
            System.out.println("🎟️ Encore de nombreuses places disponibles");
        }
    }

    /**
     * Génère un résumé du concert pour les notifications.
     * 
     * @return un résumé formaté du concert
     */
    public String genererResume() {
        StringBuilder resume = new StringBuilder();
        resume.append("CONCERT: ").append(getNom()).append("\n");
        resume.append("Artiste: ").append(artiste).append("\n");
        resume.append("Genre: ").append(genreMusical).append("\n");
        resume.append("Date: ").append(getDate()).append("\n");
        resume.append("Lieu: ").append(getLieu()).append("\n");
        resume.append("Spectateurs: ").append(getNombreParticipants()).append("/").append(getCapaciteMax());
        
        return resume.toString();
    }

    /**
     * Vérifie si le concert peut encore accueillir un groupe minimum de spectateurs.
     * Utile pour les réservations de groupe.
     * 
     * @param tailleGroupe Taille du groupe souhaitée
     * @return true si le groupe peut être accueilli, false sinon
     */
    public boolean peutAccueillirGroupe(int tailleGroupe) {
        return tailleGroupe > 0 && 
               getPlacesDisponibles() >= tailleGroupe && 
               !isAnnule();
    }

    // Getters avec encapsulation appropriée
    public String getArtiste() {
        return artiste;
    }

    public String getGenreMusical() {
        return genreMusical;
    }

    // Setters protégés pour la sérialisation
    protected void setArtiste(String artiste) {
        this.artiste = artiste;
    }

    protected void setGenreMusical(String genreMusical) {
        this.genreMusical = genreMusical;
    }

    /**
     * Implémentation de equals incluant les attributs spécifiques au Concert.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        
        Concert concert = (Concert) obj;
        return Objects.equals(artiste, concert.artiste) &&
               Objects.equals(genreMusical, concert.genreMusical);
    }

    /**
     * Implémentation de hashCode incluant les attributs spécifiques au Concert.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), artiste, genreMusical);
    }

    /**
     * Représentation textuelle détaillée du concert.
     */
    @Override
    public String toString() {
        return String.format("Concert{id='%s', nom='%s', artiste='%s', genre='%s', date=%s, lieu='%s', " +
                           "spectateurs=%d/%d, annule=%s}",
                getId(), getNom(), artiste, genreMusical, getDate(), getLieu(), 
                getNombreParticipants(), getCapaciteMax(), isAnnule());
    }
}