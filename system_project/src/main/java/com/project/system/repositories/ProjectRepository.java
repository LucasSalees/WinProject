package com.project.system.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.system.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
            SELECT p FROM Project p
            LEFT JOIN p.projectContractualAcronym a
            WHERE CAST(p.projectId AS string) LIKE %:filter%
               OR LOWER(p.projectName) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(a.acronym) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(p.projectBusinessVertical) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(p.projectPriority) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(p.projectStatus) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR CAST(p.projectExecutionPercentage AS string) LIKE %:filter%
               OR CAST(p.projectPlanningStartDate AS string) LIKE %:filter%
               OR CAST(p.projectPlanningEndDate AS string) LIKE %:filter% """)
    List<Project> searchByFilter(@Param("filter") String filter);
    
    @Query("""
            SELECT p FROM Project p
            LEFT JOIN p.projectContractualAcronym a
            WHERE CAST(p.projectId AS string) LIKE %:filter%
               OR LOWER(p.projectName) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(a.acronym) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(p.projectBusinessVertical) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(p.projectPriority) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(p.projectStatus) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR CAST(p.projectExecutionPercentage AS string) LIKE %:filter%
               OR CAST(p.projectPlanningStartDate AS string) LIKE %:filter%
               OR CAST(p.projectPlanningEndDate AS string) LIKE %:filter% """)
    Page<Project> searchByFilterPaginated(@Param("filter") String filter, Pageable pageable);

}