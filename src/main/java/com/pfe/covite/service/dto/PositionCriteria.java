package com.pfe.covite.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.pfe.covite.domain.Position} entity. This class is used
 * in {@link com.pfe.covite.web.rest.PositionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /positions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PositionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter pointdepart;

    private StringFilter pointarrive;

    private LongFilter commandeId;

    public PositionCriteria() {
    }

    public PositionCriteria(PositionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.pointdepart = other.pointdepart == null ? null : other.pointdepart.copy();
        this.pointarrive = other.pointarrive == null ? null : other.pointarrive.copy();
        this.commandeId = other.commandeId == null ? null : other.commandeId.copy();
    }

    @Override
    public PositionCriteria copy() {
        return new PositionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getPointdepart() {
        return pointdepart;
    }

    public void setPointdepart(StringFilter pointdepart) {
        this.pointdepart = pointdepart;
    }

    public StringFilter getPointarrive() {
        return pointarrive;
    }

    public void setPointarrive(StringFilter pointarrive) {
        this.pointarrive = pointarrive;
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
        final PositionCriteria that = (PositionCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(pointdepart, that.pointdepart) &&
            Objects.equals(pointarrive, that.pointarrive) &&
            Objects.equals(commandeId, that.commandeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        pointdepart,
        pointarrive,
        commandeId
        );
    }

    @Override
    public String toString() {
        return "PositionCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (pointdepart != null ? "pointdepart=" + pointdepart + ", " : "") +
                (pointarrive != null ? "pointarrive=" + pointarrive + ", " : "") +
                (commandeId != null ? "commandeId=" + commandeId + ", " : "") +
            "}";
    }

}
