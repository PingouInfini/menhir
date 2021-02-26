package com.capgemini.service.impl;

import com.capgemini.service.GroupeService;
import com.capgemini.domain.Groupe;
import com.capgemini.repository.GroupeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Groupe}.
 */
@Service
@Transactional
public class GroupeServiceImpl implements GroupeService {

    private final Logger log = LoggerFactory.getLogger(GroupeServiceImpl.class);

    private final GroupeRepository groupeRepository;

    public GroupeServiceImpl(GroupeRepository groupeRepository) {
        this.groupeRepository = groupeRepository;
    }

    @Override
    public Groupe save(Groupe groupe) {
        log.debug("Request to save Groupe : {}", groupe);
        return groupeRepository.save(groupe);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Groupe> findAll(Pageable pageable) {
        log.debug("Request to get all Groupes");
        return groupeRepository.findAll(pageable);
    }


    public Page<Groupe> findAllWithEagerRelationships(Pageable pageable) {
        return groupeRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Groupe> findOne(Long id) {
        log.debug("Request to get Groupe : {}", id);
        return groupeRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Groupe : {}", id);
        groupeRepository.deleteById(id);
    }
}
