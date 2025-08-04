package com.project.system.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.system.entity.ContractualAcronym;
import com.project.system.entity.Department;

@Repository
public interface ContractualAcronymRepository  extends JpaRepository<ContractualAcronym, Long> {

	@Query("SELECT c FROM ContractualAcronym c " +
	           "WHERE LOWER(c.contractualAcronymName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
	           "OR LOWER(c.acronym) LIKE CONCAT(CONCAT('%', :filter, '%')) " +
	           "OR CAST(c.contractualAcronymId AS string) LIKE CONCAT('%', :filter, '%')")
	 List<ContractualAcronym> searchByFilter(@Param("filter") String filter);
	
}
