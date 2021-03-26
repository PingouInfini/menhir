package com.pingouincorp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.pingouincorp.domain.enumeration.Couleur;

/**
 * A Individu.
 */
@Entity
@Table(name = "individu")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Individu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "taille")
    private Double taille;

    @Column(name = "date_de_naissance")
    private Instant dateDeNaissance;

    @Enumerated(EnumType.STRING)
    @Column(name = "couleur_cheveux")
    private Couleur couleurCheveux;

    @Column(name = "coiffure")
    private String coiffure;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "individu_appartienta",
               joinColumns = @JoinColumn(name = "individu_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "appartienta_id", referencedColumnName = "id"))
    private Set<Groupe> appartientAS = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public Individu nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Double getTaille() {
        return taille;
    }

    public Individu taille(Double taille) {
        this.taille = taille;
        return this;
    }

    public void setTaille(Double taille) {
        this.taille = taille;
    }

    public Instant getDateDeNaissance() {
        return dateDeNaissance;
    }

    public Individu dateDeNaissance(Instant dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
        return this;
    }

    public void setDateDeNaissance(Instant dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    public Couleur getCouleurCheveux() {
        return couleurCheveux;
    }

    public Individu couleurCheveux(Couleur couleurCheveux) {
        this.couleurCheveux = couleurCheveux;
        return this;
    }

    public void setCouleurCheveux(Couleur couleurCheveux) {
        this.couleurCheveux = couleurCheveux;
    }

    public String getCoiffure() {
        return coiffure;
    }

    public Individu coiffure(String coiffure) {
        this.coiffure = coiffure;
        return this;
    }

    public void setCoiffure(String coiffure) {
        this.coiffure = coiffure;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public Individu photo(byte[] photo) {
        this.photo = photo;
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public Individu photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public Set<Groupe> getAppartientAS() {
        return appartientAS;
    }

    public Individu appartientAS(Set<Groupe> groupes) {
        this.appartientAS = groupes;
        return this;
    }

    public Individu addAppartientA(Groupe groupe) {
        this.appartientAS.add(groupe);
        groupe.getIndividus().add(this);
        return this;
    }

    public Individu removeAppartientA(Groupe groupe) {
        this.appartientAS.remove(groupe);
        groupe.getIndividus().remove(this);
        return this;
    }

    public void setAppartientAS(Set<Groupe> groupes) {
        this.appartientAS = groupes;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Individu)) {
            return false;
        }
        return id != null && id.equals(((Individu) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Individu{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", taille=" + getTaille() +
            ", dateDeNaissance='" + getDateDeNaissance() + "'" +
            ", couleurCheveux='" + getCouleurCheveux() + "'" +
            ", coiffure='" + getCoiffure() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            "}";
    }
}
