package com.pfe.covite.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.Objects;
import java.time.LocalDate;

import com.pfe.covite.domain.enumeration.Categorie;

import com.pfe.covite.domain.enumeration.Service;

/**
 * A Commande.
 */
@Entity
@Table(name = "commande")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "commande")
public class Commande implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "prix")
    private Double prix;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Categorie type;

    @Enumerated(EnumType.STRING)
    @Column(name = "typeservice")
    private Service typeservice;

    @ManyToOne
    @JsonIgnoreProperties("commandes")
    private Voiture voiture;

    @ManyToOne
    @JsonIgnoreProperties("commandes")
    private Position position;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Commande date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getPrix() {
        return prix;
    }

    public Commande prix(Double prix) {
        this.prix = prix;
        return this;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Categorie getType() {
        return type;
    }

    public Commande type(Categorie type) {
        this.type = type;
        return this;
    }

    public void setType(Categorie type) {
        this.type = type;
    }

    public Service getTypeservice() {
        return typeservice;
    }

    public Commande typeservice(Service typeservice) {
        this.typeservice = typeservice;
        return this;
    }

    public void setTypeservice(Service typeservice) {
        this.typeservice = typeservice;
    }

    public Voiture getVoiture() {
        return voiture;
    }

    public Commande voiture(Voiture voiture) {
        this.voiture = voiture;
        return this;
    }

    public void setVoiture(Voiture voiture) {
        this.voiture = voiture;
    }

    public Position getPosition() {
        return position;
    }

    public Commande position(Position position) {
        this.position = position;
        return this;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Commande)) {
            return false;
        }
        return id != null && id.equals(((Commande) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Commande{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", prix=" + getPrix() +
            ", type='" + getType() + "'" +
            ", typeservice='" + getTypeservice() + "'" +
            "}";
    }
}
