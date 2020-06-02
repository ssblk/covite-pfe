package com.pfe.covite.service.impl;

import com.pfe.covite.service.VoitureService;
import com.pfe.covite.domain.Voiture;
import com.pfe.covite.repository.VoitureRepository;
import com.pfe.covite.repository.search.VoitureSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Voiture}.
 */
@Service
@Transactional
public class VoitureServiceImpl implements VoitureService {

    private final Logger log = LoggerFactory.getLogger(VoitureServiceImpl.class);

    private final VoitureRepository voitureRepository;

    private final VoitureSearchRepository voitureSearchRepository;

    public VoitureServiceImpl(VoitureRepository voitureRepository, VoitureSearchRepository voitureSearchRepository) {
        this.voitureRepository = voitureRepository;
        this.voitureSearchRepository = voitureSearchRepository;
    }

    /**
     * Save a voiture.
     *
     * @param voiture the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Voiture save(Voiture voiture) {
        log.debug("Request to save Voiture : {}", voiture);
        Voiture result = voitureRepository.save(voiture);
        voitureSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the voitures.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Voiture> findAll(Pageable pageable) {
        log.debug("Request to get all Voitures");
        return voitureRepository.findAll(pageable);
    }

    /**
     * Get one voiture by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Voiture> findOne(Long id) {
        log.debug("Request to get Voiture : {}", id);
        return voitureRepository.findById(id);
    }

    /**
     * Delete the voiture by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Voiture : {}", id);
        voitureRepository.deleteById(id);
        voitureSearchRepository.deleteById(id);
    }

    /**
     * Search for the voiture corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Voiture> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Voitures for query {}", query);
        return voitureSearchRepository.search(queryStringQuery(query), pageable);    }
}
