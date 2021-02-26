package com.capgemini.service;

import com.capgemini.domain.Lieu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Lieu}.
 */
public interface LieuService {

    /**
     * Save a lieu.
     *
     * @param lieu the entity to save.
     * @return the persisted entity.
     */
    Lieu save(Lieu lieu);

    /**
     * Get all the lieus.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Lieu> findAll(Pageable pageable);


    /**
     * Get the "id" lieu.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Lieu> findOne(Long id);

    /**
     * Delete the "id" lieu.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
