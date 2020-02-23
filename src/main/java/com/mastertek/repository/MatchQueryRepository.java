package com.mastertek.repository;

import com.mastertek.domain.MatchQuery;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the MatchQuery entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MatchQueryRepository extends JpaRepository<MatchQuery, Long> {

}
