package com.pfe.covite.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.pfe.covite.domain.Voiture;
import com.pfe.covite.domain.*; // for static metamodels
import com.pfe.covite.repository.VoitureRepository;
import com.pfe.covite.repository.search.VoitureSearchRepository;
import com.pfe.covite.service.dto.VoitureCriteria;

/**
 * Service for executing complex queries for {@link Voiture} entities in the database.
 * The main input is a {@link VoitureCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Voiture} or a {@link Page} of {@link Voiture} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VoitureQueryService extends QueryService<Voiture> {

    private final Logger log = LoggerFactory.getLogger(VoitureQueryService.class);

    private final VoitureRepository voitureRepository;

    private final VoitureSearchRepository voitureSearchRepository;

    public VoitureQueryService(VoitureRepository voitureRepository, VoitureSearchRepository voitureSearchRepository) {
        this.voitureRepository = voitureRepository;
        this.voitureSearchRepository = voitureSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Voiture} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Voiture> findByCriteria(VoitureCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Voiture> specification = createSpecification(criteria);
        return voitureRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Voiture} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Voiture> findByCriteria(VoitureCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Voiture> specification = createSpecification(criteria);
        return voitureRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VoitureCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Voiture> specification = createSpecification(criteria);
        return voitureRepository.count(specification);
    }

    /**
     * Function to convert {@link VoitureCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Voiture> createSpecification(VoitureCriteria criteria) {
        Specification<Voiture> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Voiture_.id));
            }
            if (criteria.getMatricule() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMatricule(), Voiture_.matricule));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Voiture_.type));
            }
            if (criteria.getCapacite() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCapacite(), Voiture_.capacite));
            }
            if (criteria.getCommandeId() != null) {
                specification = specification.and(buildSpecification(criteria.getCommandeId(),
                    root -> root.join(Voiture_.commandes, JoinType.LEFT).get(Commande_.id)));
            }
        }
        return specification;
    }
}
