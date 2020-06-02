package com.pfe.covite.web.rest;

import com.pfe.covite.CoviteApp;
import com.pfe.covite.domain.Commande;
import com.pfe.covite.domain.Voiture;
import com.pfe.covite.domain.Position;
import com.pfe.covite.repository.CommandeRepository;
import com.pfe.covite.repository.search.CommandeSearchRepository;
import com.pfe.covite.service.CommandeService;
import com.pfe.covite.service.dto.CommandeCriteria;
import com.pfe.covite.service.CommandeQueryService;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.pfe.covite.domain.enumeration.Categorie;
import com.pfe.covite.domain.enumeration.Service;
/**
 * Integration tests for the {@link CommandeResource} REST controller.
 */
@SpringBootTest(classes = CoviteApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class CommandeResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE = LocalDate.ofEpochDay(-1L);

    private static final Double DEFAULT_PRIX = 1D;
    private static final Double UPDATED_PRIX = 2D;
    private static final Double SMALLER_PRIX = 1D - 1D;

    private static final Categorie DEFAULT_TYPE = Categorie.PERSONNE;
    private static final Categorie UPDATED_TYPE = Categorie.ANIMAL;

    private static final Service DEFAULT_TYPESERVICE = Service.TRANSPORT;
    private static final Service UPDATED_TYPESERVICE = Service.LIVRAISON;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private CommandeService commandeService;

    /**
     * This repository is mocked in the com.pfe.covite.repository.search test package.
     *
     * @see com.pfe.covite.repository.search.CommandeSearchRepositoryMockConfiguration
     */
    @Autowired
    private CommandeSearchRepository mockCommandeSearchRepository;

    @Autowired
    private CommandeQueryService commandeQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommandeMockMvc;

    private Commande commande;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commande createEntity(EntityManager em) {
        Commande commande = new Commande()
            .date(DEFAULT_DATE)
            .prix(DEFAULT_PRIX)
            .type(DEFAULT_TYPE)
            .typeservice(DEFAULT_TYPESERVICE);
        return commande;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commande createUpdatedEntity(EntityManager em) {
        Commande commande = new Commande()
            .date(UPDATED_DATE)
            .prix(UPDATED_PRIX)
            .type(UPDATED_TYPE)
            .typeservice(UPDATED_TYPESERVICE);
        return commande;
    }

    @BeforeEach
    public void initTest() {
        commande = createEntity(em);
    }

    @Test
    @Transactional
    public void createCommande() throws Exception {
        int databaseSizeBeforeCreate = commandeRepository.findAll().size();

        // Create the Commande
        restCommandeMockMvc.perform(post("/api/commandes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(commande)))
            .andExpect(status().isCreated());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeCreate + 1);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
        assertThat(testCommande.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testCommande.getPrix()).isEqualTo(DEFAULT_PRIX);
        assertThat(testCommande.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testCommande.getTypeservice()).isEqualTo(DEFAULT_TYPESERVICE);

        // Validate the Commande in Elasticsearch
        verify(mockCommandeSearchRepository, times(1)).save(testCommande);
    }

    @Test
    @Transactional
    public void createCommandeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = commandeRepository.findAll().size();

        // Create the Commande with an existing ID
        commande.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommandeMockMvc.perform(post("/api/commandes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(commande)))
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Commande in Elasticsearch
        verify(mockCommandeSearchRepository, times(0)).save(commande);
    }


    @Test
    @Transactional
    public void getAllCommandes() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList
        restCommandeMockMvc.perform(get("/api/commandes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commande.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].prix").value(hasItem(DEFAULT_PRIX.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].typeservice").value(hasItem(DEFAULT_TYPESERVICE.toString())));
    }
    
    @Test
    @Transactional
    public void getCommande() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get the commande
        restCommandeMockMvc.perform(get("/api/commandes/{id}", commande.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(commande.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.prix").value(DEFAULT_PRIX.doubleValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.typeservice").value(DEFAULT_TYPESERVICE.toString()));
    }


    @Test
    @Transactional
    public void getCommandesByIdFiltering() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        Long id = commande.getId();

        defaultCommandeShouldBeFound("id.equals=" + id);
        defaultCommandeShouldNotBeFound("id.notEquals=" + id);

        defaultCommandeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCommandeShouldNotBeFound("id.greaterThan=" + id);

        defaultCommandeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCommandeShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllCommandesByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where date equals to DEFAULT_DATE
        defaultCommandeShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the commandeList where date equals to UPDATED_DATE
        defaultCommandeShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllCommandesByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where date not equals to DEFAULT_DATE
        defaultCommandeShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the commandeList where date not equals to UPDATED_DATE
        defaultCommandeShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllCommandesByDateIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where date in DEFAULT_DATE or UPDATED_DATE
        defaultCommandeShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the commandeList where date equals to UPDATED_DATE
        defaultCommandeShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllCommandesByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where date is not null
        defaultCommandeShouldBeFound("date.specified=true");

        // Get all the commandeList where date is null
        defaultCommandeShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllCommandesByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where date is greater than or equal to DEFAULT_DATE
        defaultCommandeShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the commandeList where date is greater than or equal to UPDATED_DATE
        defaultCommandeShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllCommandesByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where date is less than or equal to DEFAULT_DATE
        defaultCommandeShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the commandeList where date is less than or equal to SMALLER_DATE
        defaultCommandeShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    public void getAllCommandesByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where date is less than DEFAULT_DATE
        defaultCommandeShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the commandeList where date is less than UPDATED_DATE
        defaultCommandeShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllCommandesByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where date is greater than DEFAULT_DATE
        defaultCommandeShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the commandeList where date is greater than SMALLER_DATE
        defaultCommandeShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }


    @Test
    @Transactional
    public void getAllCommandesByPrixIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prix equals to DEFAULT_PRIX
        defaultCommandeShouldBeFound("prix.equals=" + DEFAULT_PRIX);

        // Get all the commandeList where prix equals to UPDATED_PRIX
        defaultCommandeShouldNotBeFound("prix.equals=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    public void getAllCommandesByPrixIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prix not equals to DEFAULT_PRIX
        defaultCommandeShouldNotBeFound("prix.notEquals=" + DEFAULT_PRIX);

        // Get all the commandeList where prix not equals to UPDATED_PRIX
        defaultCommandeShouldBeFound("prix.notEquals=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    public void getAllCommandesByPrixIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prix in DEFAULT_PRIX or UPDATED_PRIX
        defaultCommandeShouldBeFound("prix.in=" + DEFAULT_PRIX + "," + UPDATED_PRIX);

        // Get all the commandeList where prix equals to UPDATED_PRIX
        defaultCommandeShouldNotBeFound("prix.in=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    public void getAllCommandesByPrixIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prix is not null
        defaultCommandeShouldBeFound("prix.specified=true");

        // Get all the commandeList where prix is null
        defaultCommandeShouldNotBeFound("prix.specified=false");
    }

    @Test
    @Transactional
    public void getAllCommandesByPrixIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prix is greater than or equal to DEFAULT_PRIX
        defaultCommandeShouldBeFound("prix.greaterThanOrEqual=" + DEFAULT_PRIX);

        // Get all the commandeList where prix is greater than or equal to UPDATED_PRIX
        defaultCommandeShouldNotBeFound("prix.greaterThanOrEqual=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    public void getAllCommandesByPrixIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prix is less than or equal to DEFAULT_PRIX
        defaultCommandeShouldBeFound("prix.lessThanOrEqual=" + DEFAULT_PRIX);

        // Get all the commandeList where prix is less than or equal to SMALLER_PRIX
        defaultCommandeShouldNotBeFound("prix.lessThanOrEqual=" + SMALLER_PRIX);
    }

    @Test
    @Transactional
    public void getAllCommandesByPrixIsLessThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prix is less than DEFAULT_PRIX
        defaultCommandeShouldNotBeFound("prix.lessThan=" + DEFAULT_PRIX);

        // Get all the commandeList where prix is less than UPDATED_PRIX
        defaultCommandeShouldBeFound("prix.lessThan=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    public void getAllCommandesByPrixIsGreaterThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prix is greater than DEFAULT_PRIX
        defaultCommandeShouldNotBeFound("prix.greaterThan=" + DEFAULT_PRIX);

        // Get all the commandeList where prix is greater than SMALLER_PRIX
        defaultCommandeShouldBeFound("prix.greaterThan=" + SMALLER_PRIX);
    }


    @Test
    @Transactional
    public void getAllCommandesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where type equals to DEFAULT_TYPE
        defaultCommandeShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the commandeList where type equals to UPDATED_TYPE
        defaultCommandeShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllCommandesByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where type not equals to DEFAULT_TYPE
        defaultCommandeShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the commandeList where type not equals to UPDATED_TYPE
        defaultCommandeShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllCommandesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultCommandeShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the commandeList where type equals to UPDATED_TYPE
        defaultCommandeShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllCommandesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where type is not null
        defaultCommandeShouldBeFound("type.specified=true");

        // Get all the commandeList where type is null
        defaultCommandeShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllCommandesByTypeserviceIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where typeservice equals to DEFAULT_TYPESERVICE
        defaultCommandeShouldBeFound("typeservice.equals=" + DEFAULT_TYPESERVICE);

        // Get all the commandeList where typeservice equals to UPDATED_TYPESERVICE
        defaultCommandeShouldNotBeFound("typeservice.equals=" + UPDATED_TYPESERVICE);
    }

    @Test
    @Transactional
    public void getAllCommandesByTypeserviceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where typeservice not equals to DEFAULT_TYPESERVICE
        defaultCommandeShouldNotBeFound("typeservice.notEquals=" + DEFAULT_TYPESERVICE);

        // Get all the commandeList where typeservice not equals to UPDATED_TYPESERVICE
        defaultCommandeShouldBeFound("typeservice.notEquals=" + UPDATED_TYPESERVICE);
    }

    @Test
    @Transactional
    public void getAllCommandesByTypeserviceIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where typeservice in DEFAULT_TYPESERVICE or UPDATED_TYPESERVICE
        defaultCommandeShouldBeFound("typeservice.in=" + DEFAULT_TYPESERVICE + "," + UPDATED_TYPESERVICE);

        // Get all the commandeList where typeservice equals to UPDATED_TYPESERVICE
        defaultCommandeShouldNotBeFound("typeservice.in=" + UPDATED_TYPESERVICE);
    }

    @Test
    @Transactional
    public void getAllCommandesByTypeserviceIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where typeservice is not null
        defaultCommandeShouldBeFound("typeservice.specified=true");

        // Get all the commandeList where typeservice is null
        defaultCommandeShouldNotBeFound("typeservice.specified=false");
    }

    @Test
    @Transactional
    public void getAllCommandesByVoitureIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);
        Voiture voiture = VoitureResourceIT.createEntity(em);
        em.persist(voiture);
        em.flush();
        commande.setVoiture(voiture);
        commandeRepository.saveAndFlush(commande);
        Long voitureId = voiture.getId();

        // Get all the commandeList where voiture equals to voitureId
        defaultCommandeShouldBeFound("voitureId.equals=" + voitureId);

        // Get all the commandeList where voiture equals to voitureId + 1
        defaultCommandeShouldNotBeFound("voitureId.equals=" + (voitureId + 1));
    }


    @Test
    @Transactional
    public void getAllCommandesByPositionIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);
        Position position = PositionResourceIT.createEntity(em);
        em.persist(position);
        em.flush();
        commande.setPosition(position);
        commandeRepository.saveAndFlush(commande);
        Long positionId = position.getId();

        // Get all the commandeList where position equals to positionId
        defaultCommandeShouldBeFound("positionId.equals=" + positionId);

        // Get all the commandeList where position equals to positionId + 1
        defaultCommandeShouldNotBeFound("positionId.equals=" + (positionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommandeShouldBeFound(String filter) throws Exception {
        restCommandeMockMvc.perform(get("/api/commandes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commande.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].prix").value(hasItem(DEFAULT_PRIX.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].typeservice").value(hasItem(DEFAULT_TYPESERVICE.toString())));

        // Check, that the count call also returns 1
        restCommandeMockMvc.perform(get("/api/commandes/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCommandeShouldNotBeFound(String filter) throws Exception {
        restCommandeMockMvc.perform(get("/api/commandes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommandeMockMvc.perform(get("/api/commandes/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingCommande() throws Exception {
        // Get the commande
        restCommandeMockMvc.perform(get("/api/commandes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCommande() throws Exception {
        // Initialize the database
        commandeService.save(commande);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCommandeSearchRepository);

        int databaseSizeBeforeUpdate = commandeRepository.findAll().size();

        // Update the commande
        Commande updatedCommande = commandeRepository.findById(commande.getId()).get();
        // Disconnect from session so that the updates on updatedCommande are not directly saved in db
        em.detach(updatedCommande);
        updatedCommande
            .date(UPDATED_DATE)
            .prix(UPDATED_PRIX)
            .type(UPDATED_TYPE)
            .typeservice(UPDATED_TYPESERVICE);

        restCommandeMockMvc.perform(put("/api/commandes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCommande)))
            .andExpect(status().isOk());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
        assertThat(testCommande.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testCommande.getPrix()).isEqualTo(UPDATED_PRIX);
        assertThat(testCommande.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCommande.getTypeservice()).isEqualTo(UPDATED_TYPESERVICE);

        // Validate the Commande in Elasticsearch
        verify(mockCommandeSearchRepository, times(1)).save(testCommande);
    }

    @Test
    @Transactional
    public void updateNonExistingCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().size();

        // Create the Commande

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandeMockMvc.perform(put("/api/commandes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(commande)))
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Commande in Elasticsearch
        verify(mockCommandeSearchRepository, times(0)).save(commande);
    }

    @Test
    @Transactional
    public void deleteCommande() throws Exception {
        // Initialize the database
        commandeService.save(commande);

        int databaseSizeBeforeDelete = commandeRepository.findAll().size();

        // Delete the commande
        restCommandeMockMvc.perform(delete("/api/commandes/{id}", commande.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Commande in Elasticsearch
        verify(mockCommandeSearchRepository, times(1)).deleteById(commande.getId());
    }

    @Test
    @Transactional
    public void searchCommande() throws Exception {
        // Initialize the database
        commandeService.save(commande);
        when(mockCommandeSearchRepository.search(queryStringQuery("id:" + commande.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(commande), PageRequest.of(0, 1), 1));
        // Search the commande
        restCommandeMockMvc.perform(get("/api/_search/commandes?query=id:" + commande.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commande.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].prix").value(hasItem(DEFAULT_PRIX.doubleValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].typeservice").value(hasItem(DEFAULT_TYPESERVICE.toString())));
    }
}
