package com.capgemini.repository;

import com.capgemini.domain.Groupe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Groupe entity.
 */
@Repository
public interface GroupeRepository extends JpaRepository<Groupe, Long> {

    @Query(value = "select distinct groupe from Groupe groupe left join fetch groupe.estSitues",
        countQuery = "select count(distinct groupe) from Groupe groupe")
    Page<Groupe> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct groupe from Groupe groupe left join fetch groupe.estSitues")
    List<Groupe> findAllWithEagerRelationships();

    @Query("select groupe from Groupe groupe left join fetch groupe.estSitues where groupe.id =:id")
    Optional<Groupe> findOneWithEagerRelationships(@Param("id") Long id);
}
