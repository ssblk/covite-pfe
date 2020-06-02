package com.pfe.covite.web.rest;

import com.pfe.covite.domain.Voiture;
import com.pfe.covite.service.VoitureService;
import com.pfe.covite.web.rest.errors.BadRequestAlertException;
import com.pfe.covite.service.dto.VoitureCriteria;
import com.pfe.covite.service.VoitureQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.pfe.covite.domain.Voiture}.
 */
@RestController
@RequestMapping("/api")
public class VoitureResource {

    private final Logger log = LoggerFactory.getLogger(VoitureResource.class);

    private static final String ENTITY_NAME = "voiture";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VoitureService voitureService;

    private final VoitureQueryService voitureQueryService;

    public VoitureResource(VoitureService voitureService, VoitureQueryService voitureQueryService) {
        this.voitureService = voitureService;
        this.voitureQueryService = voitureQueryService;
    }

    /**
     * {@code POST  /voitures} : Create a new voiture.
     *
     * @param voiture the voiture to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new voiture, or with status {@code 400 (Bad Request)} if the voiture has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/voitures")
    public ResponseEntity<Voiture> createVoiture(@RequestBody Voiture voiture) throws URISyntaxException {
        log.debug("REST request to save Voiture : {}", voiture);
        if (voiture.getId() != null) {
            throw new BadRequestAlertException("A new voiture cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Voiture result = voitureService.save(voiture);
        return ResponseEntity.created(new URI("/api/voitures/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /voitures} : Updates an existing voiture.
     *
     * @param voiture the voiture to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated voiture,
     * or with status {@code 400 (Bad Request)} if the voiture is not valid,
     * or with status {@code 500 (Internal Server Error)} if the voiture couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/voitures")
    public ResponseEntity<Voiture> updateVoiture(@RequestBody Voiture voiture) throws URISyntaxException {
        log.debug("REST request to update Voiture : {}", voiture);
        if (voiture.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Voiture result = voitureService.save(voiture);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, voiture.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /voitures} : get all the voitures.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of voitures in body.
     */
    @GetMapping("/voitures")
    public ResponseEntity<List<Voiture>> getAllVoitures(VoitureCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Voitures by criteria: {}", criteria);
        Page<Voiture> page = voitureQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /voitures/count} : count all the voitures.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/voitures/count")
    public ResponseEntity<Long> countVoitures(VoitureCriteria criteria) {
        log.debug("REST request to count Voitures by criteria: {}", criteria);
        return ResponseEntity.ok().body(voitureQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /voitures/:id} : get the "id" voiture.
     *
     * @param id the id of the voiture to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the voiture, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/voitures/{id}")
    public ResponseEntity<Voiture> getVoiture(@PathVariable Long id) {
        log.debug("REST request to get Voiture : {}", id);
        Optional<Voiture> voiture = voitureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(voiture);
    }

    /**
     * {@code DELETE  /voitures/:id} : delete the "id" voiture.
     *
     * @param id the id of the voiture to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/voitures/{id}")
    public ResponseEntity<Void> deleteVoiture(@PathVariable Long id) {
        log.debug("REST request to delete Voiture : {}", id);
        voitureService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/voitures?query=:query} : search for the voiture corresponding
     * to the query.
     *
     * @param query the query of the voiture search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/voitures")
    public ResponseEntity<List<Voiture>> searchVoitures(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Voitures for query {}", query);
        Page<Voiture> page = voitureService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
