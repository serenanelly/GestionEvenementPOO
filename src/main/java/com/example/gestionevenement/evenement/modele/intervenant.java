package com.example.gestionevenement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Classe représentant un intervenant dans le système de gestion d'événements.
 * 
 * Un intervenant est une personne qui participe activement à une conférence
 * en tant qu'orateur, présentateur ou expert dans un domaine spécifique.
 * 
 * Principes SOLID respectés :
 * - SRP : Responsabilité unique de gérer les informations d'un intervenant
 * - OCP : Extensible pour différents types d'intervenants
 * - LSP : Peut être substituée par ses sous-classes potentielles
 * - ISP : Interface simple et cohérente
 * - DIP : Pas de dépendances concrètes externes
 * 
 * @author Votre Nom
 * @version 1.0
 * @since 2025-05-27
 */
@XmlRootElement(name = "intervenant")
public class Intervenant {
    
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
    @JsonProperty("specialite")
    private String specialite;
    
    @XmlElement
    @JsonProperty("email")
    private String email;
    
    @XmlElement
    @JsonProperty("biographie")
    private String biographie;
    
    @XmlElement
    @JsonProperty("institution")
    private String institution;

    /**
     * Constructeur par défaut requis pour la sérialisation JSON/XML.
     */
    public Intervenant() {
    }

    /**
     * Constructeur simplifié pour créer un intervenant avec les informations de base.
     * 
     * @param nom Nom complet de l'intervenant (ne peut pas être null ou vide)
     * @param specialite Domaine de spécialité (ne peut pas être null ou vide)
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public Intervenant(String nom, String specialite) {
        this(generateId(nom), nom, specialite, null, null, null);
    }

    /**
     * Constructeur complet pour créer un intervenant.
     * 
     * @param id Identifiant unique de l'intervenant
     * @param nom Nom complet de l'intervenant (ne peut pas être null ou vide)
     * @param specialite Domaine de spécialité (ne peut pas être null ou vide)
     * @param email Adresse email de contact (optionnelle, mais si fournie doit être valide)
     * @param biographie Biographie courte de l'intervenant (optionnelle)
     * @param institution Institution ou organisation de rattachement (optionnelle)
     * @throws IllegalArgumentException si les paramètres obligatoires sont invalides
     */
    @JsonCreator
    public Intervenant(
            @JsonProperty("id") String id,
            @JsonProperty("nom") String nom,
            @JsonProperty("specialite") String specialite,
            @JsonProperty("email") String email,
            @JsonProperty("biographie") String biographie,
            @JsonProperty("institution") String institution) {
        
        validateConstructorParameters(id, nom, specialite, email);
        
        this.id = id != null ? id.trim() : generateId(nom);
        this.nom = nom.trim();
        this.specialite = specialite.trim();
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.biographie = biographie != null ? biographie.trim() : null;
        this.institution = institution != null ? institution.trim() : null;
    }

    /**
     * Génère un ID unique basé sur le nom de l'intervenant.
     * 
     * @param nom Le nom de l'intervenant
     * @return un ID unique
     */
    private static String generateId(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return "INT_" + System.currentTimeMillis();
        }
        
        String nomNettoye = nom.trim().toLowerCase()
                              .replaceAll("[^a-zA-Z0-9]", "")
                              .substring(0, Math.min(nom.length(), 10));
        
        return "INT_" + nomNettoye + "_" + Math.abs(nom.hashCode());
    }

    /**
     * Valide les paramètres du constructeur selon les règles métier.
     * 
     * @param id Identifiant à valider
     * @param nom Nom à valider
     * @param specialite Spécialité à valider
     * @param email Email à valider (si fourni)
     * @throws IllegalArgumentException si un paramètre obligatoire est invalide
     */
    private void validateConstructorParameters(String id, String nom, String specialite, String email) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'intervenant ne peut pas être null ou vide");
        }
        
        if (specialite == null || specialite.trim().isEmpty()) {
            throw new IllegalArgumentException("La spécialité de l'intervenant ne peut pas être null ou vide");
        }
        
        // Validation de l'email seulement s'il est fourni
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("L'adresse email '" + email + "' n'est pas valide");
        }
    }

    /**
     * Met à jour l'adresse email de l'intervenant.
     * 
     * @param nouvelEmail La nouvelle adresse email (peut être null pour supprimer)
     * @throws IllegalArgumentException si l'email fourni n'est pas valide
     */
    public void mettreAJourEmail(String nouvelEmail) {
        if (nouvelEmail != null && !nouvelEmail.trim().isEmpty()) {
            String emailNormalise = nouvelEmail.trim().toLowerCase();
            if (!EMAIL_PATTERN.matcher(emailNormalise).matches()) {
                throw new IllegalArgumentException("L'adresse email '" + nouvelEmail + "' n'est pas valide");
            }
            this.email = emailNormalise;
        } else {
            this.email = null;
        }
    }

    /**
     * Met à jour la biographie de l'intervenant.
     * 
     * @param nouvelleBiographie La nouvelle biographie (peut être null pour supprimer)
     */
    public void mettreAJourBiographie(String nouvelleBiographie) {
        this.biographie = nouvelleBiographie != null && !nouvelleBiographie.trim().isEmpty() 
                         ? nouvelleBiographie.trim() 
                         : null;
    }

    /**
     * Met à jour l'institution de l'intervenant.
     * 
     * @param nouvelleInstitution La nouvelle institution (peut être null pour supprimer)
     */
    public void mettreAJourInstitution(String nouvelleInstitution) {
        this.institution = nouvelleInstitution != null && !nouvelleInstitution.trim().isEmpty() 
                          ? nouvelleInstitution.trim() 
                          : null;
    }

    /**
     * Vérifie si l'intervenant a une spécialité dans un domaine donné.
     * 
     * @param domaine Le domaine à vérifier (comparaison insensible à la casse)
     * @return true si l'intervenant a cette spécialité, false sinon
     */
    public boolean aSpecialiteDans(String domaine) {
        return domaine != null && 
               specialite.toLowerCase().contains(domaine.toLowerCase().trim());
    }

    /**
     * Vérifie si l'intervenant a une adresse email de contact.
     * 
     * @return true s'il a un email, false sinon
     */
    public boolean aEmail() {
        return email != null && !email.isEmpty();
    }

    /**
     * Vérifie si l'intervenant a une biographie.
     * 
     * @return true s'il a une biographie, false sinon
     */
    public boolean aBiographie() {
        return biographie != null && !biographie.isEmpty();
    }

    /**
     * Vérifie si l'intervenant a une institution de rattachement.
     * 
     * @return true s'il a une institution, false sinon
     */
    public boolean aInstitution() {
        return institution != null && !institution.isEmpty();
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
     * Génère une présentation courte de l'intervenant.
     * 
     * @return présentation formatée
     */
    public String genererPresentationCourte() {
        StringBuilder presentation = new StringBuilder();
        presentation.append(getNomFormate());
        
        if (institution != null && !institution.isEmpty()) {
            presentation.append(" (").append(institution).append(")");
        }
        
        presentation.append(" - Spécialité: ").append(specialite);
        
        return presentation.toString();
    }

    /**
     * Génère une présentation détaillée de l'intervenant.
     * 
     * @return présentation complète
     */
    public String genererPresentationDetaillee() {
        StringBuilder presentation = new StringBuilder();
        presentation.append("👤 ").append(getNomFormate()).append("\n");
        presentation.append("🎯 Spécialité: ").append(specialite).append("\n");
        
        if (institution != null && !institution.isEmpty()) {
            presentation.append("🏢 Institution: ").append(institution).append("\n");
        }
        
        if (email != null && !email.isEmpty()) {
            presentation.append("📧 Email: ").append(email).append("\n");
        }
        
        if (biographie != null && !biographie.isEmpty()) {
            presentation.append("📝 Biographie: ").append(biographie);
        }
        
        return presentation.toString();
    }

    /**
     * Génère une signature pour les emails ou documents.
     * 
     * @return signature formatée
     */
    public String genererSignature() {
        StringBuilder signature = new StringBuilder();
        signature.append(getNomFormate()).append("\n");
        signature.append("Spécialité: ").append(specialite).append("\n");
        
        if (institution != null && !institution.isEmpty()) {
            signature.append(institution).append("\n");
        }
        
        if (email != null && !email.isEmpty()) {
            signature.append("📧 ").append(email);
        }
        
        return signature.toString();
    }

    /**
     * Créé une version anonymisée de l'intervenant (pour les statistiques).
     * 
     * @return intervenant avec données anonymisées
     */
    public Intervenant creerVersionAnonymisee() {
        String idAnonymise = "ANON_INT_" + Math.abs(id.hashCode());
        String nomAnonymise = "Intervenant " + Math.abs(nom.hashCode());
        String specialiteAnonymisee = specialite; // La spécialité peut rester pour les stats
        
        return new Intervenant(idAnonymise, nomAnonymise, specialiteAnonymisee, null, null, null);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getSpecialite() {
        return specialite;
    }

    public String getEmail() {
        return email;
    }

    public String getBiographie() {
        return biographie;
    }

    public String getInstitution() {
        return institution;
    }

    // Setters protégés pour la sérialisation
    protected void setId(String id) {
        this.id = id;
    }

    protected void setNom(String nom) {
        this.nom = nom;
    }

    protected void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    protected void setBiographie(String biographie) {
        this.biographie = biographie;
    }

    protected void setInstitution(String institution) {
        this.institution = institution;
    }

    /**
     * Implémentation de equals basée sur l'ID unique.
     * Deux intervenants sont égaux s'ils ont le même ID.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Intervenant that = (Intervenant) obj;
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
     * Représentation textuelle de l'intervenant.