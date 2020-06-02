package com.pfe.covite.service;

import com.pfe.covite.domain.Position;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Position}.
 */
public interface PositionService {

    /**
     * Save a position.
     *
     * @param position the entity to save.
     * @return the persisted entity.
     */
    Position save(Position position);

    /**
     * Get all the positions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Position> findAll(Pageable pageable);

    /**
     * Get the "id" position.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Position> findOne(Long id);

    /**
     * Delete the "id" position.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the position corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Position> search(String query, Pageable pageable);
}
