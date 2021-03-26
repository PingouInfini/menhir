package com.pingouincorp.repository;

import com.pingouincorp.domain.Lieu;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Lieu entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LieuRepository extends JpaRepository<Lieu, Long> {
}
