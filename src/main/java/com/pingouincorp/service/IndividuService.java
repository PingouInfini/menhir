package com.pingouincorp.service;

import com.pingouincorp.domain.Individu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Individu}.
 */
public interface IndividuService {

    /**
     * Save a individu.
     *
     * @param individu the entity to save.
     * @return the persisted entity.
     */
    Individu save(Individu individu);

    /**
     * Get all the individus.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Individu> findAll(Pageable pageable);

    /**
     * Get all the individus with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Page<Individu> findAllWithEagerRelationships(Pageable pageable);

    Page<Individu> findAllWithEagerRelationshipsWithoutAttachment(Pageable pageable);


    /**
     * Get the "id" individu.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Individu> findOne(Long id);

    /**
     * Delete the "id" individu.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
