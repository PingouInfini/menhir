package com.capgemini.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A Groupe.
 */
@Entity
@Table(name = "groupe")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Groupe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "date_creation")
    private Instant dateCreation;

    @Lob
    @Column(name = "piece_jointe")
    private byte[] pieceJointe;

    @Column(name = "piece_jointe_content_type")
    private String pieceJointeContentType;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "groupe_est_situe",
               joinColumns = @JoinColumn(name = "groupe_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "est_situe_id", referencedColumnName = "id"))
    private Set<Lieu> estSitues = new HashSet<>();

    @ManyToMany(mappedBy = "appartientAS")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    //@JsonIgnore
    private Set<Individu> individus = new HashSet<>();

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

    public Groupe nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public Groupe description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdresse() {
        return adresse;
    }

    public Groupe adresse(String adresse) {
        this.adresse = adresse;
        return this;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public Groupe dateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
        return this;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public byte[] getPieceJointe() {
        return pieceJointe;
    }

    public Groupe pieceJointe(byte[] pieceJointe) {
        this.pieceJointe = pieceJointe;
        return this;
    }

    public void setPieceJointe(byte[] pieceJointe) {
        this.pieceJointe = pieceJointe;
    }

    public String getPieceJointeContentType() {
        return pieceJointeContentType;
    }

    public Groupe pieceJointeContentType(String pieceJointeContentType) {
        this.pieceJointeContentType = pieceJointeContentType;
        return this;
    }

    public void setPieceJointeContentType(String pieceJointeContentType) {
        this.pieceJointeContentType = pieceJointeContentType;
    }

    public Set<Lieu> getEstSitues() {
        return estSitues;
    }

    public Groupe estSitues(Set<Lieu> lieus) {
        this.estSitues = lieus;
        return this;
    }

    public Groupe addEstSitue(Lieu lieu) {
        this.estSitues.add(lieu);
        lieu.getGroupes().add(this);
        return this;
    }

    public Groupe removeEstSitue(Lieu lieu) {
        this.estSitues.remove(lieu);
        lieu.getGroupes().remove(this);
        return this;
    }

    public void setEstSitues(Set<Lieu> lieus) {
        this.estSitues = lieus;
    }

    public Set<Individu> getIndividus() {
        return individus;
    }

    public Groupe individus(Set<Individu> individus) {
        this.individus = individus;
        return this;
    }

    public Groupe addIndividu(Individu individu) {
        this.individus.add(individu);
        individu.getAppartientAS().add(this);
        return this;
    }

    public Groupe removeIndividu(Individu individu) {
        this.individus.remove(individu);
        individu.getAppartientAS().remove(this);
        return this;
    }

    public void setIndividus(Set<Individu> individus) {
        this.individus = individus;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Groupe)) {
            return false;
        }
        return id != null && id.equals(((Groupe) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Groupe{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", description='" + getDescription() + "'" +
            ", adresse='" + getAdresse() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            ", pieceJointe='" + getPieceJointe() + "'" +
            ", pieceJointeContentType='" + getPieceJointeContentType() + "'" +
            "}";
    }
}
