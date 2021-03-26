package com.pingouincorp.web.rest;

import com.pingouincorp.MenhirApp;
import com.pingouincorp.domain.Lieu;
import com.pingouincorp.repository.LieuRepository;
import com.pingouincorp.service.LieuService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link LieuResource} REST controller.
 */
@SpringBootTest(classes = MenhirApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class LieuResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    @Autowired
    private LieuRepository lieuRepository;

    @Autowired
    private LieuService lieuService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLieuMockMvc;

    private Lieu lieu;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lieu createEntity(EntityManager em) {
        Lieu lieu = new Lieu()
            .nom(DEFAULT_NOM)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE);
        return lieu;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lieu createUpdatedEntity(EntityManager em) {
        Lieu lieu = new Lieu()
            .nom(UPDATED_NOM)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        return lieu;
    }

    @BeforeEach
    public void initTest() {
        lieu = createEntity(em);
    }

    @Test
    @Transactional
    public void createLieu() throws Exception {
        int databaseSizeBeforeCreate = lieuRepository.findAll().size();
        // Create the Lieu
        restLieuMockMvc.perform(post("/api/lieus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(lieu)))
            .andExpect(status().isCreated());

        // Validate the Lieu in the database
        List<Lieu> lieuList = lieuRepository.findAll();
        assertThat(lieuList).hasSize(databaseSizeBeforeCreate + 1);
        Lieu testLieu = lieuList.get(lieuList.size() - 1);
        assertThat(testLieu.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testLieu.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testLieu.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    public void createLieuWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = lieuRepository.findAll().size();

        // Create the Lieu with an existing ID
        lieu.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLieuMockMvc.perform(post("/api/lieus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(lieu)))
            .andExpect(status().isBadRequest());

        // Validate the Lieu in the database
        List<Lieu> lieuList = lieuRepository.findAll();
        assertThat(lieuList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllLieus() throws Exception {
        // Initialize the database
        lieuRepository.saveAndFlush(lieu);

        // Get all the lieuList
        restLieuMockMvc.perform(get("/api/lieus?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lieu.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getLieu() throws Exception {
        // Initialize the database
        lieuRepository.saveAndFlush(lieu);

        // Get the lieu
        restLieuMockMvc.perform(get("/api/lieus/{id}", lieu.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lieu.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()));
    }
    @Test
    @Transactional
    public void getNonExistingLieu() throws Exception {
        // Get the lieu
        restLieuMockMvc.perform(get("/api/lieus/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLieu() throws Exception {
        // Initialize the database
        lieuService.save(lieu);

        int databaseSizeBeforeUpdate = lieuRepository.findAll().size();

        // Update the lieu
        Lieu updatedLieu = lieuRepository.findById(lieu.getId()).get();
        // Disconnect from session so that the updates on updatedLieu are not directly saved in db
        em.detach(updatedLieu);
        updatedLieu
            .nom(UPDATED_NOM)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restLieuMockMvc.perform(put("/api/lieus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedLieu)))
            .andExpect(status().isOk());

        // Validate the Lieu in the database
        List<Lieu> lieuList = lieuRepository.findAll();
        assertThat(lieuList).hasSize(databaseSizeBeforeUpdate);
        Lieu testLieu = lieuList.get(lieuList.size() - 1);
        assertThat(testLieu.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testLieu.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testLieu.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    public void updateNonExistingLieu() throws Exception {
        int databaseSizeBeforeUpdate = lieuRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLieuMockMvc.perform(put("/api/lieus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(lieu)))
            .andExpect(status().isBadRequest());

        // Validate the Lieu in the database
        List<Lieu> lieuList = lieuRepository.findAll();
        assertThat(lieuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLieu() throws Exception {
        // Initialize the database
        lieuService.save(lieu);

        int databaseSizeBeforeDelete = lieuRepository.findAll().size();

        // Delete the lieu
        restLieuMockMvc.perform(delete("/api/lieus/{id}", lieu.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Lieu> lieuList = lieuRepository.findAll();
        assertThat(lieuList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
