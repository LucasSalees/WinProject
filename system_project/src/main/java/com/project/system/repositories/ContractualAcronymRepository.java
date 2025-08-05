package com.project.system.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.system.entity.ContractualAcronym;

@Repository
public interface ContractualAcronymRepository  extends JpaRepository<ContractualAcronym, Long> {

	@Query(value = "SELECT * FROM contractual_acronym c " +
            "WHERE LOWER(c.contractual_acronym_name) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(c.acronym) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR CAST(c.contractual_acronym_id AS TEXT) LIKE CONCAT('%', :filter, '%')",
    nativeQuery = true)
List<ContractualAcronym> searchByFilter(@Param("filter") String filter);
	
}
