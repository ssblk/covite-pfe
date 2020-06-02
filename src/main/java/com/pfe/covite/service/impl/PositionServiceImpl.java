package com.pfe.covite.service.impl;

import com.pfe.covite.service.PositionService;
import com.pfe.covite.domain.Position;
import com.pfe.covite.repository.PositionRepository;
import com.pfe.covite.repository.search.PositionSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Position}.
 */
@Service
@Transactional
public class PositionServiceImpl implements PositionService {

    private final Logger log = LoggerFactory.getLogger(PositionServiceImpl.class);

    private final PositionRepository positionRepository;

    private final PositionSearchRepository positionSearchRepository;

    public PositionServiceImpl(PositionRepository positionRepository, PositionSearchRepository positionSearchRepository) {
        this.positionRepository = positionRepository;
        this.positionSearchRepository = positionSearchRepository;
    }

    /**
     * Save a position.
     *
     * @param position the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Position save(Position position) {
        log.debug("Request to save Position : {}", position);
        Position result = positionRepository.save(position);
        positionSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the positions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Position> findAll(Pageable pageable) {
        log.debug("Request to get all Positions");
        return positionRepository.findAll(pageable);
    }

    /**
     * Get one position by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Position> findOne(Long id) {
        log.debug("Request to get Position : {}", id);
        return positionRepository.findById(id);
    }

    /**
     * Delete the position by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Position : {}", id);
        positionRepository.deleteById(id);
        positionSearchRepository.deleteById(id);
    }

    /**
     * Search for the position corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Position> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Positions for query {}", query);
        return positionSearchRepository.search(queryStringQuery(query), pageable);    }
}
