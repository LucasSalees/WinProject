package com.project.system.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.system.entity.Occupation;

@Repository
public interface OccupationRepository extends JpaRepository<Occupation, Long> {

    @Query("SELECT o FROM Occupation o " +
           "WHERE LOWER(o.occupationName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR LOWER(o.occupationCBO) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR LOWER(o.occupationType) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR CAST(o.occupationId AS string) LIKE CONCAT('%', :filter, '%')")
    List<Occupation> searchByFilter(@Param("filter") String filter);

    // NOVO: método paginado
    @Query("SELECT o FROM Occupation o " +
	       "WHERE LOWER(o.occupationName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
	       "OR LOWER(o.occupationCBO) LIKE LOWER(CONCAT('%', :filter, '%')) " +
	       "OR (o.occupationType = com.project.system.enums.input.OccupationType.FAMILY AND LOWER(:filter) LIKE '%família%') " +
	       "OR (o.occupationType = com.project.system.enums.input.OccupationType.OCCUPATION AND LOWER(:filter) LIKE '%ocupação%') " +
	       "OR (o.occupationType = com.project.system.enums.input.OccupationType.SYNONYMOUS AND LOWER(:filter) LIKE '%sinônimos%') " +
	       "OR CAST(o.occupationId AS string) LIKE CONCAT('%', :filter, '%')")
	Page<Occupation> searchByFilterPaginated(@Param("filter") String filter, Pageable pageable);

    
    Page<Occupation> findByOccupationNameContainingIgnoreCaseOrOccupationCBOContainingIgnoreCaseOrOccupationTypeContainingIgnoreCaseOrOccupationId(
            String name, String cbo, String type, Long id, Pageable pageable);
}

