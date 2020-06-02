package com.pfe.covite.service;

import com.pfe.covite.domain.Voiture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Voiture}.
 */
public interface VoitureService {

    /**
     * Save a voiture.
     *
     * @param voiture the entity to save.
     * @return the persisted entity.
     */
    Voiture save(Voiture voiture);

    /**
     * Get all the voitures.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Voiture> findAll(Pageable pageable);

    /**
     * Get the "id" voiture.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Voiture> findOne(Long id);

    /**
     * Delete the "id" voiture.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the voiture corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Voiture> search(String query, Pageable pageable);
}
