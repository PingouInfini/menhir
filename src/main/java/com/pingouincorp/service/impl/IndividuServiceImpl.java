package com.pingouincorp.service.impl;

import com.pingouincorp.domain.Groupe;
import com.pingouincorp.domain.Individu;
import com.pingouincorp.repository.IndividuRepository;
import com.pingouincorp.service.IndividuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Individu}.
 */
@Service
@Transactional
public class IndividuServiceImpl implements IndividuService {

    private final Logger log = LoggerFactory.getLogger(IndividuServiceImpl.class);

    private final IndividuRepository individuRepository;

    public IndividuServiceImpl(IndividuRepository individuRepository) {
        this.individuRepository = individuRepository;
    }

    @Override
    public Individu save(Individu individu) {
        log.debug("Request to save Individu : {}", individu);
        return individuRepository.save(individu);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Individu> findAll(Pageable pageable) {
        log.debug("Request to get all Individus");
        return individuRepository.findAll(pageable);
    }


    public Page<Individu> findAllWithEagerRelationships(Pageable pageable) {
        return individuRepository.findAllWithEagerRelationships(pageable);
    }

    public Page<Individu> findAllWithEagerRelationshipsWithoutAttachment(Pageable pageable) {
        Page<Individu> result = individuRepository.findAllWithEagerRelationships(pageable);
        for (Individu individu : result.getContent())
            for (Groupe grp : individu.getAppartientAS())
                grp.setPieceJointe(null);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Individu> findOne(Long id) {
        log.debug("Request to get Individu : {}", id);
        return individuRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Individu : {}", id);
        individuRepository.deleteById(id);
    }
}
