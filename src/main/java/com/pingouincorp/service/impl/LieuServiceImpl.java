package com.pingouincorp.service.impl;

import com.pingouincorp.service.LieuService;
import com.pingouincorp.domain.Lieu;
import com.pingouincorp.repository.LieuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Lieu}.
 */
@Service
@Transactional
public class LieuServiceImpl implements LieuService {

    private final Logger log = LoggerFactory.getLogger(LieuServiceImpl.class);

    private final LieuRepository lieuRepository;

    public LieuServiceImpl(LieuRepository lieuRepository) {
        this.lieuRepository = lieuRepository;
    }

    @Override
    public Lieu save(Lieu lieu) {
        log.debug("Request to save Lieu : {}", lieu);
        return lieuRepository.save(lieu);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Lieu> findAll(Pageable pageable) {
        log.debug("Request to get all Lieus");
        return lieuRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Lieu> findOne(Long id) {
        log.debug("Request to get Lieu : {}", id);
        return lieuRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Lieu : {}", id);
        lieuRepository.deleteById(id);
    }
}
