package com.pingouincorp.web.rest;

import com.pingouincorp.MenhirApp;
import com.pingouincorp.domain.Individu;
import com.pingouincorp.repository.IndividuRepository;
import com.pingouincorp.service.IndividuService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.pingouincorp.domain.enumeration.Couleur;
/**
 * Integration tests for the {@link IndividuResource} REST controller.
 */
@SpringBootTest(classes = MenhirApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class IndividuResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Double DEFAULT_TAILLE = 1D;
    private static final Double UPDATED_TAILLE = 2D;

    private static final Instant DEFAULT_DATE_DE_NAISSANCE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_DE_NAISSANCE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Couleur DEFAULT_COULEUR_CHEVEUX = Couleur.AUTRE;
    private static final Couleur UPDATED_COULEUR_CHEVEUX = Couleur.BLANC;

    private static final String DEFAULT_COIFFURE = "AAAAAAAAAA";
    private static final String UPDATED_COIFFURE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    @Autowired
    private IndividuRepository individuRepository;

    @Mock
    private IndividuRepository individuRepositoryMock;

    @Mock
    private IndividuService individuServiceMock;

    @Autowired
    private IndividuService individuService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIndividuMockMvc;

    private Individu individu;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Individu createEntity(EntityManager em) {
        Individu individu = new Individu()
            .nom(DEFAULT_NOM)
            .taille(DEFAULT_TAILLE)
            .dateDeNaissance(DEFAULT_DATE_DE_NAISSANCE)
            .couleurCheveux(DEFAULT_COULEUR_CHEVEUX)
            .coiffure(DEFAULT_COIFFURE)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE);
        return individu;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Individu createUpdatedEntity(EntityManager em) {
        Individu individu = new Individu()
            .nom(UPDATED_NOM)
            .taille(UPDATED_TAILLE)
            .dateDeNaissance(UPDATED_DATE_DE_NAISSANCE)
            .couleurCheveux(UPDATED_COULEUR_CHEVEUX)
            .coiffure(UPDATED_COIFFURE)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE);
        return individu;
    }

    @BeforeEach
    public void initTest() {
        individu = createEntity(em);
    }

    @Test
    @Transactional
    public void createIndividu() throws Exception {
        int databaseSizeBeforeCreate = individuRepository.findAll().size();
        // Create the Individu
        restIndividuMockMvc.perform(post("/api/individus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(individu)))
            .andExpect(status().isCreated());

        // Validate the Individu in the database
        List<Individu> individuList = individuRepository.findAll();
        assertThat(individuList).hasSize(databaseSizeBeforeCreate + 1);
        Individu testIndividu = individuList.get(individuList.size() - 1);
        assertThat(testIndividu.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testIndividu.getTaille()).isEqualTo(DEFAULT_TAILLE);
        assertThat(testIndividu.getDateDeNaissance()).isEqualTo(DEFAULT_DATE_DE_NAISSANCE);
        assertThat(testIndividu.getCouleurCheveux()).isEqualTo(DEFAULT_COULEUR_CHEVEUX);
        assertThat(testIndividu.getCoiffure()).isEqualTo(DEFAULT_COIFFURE);
        assertThat(testIndividu.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testIndividu.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createIndividuWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = individuRepository.findAll().size();

        // Create the Individu with an existing ID
        individu.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restIndividuMockMvc.perform(post("/api/individus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(individu)))
            .andExpect(status().isBadRequest());

        // Validate the Individu in the database
        List<Individu> individuList = individuRepository.findAll();
        assertThat(individuList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllIndividus() throws Exception {
        // Initialize the database
        individuRepository.saveAndFlush(individu);

        // Get all the individuList
        restIndividuMockMvc.perform(get("/api/individus?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(individu.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].taille").value(hasItem(DEFAULT_TAILLE.doubleValue())))
            .andExpect(jsonPath("$.[*].dateDeNaissance").value(hasItem(DEFAULT_DATE_DE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].couleurCheveux").value(hasItem(DEFAULT_COULEUR_CHEVEUX.toString())))
            .andExpect(jsonPath("$.[*].coiffure").value(hasItem(DEFAULT_COIFFURE)))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllIndividusWithEagerRelationshipsIsEnabled() throws Exception {
        when(individuServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIndividuMockMvc.perform(get("/api/individus?eagerload=true"))
            .andExpect(status().isOk());

        verify(individuServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllIndividusWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(individuServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIndividuMockMvc.perform(get("/api/individus?eagerload=true"))
            .andExpect(status().isOk());

        verify(individuServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getIndividu() throws Exception {
        // Initialize the database
        individuRepository.saveAndFlush(individu);

        // Get the individu
        restIndividuMockMvc.perform(get("/api/individus/{id}", individu.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(individu.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.taille").value(DEFAULT_TAILLE.doubleValue()))
            .andExpect(jsonPath("$.dateDeNaissance").value(DEFAULT_DATE_DE_NAISSANCE.toString()))
            .andExpect(jsonPath("$.couleurCheveux").value(DEFAULT_COULEUR_CHEVEUX.toString()))
            .andExpect(jsonPath("$.coiffure").value(DEFAULT_COIFFURE))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)));
    }
    @Test
    @Transactional
    public void getNonExistingIndividu() throws Exception {
        // Get the individu
        restIndividuMockMvc.perform(get("/api/individus/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIndividu() throws Exception {
        // Initialize the database
        individuService.save(individu);

        int databaseSizeBeforeUpdate = individuRepository.findAll().size();

        // Update the individu
        Individu updatedIndividu = individuRepository.findById(individu.getId()).get();
        // Disconnect from session so that the updates on updatedIndividu are not directly saved in db
        em.detach(updatedIndividu);
        updatedIndividu
            .nom(UPDATED_NOM)
            .taille(UPDATED_TAILLE)
            .dateDeNaissance(UPDATED_DATE_DE_NAISSANCE)
            .couleurCheveux(UPDATED_COULEUR_CHEVEUX)
            .coiffure(UPDATED_COIFFURE)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE);

        restIndividuMockMvc.perform(put("/api/individus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedIndividu)))
            .andExpect(status().isOk());

        // Validate the Individu in the database
        List<Individu> individuList = individuRepository.findAll();
        assertThat(individuList).hasSize(databaseSizeBeforeUpdate);
        Individu testIndividu = individuList.get(individuList.size() - 1);
        assertThat(testIndividu.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testIndividu.getTaille()).isEqualTo(UPDATED_TAILLE);
        assertThat(testIndividu.getDateDeNaissance()).isEqualTo(UPDATED_DATE_DE_NAISSANCE);
        assertThat(testIndividu.getCouleurCheveux()).isEqualTo(UPDATED_COULEUR_CHEVEUX);
        assertThat(testIndividu.getCoiffure()).isEqualTo(UPDATED_COIFFURE);
        assertThat(testIndividu.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testIndividu.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingIndividu() throws Exception {
        int databaseSizeBeforeUpdate = individuRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIndividuMockMvc.perform(put("/api/individus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(individu)))
            .andExpect(status().isBadRequest());

        // Validate the Individu in the database
        List<Individu> individuList = individuRepository.findAll();
        assertThat(individuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteIndividu() throws Exception {
        // Initialize the database
        individuService.save(individu);

        int databaseSizeBeforeDelete = individuRepository.findAll().size();

        // Delete the individu
        restIndividuMockMvc.perform(delete("/api/individus/{id}", individu.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Individu> individuList = individuRepository.findAll();
        assertThat(individuList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
