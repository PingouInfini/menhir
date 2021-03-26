package com.pingouincorp.repository;

import com.pingouincorp.domain.Individu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Individu entity.
 */
@Repository
public interface IndividuRepository extends JpaRepository<Individu, Long> {

    @Query(value = "select distinct individu from Individu individu left join fetch individu.appartientAS",
        countQuery = "select count(distinct individu) from Individu individu")
    Page<Individu> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct individu from Individu individu left join fetch individu.appartientAS")
    List<Individu> findAllWithEagerRelationships();

    @Query("select individu from Individu individu left join fetch individu.appartientAS where individu.id =:id")
    Optional<Individu> findOneWithEagerRelationships(@Param("id") Long id);
}
