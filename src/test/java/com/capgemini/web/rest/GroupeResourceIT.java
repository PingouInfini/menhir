package com.capgemini.web.rest;

import com.capgemini.MenhirApp;
import com.capgemini.domain.Groupe;
import com.capgemini.repository.GroupeRepository;
import com.capgemini.service.GroupeService;

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

/**
 * Integration tests for the {@link GroupeResource} REST controller.
 */
@SpringBootTest(classes = MenhirApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class GroupeResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final byte[] DEFAULT_PIECE_JOINTE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PIECE_JOINTE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PIECE_JOINTE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PIECE_JOINTE_CONTENT_TYPE = "image/png";

    @Autowired
    private GroupeRepository groupeRepository;

    @Mock
    private GroupeRepository groupeRepositoryMock;

    @Mock
    private GroupeService groupeServiceMock;

    @Autowired
    private GroupeService groupeService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGroupeMockMvc;

    private Groupe groupe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Groupe createEntity(EntityManager em) {
        Groupe groupe = new Groupe()
            .nom(DEFAULT_NOM)
            .description(DEFAULT_DESCRIPTION)
            .adresse(DEFAULT_ADRESSE)
            .dateCreation(DEFAULT_DATE_CREATION)
            .pieceJointe(DEFAULT_PIECE_JOINTE)
            .pieceJointeContentType(DEFAULT_PIECE_JOINTE_CONTENT_TYPE);
        return groupe;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Groupe createUpdatedEntity(EntityManager em) {
        Groupe groupe = new Groupe()
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .adresse(UPDATED_ADRESSE)
            .dateCreation(UPDATED_DATE_CREATION)
            .pieceJointe(UPDATED_PIECE_JOINTE)
            .pieceJointeContentType(UPDATED_PIECE_JOINTE_CONTENT_TYPE);
        return groupe;
    }

    @BeforeEach
    public void initTest() {
        groupe = createEntity(em);
    }

    @Test
    @Transactional
    public void createGroupe() throws Exception {
        int databaseSizeBeforeCreate = groupeRepository.findAll().size();
        // Create the Groupe
        restGroupeMockMvc.perform(post("/api/groupes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(groupe)))
            .andExpect(status().isCreated());

        // Validate the Groupe in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeCreate + 1);
        Groupe testGroupe = groupeList.get(groupeList.size() - 1);
        assertThat(testGroupe.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testGroupe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testGroupe.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
        assertThat(testGroupe.getDateCreation()).isEqualTo(DEFAULT_DATE_CREATION);
        assertThat(testGroupe.getPieceJointe()).isEqualTo(DEFAULT_PIECE_JOINTE);
        assertThat(testGroupe.getPieceJointeContentType()).isEqualTo(DEFAULT_PIECE_JOINTE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createGroupeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = groupeRepository.findAll().size();

        // Create the Groupe with an existing ID
        groupe.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGroupeMockMvc.perform(post("/api/groupes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(groupe)))
            .andExpect(status().isBadRequest());

        // Validate the Groupe in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllGroupes() throws Exception {
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);

        // Get all the groupeList
        restGroupeMockMvc.perform(get("/api/groupes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groupe.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].pieceJointeContentType").value(hasItem(DEFAULT_PIECE_JOINTE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].pieceJointe").value(hasItem(Base64Utils.encodeToString(DEFAULT_PIECE_JOINTE))));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllGroupesWithEagerRelationshipsIsEnabled() throws Exception {
        when(groupeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGroupeMockMvc.perform(get("/api/groupes?eagerload=true"))
            .andExpect(status().isOk());

        verify(groupeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllGroupesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(groupeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGroupeMockMvc.perform(get("/api/groupes?eagerload=true"))
            .andExpect(status().isOk());

        verify(groupeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getGroupe() throws Exception {
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);

        // Get the groupe
        restGroupeMockMvc.perform(get("/api/groupes/{id}", groupe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(groupe.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()))
            .andExpect(jsonPath("$.pieceJointeContentType").value(DEFAULT_PIECE_JOINTE_CONTENT_TYPE))
            .andExpect(jsonPath("$.pieceJointe").value(Base64Utils.encodeToString(DEFAULT_PIECE_JOINTE)));
    }
    @Test
    @Transactional
    public void getNonExistingGroupe() throws Exception {
        // Get the groupe
        restGroupeMockMvc.perform(get("/api/groupes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGroupe() throws Exception {
        // Initialize the database
        groupeService.save(groupe);

        int databaseSizeBeforeUpdate = groupeRepository.findAll().size();

        // Update the groupe
        Groupe updatedGroupe = groupeRepository.findById(groupe.getId()).get();
        // Disconnect from session so that the updates on updatedGroupe are not directly saved in db
        em.detach(updatedGroupe);
        updatedGroupe
            .nom(UPDATED_NOM)
            .description(UPDATED_DESCRIPTION)
            .adresse(UPDATED_ADRESSE)
            .dateCreation(UPDATED_DATE_CREATION)
            .pieceJointe(UPDATED_PIECE_JOINTE)
            .pieceJointeContentType(UPDATED_PIECE_JOINTE_CONTENT_TYPE);

        restGroupeMockMvc.perform(put("/api/groupes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedGroupe)))
            .andExpect(status().isOk());

        // Validate the Groupe in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeUpdate);
        Groupe testGroupe = groupeList.get(groupeList.size() - 1);
        assertThat(testGroupe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testGroupe.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testGroupe.getAdresse()).isEqualTo(UPDATED_ADRESSE);
        assertThat(testGroupe.getDateCreation()).isEqualTo(UPDATED_DATE_CREATION);
        assertThat(testGroupe.getPieceJointe()).isEqualTo(UPDATED_PIECE_JOINTE);
        assertThat(testGroupe.getPieceJointeContentType()).isEqualTo(UPDATED_PIECE_JOINTE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingGroupe() throws Exception {
        int databaseSizeBeforeUpdate = groupeRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroupeMockMvc.perform(put("/api/groupes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(groupe)))
            .andExpect(status().isBadRequest());

        // Validate the Groupe in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteGroupe() throws Exception {
        // Initialize the database
        groupeService.save(groupe);

        int databaseSizeBeforeDelete = groupeRepository.findAll().size();

        // Delete the groupe
        restGroupeMockMvc.perform(delete("/api/groupes/{id}", groupe.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
