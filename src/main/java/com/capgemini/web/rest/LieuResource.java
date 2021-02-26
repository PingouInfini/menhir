package com.capgemini.web.rest;

import com.capgemini.domain.Lieu;
import com.capgemini.service.LieuService;
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
 * REST controller for managing {@link com.capgemini.domain.Lieu}.
 */
@RestController
@RequestMapping("/api")
public class LieuResource {

    private final Logger log = LoggerFactory.getLogger(LieuResource.class);

    private static final String ENTITY_NAME = "lieu";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LieuService lieuService;

    public LieuResource(LieuService lieuService) {
        this.lieuService = lieuService;
    }

    /**
     * {@code POST  /lieus} : Create a new lieu.
     *
     * @param lieu the lieu to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lieu, or with status {@code 400 (Bad Request)} if the lieu has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/lieus")
    public ResponseEntity<Lieu> createLieu(@RequestBody Lieu lieu) throws URISyntaxException {
        log.debug("REST request to save Lieu : {}", lieu);
        if (lieu.getId() != null) {
            throw new BadRequestAlertException("A new lieu cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Lieu result = lieuService.save(lieu);
        return ResponseEntity.created(new URI("/api/lieus/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /lieus} : Updates an existing lieu.
     *
     * @param lieu the lieu to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lieu,
     * or with status {@code 400 (Bad Request)} if the lieu is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lieu couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/lieus")
    public ResponseEntity<Lieu> updateLieu(@RequestBody Lieu lieu) throws URISyntaxException {
        log.debug("REST request to update Lieu : {}", lieu);
        if (lieu.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Lieu result = lieuService.save(lieu);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lieu.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /lieus} : get all the lieus.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lieus in body.
     */
    @GetMapping("/lieus")
    public ResponseEntity<List<Lieu>> getAllLieus(Pageable pageable) {
        log.debug("REST request to get a page of Lieus");
        Page<Lieu> page = lieuService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /lieus/:id} : get the "id" lieu.
     *
     * @param id the id of the lieu to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lieu, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/lieus/{id}")
    public ResponseEntity<Lieu> getLieu(@PathVariable Long id) {
        log.debug("REST request to get Lieu : {}", id);
        Optional<Lieu> lieu = lieuService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lieu);
    }

    /**
     * {@code DELETE  /lieus/:id} : delete the "id" lieu.
     *
     * @param id the id of the lieu to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/lieus/{id}")
    public ResponseEntity<Void> deleteLieu(@PathVariable Long id) {
        log.debug("REST request to delete Lieu : {}", id);
        lieuService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
