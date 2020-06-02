package com.pfe.covite.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.pfe.covite.domain.enumeration.Typevehicule;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.pfe.covite.domain.Voiture} entity. This class is used
 * in {@link com.pfe.covite.web.rest.VoitureResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /voitures?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class VoitureCriteria implements Serializable, Criteria {
    /**
     * Class for filtering Typevehicule
     */
    public static class TypevehiculeFilter extends Filter<Typevehicule> {

        public TypevehiculeFilter() {
        }

        public TypevehiculeFilter(TypevehiculeFilter filter) {
            super(filter);
        }

        @Override
        public TypevehiculeFilter copy() {
            return new TypevehiculeFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter matricule;

    private TypevehiculeFilter type;

    private IntegerFilter capacite;

    private LongFilter commandeId;

    public VoitureCriteria() {
    }

    public VoitureCriteria(VoitureCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.matricule = other.matricule == null ? null : other.matricule.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.capacite = other.capacite == null ? null : other.capacite.copy();
        this.commandeId = other.commandeId == null ? null : other.commandeId.copy();
    }

    @Override
    public VoitureCriteria copy() {
        return new VoitureCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getMatricule() {
        return matricule;
    }

    public void setMatricule(StringFilter matricule) {
        this.matricule = matricule;
    }

    public TypevehiculeFilter getType() {
        return type;
    }

    public void setType(TypevehiculeFilter type) {
        this.type = type;
    }

    public IntegerFilter getCapacite() {
        return capacite;
    }

    public void setCapacite(IntegerFilter capacite) {
        this.capacite = capacite;
    }

    public LongFilter getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(LongFilter commandeId) {
        this.commandeId = commandeId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final VoitureCriteria that = (VoitureCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(matricule, that.matricule) &&
            Objects.equals(type, that.type) &&
            Objects.equals(capacite, that.capacite) &&
            Objects.equals(commandeId, that.commandeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        matricule,
        type,
        capacite,
        commandeId
        );
    }

    @Override
    public String toString() {
        return "VoitureCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (matricule != null ? "matricule=" + matricule + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (capacite != null ? "capacite=" + capacite + ", " : "") +
                (commandeId != null ? "commandeId=" + commandeId + ", " : "") +
            "}";
    }

}
