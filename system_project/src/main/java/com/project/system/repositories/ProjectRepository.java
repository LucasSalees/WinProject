package com.project.system.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.system.entity.Occupation;
import com.project.system.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p " +
            "WHERE LOWER(p.projectName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(p.projectContractualAcronym) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(p.projectBusinessVertical) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(p.projectPriority) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(p.projectStatus) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR function('str', p.projectExecutionPercentage) LIKE CONCAT('%', :filter, '%') " +
            "OR function('FORMAT', p.projectPlanningStartDate, 'yyyy-MM-dd') LIKE CONCAT('%', :filter, '%') " +
            "OR function('FORMAT', p.projectPlanningEndDate, 'yyyy-MM-dd') LIKE CONCAT('%', :filter, '%') " +
            "OR CAST(p.projectId AS string) LIKE CONCAT('%', :filter, '%')")
     List<Project> searchByFilter(@Param("filter") String filter);
}