package com.project.system.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.system.entity.ContractualAcronym;

@Repository
public interface ContractualAcronymRepository  extends JpaRepository<ContractualAcronym, Long> {

	@Query(value = "SELECT c FROM ContractualAcronym c " +
            "WHERE LOWER(c.contractualAcronymName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(c.acronym) LIKE LOWER(CONCAT('%', :filter, '%')) " +
			"OR CAST(c.acronymId AS string) LIKE CONCAT('%', :filter, '%')")
	List<ContractualAcronym> searchByFilter(@Param("filter") String filter);
	
	@Query(value = "SELECT c FROM ContractualAcronym c " +
            "WHERE LOWER(c.contractualAcronymName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(c.acronym) LIKE LOWER(CONCAT('%', :filter, '%')) " +
			"OR CAST(c.acronymId AS string) LIKE CONCAT('%', :filter, '%')")
	Page<ContractualAcronym> searchByFilterPaginated(@Param("filter") String filter, Pageable pageable);
	
}
