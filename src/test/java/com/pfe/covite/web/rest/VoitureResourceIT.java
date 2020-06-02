package com.pfe.covite.web.rest;

import com.pfe.covite.CoviteApp;
import com.pfe.covite.domain.Voiture;
import com.pfe.covite.domain.Commande;
import com.pfe.covite.repository.VoitureRepository;
import com.pfe.covite.repository.search.VoitureSearchRepository;
import com.pfe.covite.service.VoitureService;
import com.pfe.covite.service.dto.VoitureCriteria;
import com.pfe.covite.service.VoitureQueryService;

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

import com.pfe.covite.domain.enumeration.Typevehicule;
/**
 * Integration tests for the {@link VoitureResource} REST controller.
 */
@SpringBootTest(classes = CoviteApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class VoitureResourceIT {

    private static final String DEFAULT_MATRICULE = "AAAAAAAAAA";
    private static final String UPDATED_MATRICULE = "BBBBBBBBBB";

    private static final Typevehicule DEFAULT_TYPE = Typevehicule.VOITURE;
    private static final Typevehicule UPDATED_TYPE = Typevehicule.CAMION;

    private static final Integer DEFAULT_CAPACITE = 1;
    private static final Integer UPDATED_CAPACITE = 2;
    private static final Integer SMALLER_CAPACITE = 1 - 1;

    @Autowired
    private VoitureRepository voitureRepository;

    @Autowired
    private VoitureService voitureService;

    /**
     * This repository is mocked in the com.pfe.covite.repository.search test package.
     *
     * @see com.pfe.covite.repository.search.VoitureSearchRepositoryMockConfiguration
     */
    @Autowired
    private VoitureSearchRepository mockVoitureSearchRepository;

    @Autowired
    private VoitureQueryService voitureQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVoitureMockMvc;

    private Voiture voiture;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Voiture createEntity(EntityManager em) {
        Voiture voiture = new Voiture()
            .matricule(DEFAULT_MATRICULE)
            .type(DEFAULT_TYPE)
            .capacite(DEFAULT_CAPACITE);
        return voiture;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Voiture createUpdatedEntity(EntityManager em) {
        Voiture voiture = new Voiture()
            .matricule(UPDATED_MATRICULE)
            .type(UPDATED_TYPE)
            .capacite(UPDATED_CAPACITE);
        return voiture;
    }

    @BeforeEach
    public void initTest() {
        voiture = createEntity(em);
    }

    @Test
    @Transactional
    public void createVoiture() throws Exception {
        int databaseSizeBeforeCreate = voitureRepository.findAll().size();

        // Create the Voiture
        restVoitureMockMvc.perform(post("/api/voitures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(voiture)))
            .andExpect(status().isCreated());

        // Validate the Voiture in the database
        List<Voiture> voitureList = voitureRepository.findAll();
        assertThat(voitureList).hasSize(databaseSizeBeforeCreate + 1);
        Voiture testVoiture = voitureList.get(voitureList.size() - 1);
        assertThat(testVoiture.getMatricule()).isEqualTo(DEFAULT_MATRICULE);
        assertThat(testVoiture.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testVoiture.getCapacite()).isEqualTo(DEFAULT_CAPACITE);

        // Validate the Voiture in Elasticsearch
        verify(mockVoitureSearchRepository, times(1)).save(testVoiture);
    }

    @Test
    @Transactional
    public void createVoitureWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = voitureRepository.findAll().size();

        // Create the Voiture with an existing ID
        voiture.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVoitureMockMvc.perform(post("/api/voitures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(voiture)))
            .andExpect(status().isBadRequest());

        // Validate the Voiture in the database
        List<Voiture> voitureList = voitureRepository.findAll();
        assertThat(voitureList).hasSize(databaseSizeBeforeCreate);

        // Validate the Voiture in Elasticsearch
        verify(mockVoitureSearchRepository, times(0)).save(voiture);
    }


    @Test
    @Transactional
    public void getAllVoitures() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList
        restVoitureMockMvc.perform(get("/api/voitures?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(voiture.getId().intValue())))
            .andExpect(jsonPath("$.[*].matricule").value(hasItem(DEFAULT_MATRICULE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].capacite").value(hasItem(DEFAULT_CAPACITE)));
    }
    
    @Test
    @Transactional
    public void getVoiture() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get the voiture
        restVoitureMockMvc.perform(get("/api/voitures/{id}", voiture.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(voiture.getId().intValue()))
            .andExpect(jsonPath("$.matricule").value(DEFAULT_MATRICULE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.capacite").value(DEFAULT_CAPACITE));
    }


    @Test
    @Transactional
    public void getVoituresByIdFiltering() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        Long id = voiture.getId();

        defaultVoitureShouldBeFound("id.equals=" + id);
        defaultVoitureShouldNotBeFound("id.notEquals=" + id);

        defaultVoitureShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultVoitureShouldNotBeFound("id.greaterThan=" + id);

        defaultVoitureShouldBeFound("id.lessThanOrEqual=" + id);
        defaultVoitureShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllVoituresByMatriculeIsEqualToSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where matricule equals to DEFAULT_MATRICULE
        defaultVoitureShouldBeFound("matricule.equals=" + DEFAULT_MATRICULE);

        // Get all the voitureList where matricule equals to UPDATED_MATRICULE
        defaultVoitureShouldNotBeFound("matricule.equals=" + UPDATED_MATRICULE);
    }

    @Test
    @Transactional
    public void getAllVoituresByMatriculeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where matricule not equals to DEFAULT_MATRICULE
        defaultVoitureShouldNotBeFound("matricule.notEquals=" + DEFAULT_MATRICULE);

        // Get all the voitureList where matricule not equals to UPDATED_MATRICULE
        defaultVoitureShouldBeFound("matricule.notEquals=" + UPDATED_MATRICULE);
    }

    @Test
    @Transactional
    public void getAllVoituresByMatriculeIsInShouldWork() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where matricule in DEFAULT_MATRICULE or UPDATED_MATRICULE
        defaultVoitureShouldBeFound("matricule.in=" + DEFAULT_MATRICULE + "," + UPDATED_MATRICULE);

        // Get all the voitureList where matricule equals to UPDATED_MATRICULE
        defaultVoitureShouldNotBeFound("matricule.in=" + UPDATED_MATRICULE);
    }

    @Test
    @Transactional
    public void getAllVoituresByMatriculeIsNullOrNotNull() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where matricule is not null
        defaultVoitureShouldBeFound("matricule.specified=true");

        // Get all the voitureList where matricule is null
        defaultVoitureShouldNotBeFound("matricule.specified=false");
    }
                @Test
    @Transactional
    public void getAllVoituresByMatriculeContainsSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where matricule contains DEFAULT_MATRICULE
        defaultVoitureShouldBeFound("matricule.contains=" + DEFAULT_MATRICULE);

        // Get all the voitureList where matricule contains UPDATED_MATRICULE
        defaultVoitureShouldNotBeFound("matricule.contains=" + UPDATED_MATRICULE);
    }

    @Test
    @Transactional
    public void getAllVoituresByMatriculeNotContainsSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where matricule does not contain DEFAULT_MATRICULE
        defaultVoitureShouldNotBeFound("matricule.doesNotContain=" + DEFAULT_MATRICULE);

        // Get all the voitureList where matricule does not contain UPDATED_MATRICULE
        defaultVoitureShouldBeFound("matricule.doesNotContain=" + UPDATED_MATRICULE);
    }


    @Test
    @Transactional
    public void getAllVoituresByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where type equals to DEFAULT_TYPE
        defaultVoitureShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the voitureList where type equals to UPDATED_TYPE
        defaultVoitureShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllVoituresByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where type not equals to DEFAULT_TYPE
        defaultVoitureShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the voitureList where type not equals to UPDATED_TYPE
        defaultVoitureShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllVoituresByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultVoitureShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the voitureList where type equals to UPDATED_TYPE
        defaultVoitureShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllVoituresByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where type is not null
        defaultVoitureShouldBeFound("type.specified=true");

        // Get all the voitureList where type is null
        defaultVoitureShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllVoituresByCapaciteIsEqualToSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where capacite equals to DEFAULT_CAPACITE
        defaultVoitureShouldBeFound("capacite.equals=" + DEFAULT_CAPACITE);

        // Get all the voitureList where capacite equals to UPDATED_CAPACITE
        defaultVoitureShouldNotBeFound("capacite.equals=" + UPDATED_CAPACITE);
    }

    @Test
    @Transactional
    public void getAllVoituresByCapaciteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where capacite not equals to DEFAULT_CAPACITE
        defaultVoitureShouldNotBeFound("capacite.notEquals=" + DEFAULT_CAPACITE);

        // Get all the voitureList where capacite not equals to UPDATED_CAPACITE
        defaultVoitureShouldBeFound("capacite.notEquals=" + UPDATED_CAPACITE);
    }

    @Test
    @Transactional
    public void getAllVoituresByCapaciteIsInShouldWork() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where capacite in DEFAULT_CAPACITE or UPDATED_CAPACITE
        defaultVoitureShouldBeFound("capacite.in=" + DEFAULT_CAPACITE + "," + UPDATED_CAPACITE);

        // Get all the voitureList where capacite equals to UPDATED_CAPACITE
        defaultVoitureShouldNotBeFound("capacite.in=" + UPDATED_CAPACITE);
    }

    @Test
    @Transactional
    public void getAllVoituresByCapaciteIsNullOrNotNull() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where capacite is not null
        defaultVoitureShouldBeFound("capacite.specified=true");

        // Get all the voitureList where capacite is null
        defaultVoitureShouldNotBeFound("capacite.specified=false");
    }

    @Test
    @Transactional
    public void getAllVoituresByCapaciteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where capacite is greater than or equal to DEFAULT_CAPACITE
        defaultVoitureShouldBeFound("capacite.greaterThanOrEqual=" + DEFAULT_CAPACITE);

        // Get all the voitureList where capacite is greater than or equal to UPDATED_CAPACITE
        defaultVoitureShouldNotBeFound("capacite.greaterThanOrEqual=" + UPDATED_CAPACITE);
    }

    @Test
    @Transactional
    public void getAllVoituresByCapaciteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where capacite is less than or equal to DEFAULT_CAPACITE
        defaultVoitureShouldBeFound("capacite.lessThanOrEqual=" + DEFAULT_CAPACITE);

        // Get all the voitureList where capacite is less than or equal to SMALLER_CAPACITE
        defaultVoitureShouldNotBeFound("capacite.lessThanOrEqual=" + SMALLER_CAPACITE);
    }

    @Test
    @Transactional
    public void getAllVoituresByCapaciteIsLessThanSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where capacite is less than DEFAULT_CAPACITE
        defaultVoitureShouldNotBeFound("capacite.lessThan=" + DEFAULT_CAPACITE);

        // Get all the voitureList where capacite is less than UPDATED_CAPACITE
        defaultVoitureShouldBeFound("capacite.lessThan=" + UPDATED_CAPACITE);
    }

    @Test
    @Transactional
    public void getAllVoituresByCapaciteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);

        // Get all the voitureList where capacite is greater than DEFAULT_CAPACITE
        defaultVoitureShouldNotBeFound("capacite.greaterThan=" + DEFAULT_CAPACITE);

        // Get all the voitureList where capacite is greater than SMALLER_CAPACITE
        defaultVoitureShouldBeFound("capacite.greaterThan=" + SMALLER_CAPACITE);
    }


    @Test
    @Transactional
    public void getAllVoituresByCommandeIsEqualToSomething() throws Exception {
        // Initialize the database
        voitureRepository.saveAndFlush(voiture);
        Commande commande = CommandeResourceIT.createEntity(em);
        em.persist(commande);
        em.flush();
        voiture.addCommande(commande);
        voitureRepository.saveAndFlush(voiture);
        Long commandeId = commande.getId();

        // Get all the voitureList where commande equals to commandeId
        defaultVoitureShouldBeFound("commandeId.equals=" + commandeId);

        // Get all the voitureList where commande equals to commandeId + 1
        defaultVoitureShouldNotBeFound("commandeId.equals=" + (commandeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVoitureShouldBeFound(String filter) throws Exception {
        restVoitureMockMvc.perform(get("/api/voitures?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(voiture.getId().intValue())))
            .andExpect(jsonPath("$.[*].matricule").value(hasItem(DEFAULT_MATRICULE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].capacite").value(hasItem(DEFAULT_CAPACITE)));

        // Check, that the count call also returns 1
        restVoitureMockMvc.perform(get("/api/voitures/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVoitureShouldNotBeFound(String filter) throws Exception {
        restVoitureMockMvc.perform(get("/api/voitures?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVoitureMockMvc.perform(get("/api/voitures/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingVoiture() throws Exception {
        // Get the voiture
        restVoitureMockMvc.perform(get("/api/voitures/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVoiture() throws Exception {
        // Initialize the database
        voitureService.save(voiture);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockVoitureSearchRepository);

        int databaseSizeBeforeUpdate = voitureRepository.findAll().size();

        // Update the voiture
        Voiture updatedVoiture = voitureRepository.findById(voiture.getId()).get();
        // Disconnect from session so that the updates on updatedVoiture are not directly saved in db
        em.detach(updatedVoiture);
        updatedVoiture
            .matricule(UPDATED_MATRICULE)
            .type(UPDATED_TYPE)
            .capacite(UPDATED_CAPACITE);

        restVoitureMockMvc.perform(put("/api/voitures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedVoiture)))
            .andExpect(status().isOk());

        // Validate the Voiture in the database
        List<Voiture> voitureList = voitureRepository.findAll();
        assertThat(voitureList).hasSize(databaseSizeBeforeUpdate);
        Voiture testVoiture = voitureList.get(voitureList.size() - 1);
        assertThat(testVoiture.getMatricule()).isEqualTo(UPDATED_MATRICULE);
        assertThat(testVoiture.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testVoiture.getCapacite()).isEqualTo(UPDATED_CAPACITE);

        // Validate the Voiture in Elasticsearch
        verify(mockVoitureSearchRepository, times(1)).save(testVoiture);
    }

    @Test
    @Transactional
    public void updateNonExistingVoiture() throws Exception {
        int databaseSizeBeforeUpdate = voitureRepository.findAll().size();

        // Create the Voiture

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVoitureMockMvc.perform(put("/api/voitures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(voiture)))
            .andExpect(status().isBadRequest());

        // Validate the Voiture in the database
        List<Voiture> voitureList = voitureRepository.findAll();
        assertThat(voitureList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Voiture in Elasticsearch
        verify(mockVoitureSearchRepository, times(0)).save(voiture);
    }

    @Test
    @Transactional
    public void deleteVoiture() throws Exception {
        // Initialize the database
        voitureService.save(voiture);

        int databaseSizeBeforeDelete = voitureRepository.findAll().size();

        // Delete the voiture
        restVoitureMockMvc.perform(delete("/api/voitures/{id}", voiture.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Voiture> voitureList = voitureRepository.findAll();
        assertThat(voitureList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Voiture in Elasticsearch
        verify(mockVoitureSearchRepository, times(1)).deleteById(voiture.getId());
    }

    @Test
    @Transactional
    public void searchVoiture() throws Exception {
        // Initialize the database
        voitureService.save(voiture);
        when(mockVoitureSearchRepository.search(queryStringQuery("id:" + voiture.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(voiture), PageRequest.of(0, 1), 1));
        // Search the voiture
        restVoitureMockMvc.perform(get("/api/_search/voitures?query=id:" + voiture.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(voiture.getId().intValue())))
            .andExpect(jsonPath("$.[*].matricule").value(hasItem(DEFAULT_MATRICULE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].capacite").value(hasItem(DEFAULT_CAPACITE)));
    }
}
