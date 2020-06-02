package com.pfe.covite.web.rest;

import com.pfe.covite.CoviteApp;
import com.pfe.covite.domain.Position;
import com.pfe.covite.domain.Commande;
import com.pfe.covite.repository.PositionRepository;
import com.pfe.covite.repository.search.PositionSearchRepository;
import com.pfe.covite.service.PositionService;
import com.pfe.covite.service.dto.PositionCriteria;
import com.pfe.covite.service.PositionQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PositionResource} REST controller.
 */
@SpringBootTest(classes = CoviteApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class PositionResourceIT {

    private static final String DEFAULT_POINTDEPART = "AAAAAAAAAA";
    private static final String UPDATED_POINTDEPART = "BBBBBBBBBB";

    private static final String DEFAULT_POINTARRIVE = "AAAAAAAAAA";
    private static final String UPDATED_POINTARRIVE = "BBBBBBBBBB";

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PositionService positionService;

    /**
     * This repository is mocked in the com.pfe.covite.repository.search test package.
     *
     * @see com.pfe.covite.repository.search.PositionSearchRepositoryMockConfiguration
     */
    @Autowired
    private PositionSearchRepository mockPositionSearchRepository;

    @Autowired
    private PositionQueryService positionQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPositionMockMvc;

    private Position position;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Position createEntity(EntityManager em) {
        Position position = new Position()
            .pointdepart(DEFAULT_POINTDEPART)
            .pointarrive(DEFAULT_POINTARRIVE);
        return position;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Position createUpdatedEntity(EntityManager em) {
        Position position = new Position()
            .pointdepart(UPDATED_POINTDEPART)
            .pointarrive(UPDATED_POINTARRIVE);
        return position;
    }

    @BeforeEach
    public void initTest() {
        position = createEntity(em);
    }

    @Test
    @Transactional
    public void createPosition() throws Exception {
        int databaseSizeBeforeCreate = positionRepository.findAll().size();

        // Create the Position
        restPositionMockMvc.perform(post("/api/positions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(position)))
            .andExpect(status().isCreated());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeCreate + 1);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getPointdepart()).isEqualTo(DEFAULT_POINTDEPART);
        assertThat(testPosition.getPointarrive()).isEqualTo(DEFAULT_POINTARRIVE);

        // Validate the Position in Elasticsearch
        verify(mockPositionSearchRepository, times(1)).save(testPosition);
    }

    @Test
    @Transactional
    public void createPositionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = positionRepository.findAll().size();

        // Create the Position with an existing ID
        position.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPositionMockMvc.perform(post("/api/positions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(position)))
            .andExpect(status().isBadRequest());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeCreate);

        // Validate the Position in Elasticsearch
        verify(mockPositionSearchRepository, times(0)).save(position);
    }


    @Test
    @Transactional
    public void getAllPositions() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList
        restPositionMockMvc.perform(get("/api/positions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(position.getId().intValue())))
            .andExpect(jsonPath("$.[*].pointdepart").value(hasItem(DEFAULT_POINTDEPART)))
            .andExpect(jsonPath("$.[*].pointarrive").value(hasItem(DEFAULT_POINTARRIVE)));
    }
    
    @Test
    @Transactional
    public void getPosition() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get the position
        restPositionMockMvc.perform(get("/api/positions/{id}", position.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(position.getId().intValue()))
            .andExpect(jsonPath("$.pointdepart").value(DEFAULT_POINTDEPART))
            .andExpect(jsonPath("$.pointarrive").value(DEFAULT_POINTARRIVE));
    }


    @Test
    @Transactional
    public void getPositionsByIdFiltering() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        Long id = position.getId();

        defaultPositionShouldBeFound("id.equals=" + id);
        defaultPositionShouldNotBeFound("id.notEquals=" + id);

        defaultPositionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPositionShouldNotBeFound("id.greaterThan=" + id);

        defaultPositionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPositionShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPositionsByPointdepartIsEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointdepart equals to DEFAULT_POINTDEPART
        defaultPositionShouldBeFound("pointdepart.equals=" + DEFAULT_POINTDEPART);

        // Get all the positionList where pointdepart equals to UPDATED_POINTDEPART
        defaultPositionShouldNotBeFound("pointdepart.equals=" + UPDATED_POINTDEPART);
    }

    @Test
    @Transactional
    public void getAllPositionsByPointdepartIsNotEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointdepart not equals to DEFAULT_POINTDEPART
        defaultPositionShouldNotBeFound("pointdepart.notEquals=" + DEFAULT_POINTDEPART);

        // Get all the positionList where pointdepart not equals to UPDATED_POINTDEPART
        defaultPositionShouldBeFound("pointdepart.notEquals=" + UPDATED_POINTDEPART);
    }

    @Test
    @Transactional
    public void getAllPositionsByPointdepartIsInShouldWork() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointdepart in DEFAULT_POINTDEPART or UPDATED_POINTDEPART
        defaultPositionShouldBeFound("pointdepart.in=" + DEFAULT_POINTDEPART + "," + UPDATED_POINTDEPART);

        // Get all the positionList where pointdepart equals to UPDATED_POINTDEPART
        defaultPositionShouldNotBeFound("pointdepart.in=" + UPDATED_POINTDEPART);
    }

    @Test
    @Transactional
    public void getAllPositionsByPointdepartIsNullOrNotNull() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointdepart is not null
        defaultPositionShouldBeFound("pointdepart.specified=true");

        // Get all the positionList where pointdepart is null
        defaultPositionShouldNotBeFound("pointdepart.specified=false");
    }
                @Test
    @Transactional
    public void getAllPositionsByPointdepartContainsSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointdepart contains DEFAULT_POINTDEPART
        defaultPositionShouldBeFound("pointdepart.contains=" + DEFAULT_POINTDEPART);

        // Get all the positionList where pointdepart contains UPDATED_POINTDEPART
        defaultPositionShouldNotBeFound("pointdepart.contains=" + UPDATED_POINTDEPART);
    }

    @Test
    @Transactional
    public void getAllPositionsByPointdepartNotContainsSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointdepart does not contain DEFAULT_POINTDEPART
        defaultPositionShouldNotBeFound("pointdepart.doesNotContain=" + DEFAULT_POINTDEPART);

        // Get all the positionList where pointdepart does not contain UPDATED_POINTDEPART
        defaultPositionShouldBeFound("pointdepart.doesNotContain=" + UPDATED_POINTDEPART);
    }


    @Test
    @Transactional
    public void getAllPositionsByPointarriveIsEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointarrive equals to DEFAULT_POINTARRIVE
        defaultPositionShouldBeFound("pointarrive.equals=" + DEFAULT_POINTARRIVE);

        // Get all the positionList where pointarrive equals to UPDATED_POINTARRIVE
        defaultPositionShouldNotBeFound("pointarrive.equals=" + UPDATED_POINTARRIVE);
    }

    @Test
    @Transactional
    public void getAllPositionsByPointarriveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointarrive not equals to DEFAULT_POINTARRIVE
        defaultPositionShouldNotBeFound("pointarrive.notEquals=" + DEFAULT_POINTARRIVE);

        // Get all the positionList where pointarrive not equals to UPDATED_POINTARRIVE
        defaultPositionShouldBeFound("pointarrive.notEquals=" + UPDATED_POINTARRIVE);
    }

    @Test
    @Transactional
    public void getAllPositionsByPointarriveIsInShouldWork() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointarrive in DEFAULT_POINTARRIVE or UPDATED_POINTARRIVE
        defaultPositionShouldBeFound("pointarrive.in=" + DEFAULT_POINTARRIVE + "," + UPDATED_POINTARRIVE);

        // Get all the positionList where pointarrive equals to UPDATED_POINTARRIVE
        defaultPositionShouldNotBeFound("pointarrive.in=" + UPDATED_POINTARRIVE);
    }

    @Test
    @Transactional
    public void getAllPositionsByPointarriveIsNullOrNotNull() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointarrive is not null
        defaultPositionShouldBeFound("pointarrive.specified=true");

        // Get all the positionList where pointarrive is null
        defaultPositionShouldNotBeFound("pointarrive.specified=false");
    }
                @Test
    @Transactional
    public void getAllPositionsByPointarriveContainsSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointarrive contains DEFAULT_POINTARRIVE
        defaultPositionShouldBeFound("pointarrive.contains=" + DEFAULT_POINTARRIVE);

        // Get all the positionList where pointarrive contains UPDATED_POINTARRIVE
        defaultPositionShouldNotBeFound("pointarrive.contains=" + UPDATED_POINTARRIVE);
    }

    @Test
    @Transactional
    public void getAllPositionsByPointarriveNotContainsSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where pointarrive does not contain DEFAULT_POINTARRIVE
        defaultPositionShouldNotBeFound("pointarrive.doesNotContain=" + DEFAULT_POINTARRIVE);

        // Get all the positionList where pointarrive does not contain UPDATED_POINTARRIVE
        defaultPositionShouldBeFound("pointarrive.doesNotContain=" + UPDATED_POINTARRIVE);
    }


    @Test
    @Transactional
    public void getAllPositionsByCommandeIsEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);
        Commande commande = CommandeResourceIT.createEntity(em);
        em.persist(commande);
        em.flush();
        position.addCommande(commande);
        positionRepository.saveAndFlush(position);
        Long commandeId = commande.getId();

        // Get all the positionList where commande equals to commandeId
        defaultPositionShouldBeFound("commandeId.equals=" + commandeId);

        // Get all the positionList where commande equals to commandeId + 1
        defaultPositionShouldNotBeFound("commandeId.equals=" + (commandeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPositionShouldBeFound(String filter) throws Exception {
        restPositionMockMvc.perform(get("/api/positions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(position.getId().intValue())))
            .andExpect(jsonPath("$.[*].pointdepart").value(hasItem(DEFAULT_POINTDEPART)))
            .andExpect(jsonPath("$.[*].pointarrive").value(hasItem(DEFAULT_POINTARRIVE)));

        // Check, that the count call also returns 1
        restPositionMockMvc.perform(get("/api/positions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPositionShouldNotBeFound(String filter) throws Exception {
        restPositionMockMvc.perform(get("/api/positions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPositionMockMvc.perform(get("/api/positions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPosition() throws Exception {
        // Get the position
        restPositionMockMvc.perform(get("/api/positions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePosition() throws Exception {
        // Initialize the database
        positionService.save(position);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockPositionSearchRepository);

        int databaseSizeBeforeUpdate = positionRepository.findAll().size();

        // Update the position
        Position updatedPosition = positionRepository.findById(position.getId()).get();
        // Disconnect from session so that the updates on updatedPosition are not directly saved in db
        em.detach(updatedPosition);
        updatedPosition
            .pointdepart(UPDATED_POINTDEPART)
            .pointarrive(UPDATED_POINTARRIVE);

        restPositionMockMvc.perform(put("/api/positions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedPosition)))
            .andExpect(status().isOk());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getPointdepart()).isEqualTo(UPDATED_POINTDEPART);
        assertThat(testPosition.getPointarrive()).isEqualTo(UPDATED_POINTARRIVE);

        // Validate the Position in Elasticsearch
        verify(mockPositionSearchRepository, times(1)).save(testPosition);
    }

    @Test
    @Transactional
    public void updateNonExistingPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();

        // Create the Position

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPositionMockMvc.perform(put("/api/positions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(position)))
            .andExpect(status().isBadRequest());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Position in Elasticsearch
        verify(mockPositionSearchRepository, times(0)).save(position);
    }

    @Test
    @Transactional
    public void deletePosition() throws Exception {
        // Initialize the database
        positionService.save(position);

        int databaseSizeBeforeDelete = positionRepository.findAll().size();

        // Delete the position
        restPositionMockMvc.perform(delete("/api/positions/{id}", position.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Position in Elasticsearch
        verify(mockPositionSearchRepository, times(1)).deleteById(position.getId());
    }

    @Test
    @Transactional
    public void searchPosition() throws Exception {
        // Initialize the database
        positionService.save(position);
        when(mockPositionSearchRepository.search(queryStringQuery("id:" + position.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(position), PageRequest.of(0, 1), 1));
        // Search the position
        restPositionMockMvc.perform(get("/api/_search/positions?query=id:" + position.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(position.getId().intValue())))
            .andExpect(jsonPath("$.[*].pointdepart").value(hasItem(DEFAULT_POINTDEPART)))
            .andExpect(jsonPath("$.[*].pointarrive").value(hasItem(DEFAULT_POINTARRIVE)));
    }
}
