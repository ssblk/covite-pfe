package com.pfe.covite.service.impl;

import com.pfe.covite.service.CommandeService;
import com.pfe.covite.domain.Commande;
import com.pfe.covite.repository.CommandeRepository;
import com.pfe.covite.repository.search.CommandeSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Commande}.
 */
@Service
@Transactional
public class CommandeServiceImpl implements CommandeService {

    private final Logger log = LoggerFactory.getLogger(CommandeServiceImpl.class);

    private final CommandeRepository commandeRepository;

    private final CommandeSearchRepository commandeSearchRepository;

    public CommandeServiceImpl(CommandeRepository commandeRepository, CommandeSearchRepository commandeSearchRepository) {
        this.commandeRepository = commandeRepository;
        this.commandeSearchRepository = commandeSearchRepository;
    }

    /**
     * Save a commande.
     *
     * @param commande the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Commande save(Commande commande) {
        log.debug("Request to save Commande : {}", commande);
        Commande result = commandeRepository.save(commande);
        commandeSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the commandes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Commande> findAll(Pageable pageable) {
        log.debug("Request to get all Commandes");
        return commandeRepository.findAll(pageable);
    }

    /**
     * Get one commande by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Commande> findOne(Long id) {
        log.debug("Request to get Commande : {}", id);
        return commandeRepository.findById(id);
    }

    /**
     * Delete the commande by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Commande : {}", id);
        commandeRepository.deleteById(id);
        commandeSearchRepository.deleteById(id);
    }

    /**
     * Search for the commande corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Commande> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Commandes for query {}", query);
        return commandeSearchRepository.search(queryStringQuery(query), pageable);    }
}
