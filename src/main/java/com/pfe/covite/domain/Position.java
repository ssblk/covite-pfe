package com.pfe.covite.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

/**
 * A Position.
 */
@Entity
@Table(name = "position")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "position")
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pointdepart")
    private String pointdepart;

    @Column(name = "pointarrive")
    private String pointarrive;

    @OneToMany(mappedBy = "position")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Commande> commandes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPointdepart() {
        return pointdepart;
    }

    public Position pointdepart(String pointdepart) {
        this.pointdepart = pointdepart;
        return this;
    }

    public void setPointdepart(String pointdepart) {
        this.pointdepart = pointdepart;
    }

    public String getPointarrive() {
        return pointarrive;
    }

    public Position pointarrive(String pointarrive) {
        this.pointarrive = pointarrive;
        return this;
    }

    public void setPointarrive(String pointarrive) {
        this.pointarrive = pointarrive;
    }

    public Set<Commande> getCommandes() {
        return commandes;
    }

    public Position commandes(Set<Commande> commandes) {
        this.commandes = commandes;
        return this;
    }

    public Position addCommande(Commande commande) {
        this.commandes.add(commande);
        commande.setPosition(this);
        return this;
    }

    public Position removeCommande(Commande commande) {
        this.commandes.remove(commande);
        commande.setPosition(null);
        return this;
    }

    public void setCommandes(Set<Commande> commandes) {
        this.commandes = commandes;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        return id != null && id.equals(((Position) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Position{" +
            "id=" + getId() +
            ", pointdepart='" + getPointdepart() + "'" +
            ", pointarrive='" + getPointarrive() + "'" +
            "}";
    }
}
