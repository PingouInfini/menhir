package com.capgemini.web.rest;

import com.capgemini.domain.Groupe;
import com.capgemini.service.GroupeService;
import com.capgemini.web.rest.errors.BadRequestAlertException;

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

/**
 * REST controller for managing {@link com.capgemini.domain.Groupe}.
 */
@RestController
@RequestMapping("/api")
public class GroupeResource {

    private final Logger log = LoggerFactory.getLogger(GroupeResource.class);

    private static final String ENTITY_NAME = "groupe";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GroupeService groupeService;

    public GroupeResource(GroupeService groupeService) {
        this.groupeService = groupeService;
    }

    /**
     * {@code POST  /groupes} : Create a new groupe.
     *
     * @param groupe the groupe to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new groupe, or with status {@code 400 (Bad Request)} if the groupe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/groupes")
    public ResponseEntity<Groupe> createGroupe(@RequestBody Groupe groupe) throws URISyntaxException {
        log.debug("REST request to save Groupe : {}", groupe);
        if (groupe.getId() != null) {
            throw new BadRequestAlertException("A new groupe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Groupe result = groupeService.save(groupe);
        return ResponseEntity.created(new URI("/api/groupes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /groupes} : Updates an existing groupe.
     *
     * @param groupe the groupe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groupe,
     * or with status {@code 400 (Bad Request)} if the groupe is not valid,
     * or with status {@code 500 (Internal Server Error)} if the groupe couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/groupes")
    public ResponseEntity<Groupe> updateGroupe(@RequestBody Groupe groupe) throws URISyntaxException {
        log.debug("REST request to update Groupe : {}", groupe);
        if (groupe.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Groupe result = groupeService.save(groupe);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, groupe.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /groupes} : get all the groupes.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of groupes in body.
     */
    @GetMapping("/groupes")
    public ResponseEntity<List<Groupe>> getAllGroupes(Pageable pageable, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get a page of Groupes");
        Page<Groupe> page;
        if (eagerload) {
            page = groupeService.findAllWithEagerRelationships(pageable);
        } else {
            page = groupeService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /groupes/:id} : get the "id" groupe.
     *
     * @param id the id of the groupe to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the groupe, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/groupes/{id}")
    public ResponseEntity<Groupe> getGroupe(@PathVariable Long id) {
        log.debug("REST request to get Groupe : {}", id);
        Optional<Groupe> groupe = groupeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(groupe);
    }

    /**
     * {@code DELETE  /groupes/:id} : delete the "id" groupe.
     *
     * @param id the id of the groupe to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/groupes/{id}")
    public ResponseEntity<Void> deleteGroupe(@PathVariable Long id) {
        log.debug("REST request to delete Groupe : {}", id);
        groupeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
