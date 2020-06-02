package com.pfe.covite.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

import com.pfe.covite.domain.enumeration.Typevehicule;

/**
 * A Voiture.
 */
@Entity
@Table(name = "voiture")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "voiture")
public class Voiture implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "matricule")
    private String matricule;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Typevehicule type;

    @Column(name = "capacite")
    private Integer capacite;

    @OneToMany(mappedBy = "voiture")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Commande> commandes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public Voiture matricule(String matricule) {
        this.matricule = matricule;
        return this;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public Typevehicule getType() {
        return type;
    }

    public Voiture type(Typevehicule type) {
        this.type = type;
        return this;
    }

    public void setType(Typevehicule type) {
        this.type = type;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public Voiture capacite(Integer capacite) {
        this.capacite = capacite;
        return this;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public Set<Commande> getCommandes() {
        return commandes;
    }

    public Voiture commandes(Set<Commande> commandes) {
        this.commandes = commandes;
        return this;
    }

    public Voiture addCommande(Commande commande) {
        this.commandes.add(commande);
        commande.setVoiture(this);
        return this;
    }

    public Voiture removeCommande(Commande commande) {
        this.commandes.remove(commande);
        commande.setVoiture(null);
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
        if (!(o instanceof Voiture)) {
            return false;
        }
        return id != null && id.equals(((Voiture) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Voiture{" +
            "id=" + getId() +
            ", matricule='" + getMatricule() + "'" +
            ", type='" + getType() + "'" +
            ", capacite=" + getCapacite() +
            "}";
    }
}
