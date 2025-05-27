package com.example.gestionevenement.evenement.modele;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Classe représentant un participant dans le système de gestion d'événements.
 * 
 * Cette classe encapsule les informations d'un participant et fournit
 * les méthodes nécessaires pour la gestion des inscriptions aux événements.
 * 
 * Principes SOLID respectés :
 * - SRP : Responsabilité unique de gérer les données d'un participant
 * - OCP : Extensible via héritage (voir Organisateur)
 * - LSP : Peut être substituée par ses sous-classes
 * - ISP : Interface simple et cohérente
 * - DIP : Pas de dépendances concrètes externes
 * 
 * @author Votre Nom
 * @version 1.0
 * @since 2025-05-27
 */
@XmlRootElement(name = "participant")
public class Participant {
    
    // Pattern pour validation d'email (RFC 5322 simplifié)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    @XmlElement
    @JsonProperty("id")
    private String id;
    
    @XmlElement
    @JsonProperty("nom")
    private String nom;
    
    @XmlElement
    @JsonProperty("email")
    private String email;

    /**
     * Constructeur par défaut requis pour la sérialisation JSON/XML.
     */
    public Participant() {
    }

    /**
     * Constructeur principal pour créer un participant.
     * 
     * @param id Identifiant unique du participant (ne peut pas être null ou vide)
     * @param nom Nom complet du participant (ne peut pas être null ou vide)
     * @param email Adresse email du participant (doit être valide)
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    @JsonCreator
    public Participant(
            @JsonProperty("id") String id,
            @JsonProperty("nom") String nom,
            @JsonProperty("email") String email) {
        
        validateConstructorParameters(id, nom, email);
        
        this.id = id.trim();
        this.nom = nom.trim();
        this.email = email.trim().toLowerCase(); // Normalisation de l'email
    }

    /**
     * Valide les paramètres du constructeur selon les règles métier.
     * 
     * @param id Identifiant à valider
     * @param nom Nom à valider
     * @param email Email à valider
     * @throws IllegalArgumentException si un paramètre est invalide
     */
    private void validateConstructorParameters(String id, String nom, String email) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant du participant ne peut pas être null ou vide");
        }
        
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du participant ne peut pas être null ou vide");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email du participant ne peut pas être null ou vide");
        }
        
        if (!isValidEmail(email.trim())) {
            throw new IllegalArgumentException("L'adresse email '" + email + "' n'est pas valide");
        }
    }

    /**
     * Valide le format d'une adresse email.
     * 
     * @param email L'adresse email à valider
     * @return true si l'email est valide, false sinon
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Met à jour l'adresse email du participant.
     * 
     * @param nouvelEmail La nouvelle adresse email (doit être valide)
     * @throws IllegalArgumentException si l'email n'est pas valide
     */
    public void mettreAJourEmail(String nouvelEmail) {
        if (nouvelEmail == null || nouvelEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être null ou vide");
        }
        
        String emailNormalise = nouvelEmail.trim().toLowerCase();
        if (!isValidEmail(emailNormalise)) {
            throw new IllegalArgumentException("L'adresse email '" + nouvelEmail + "' n'est pas valide");
        }
        
        this.email = emailNormalise;
    }

    /**
     * Met à jour le nom du participant.
     * 
     * @param nouveauNom Le nouveau nom (ne peut pas être null ou vide)
     * @throws IllegalArgumentException si le nom n'est pas valide
     */
    public void mettreAJourNom(String nouveauNom) {
        if (nouveauNom == null || nouveauNom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être null ou vide");
        }
        
        this.nom = nouveauNom.trim();
    }

    /**
     * Génère un nom d'affichage formaté pour les interfaces utilisateur.
     * 
     * @return nom formaté avec la première lettre en majuscule pour chaque mot
     */
    public String getNomFormate() {
        if (nom == null || nom.isEmpty()) {
            return "";
        }
        
        String[] mots = nom.toLowerCase().split("\\s+");
        StringBuilder nomFormate = new StringBuilder();
        
        for (String mot : mots) {
            if (!mot.isEmpty()) {
                if (nomFormate.length() > 0) {
                    nomFormate.append(" ");
                }
                nomFormate.append(mot.substring(0, 1).toUpperCase())
                         .append(mot.substring(1));
            }
        }
        
        return nomFormate.toString();
    }

    /**
     * Génère les initiales du participant.
     * 
     * @return les initiales (ex: "Jean Dupont" -> "JD")
     */
    public String getInitiales() {
        if (nom == null || nom.trim().isEmpty()) {
            return "";
        }
        
        String[] mots = nom.trim().split("\\s+");
        StringBuilder initiales = new StringBuilder();
        
        for (String mot : mots) {
            if (!mot.isEmpty()) {
                initiales.append(mot.charAt(0));
            }
        }
        
        return initiales.toString().toUpperCase();
    }

    /**
     * Vérifie si le participant a une adresse email d'un domaine spécifique.
     * 
     * @param domaine Le domaine à vérifier (ex: "gmail.com")
     * @return true si l'email appartient au domaine, false sinon
     */
    public boolean aDomainEmail(String domaine) {
        if (domaine == null || email == null) {
            return false;
        }
        
        return email.toLowerCase().endsWith("@" + domaine.toLowerCase());
    }

    /**
     * Génère une signature email pour le participant.
     * 
     * @return signature formatée
     */
    public String genererSignatureEmail() {
        return String.format("%s\n📧 %s\n🆔 ID: %s", 
                getNomFormate(), email, id);
    }

    /**
     * Créé une version anonymisée du participant (pour les statistiques).
     * 
     * @return participant avec données anonymisées
     */
    public Participant creerVersionAnonymisee() {
        String idAnonymise = "ANON_" + Math.abs(id.hashCode());
        String nomAnonymise = "Participant " + getInitiales();
        String emailAnonymise = "anonyme" + Math.abs(email.hashCode()) + "@exemple.com";
        
        return new Participant(idAnonymise, nomAnonymise, emailAnonymise);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    // Setters protégés pour la sérialisation
    protected void setId(String id) {
        this.id = id;
    }

    protected void setNom(String nom) {
        this.nom = nom;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    /**
     * Implémentation de equals basée sur l'ID unique.
     * Deux participants sont égaux s'ils ont le même ID.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Participant that = (Participant) obj;
        return Objects.equals(id, that.id);
    }

    /**
     * Implémentation de hashCode basée sur l'ID unique.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Représentation textuelle du participant.
     * Format : "Nom Formaté <email> [ID: id]"
     */
    @Override
    public String toString() {
        return String.format("%s <%s> [ID: %s]", 
                getNomFormate(), email, id);
    }

    /**
     * Version simplifiée pour l'affichage dans les listes.
     * Format : "Nom <email>"
     */
    public String toStringSimple() {
        return String.format("%s <%s>", getNomFormate(), email);
    }

    /**
     * Version détaillée pour les rapports.
     * 
     * @return représentation détaillée du participant
     */
    public String toStringDetaille() {
        return String.format(
                "Participant {\n" +
                "  ID: %s\n" +
                "  Nom: %s\n" +
                "  Email: %s\n" +
                "  Initiales: %s\n" +
                "  Domaine: %s\n" +
                "}",
                id,
                getNomFormate(),
                email,
                getInitiales(),
                email.substring(email.indexOf('@') + 1)
        );
    }
}