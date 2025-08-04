package com.project.system.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.system.entity.Occupation;

@Repository
public interface OccupationRepository extends JpaRepository<Occupation, Long> {

    @Query("SELECT o FROM Occupation o " +
           "WHERE LOWER(o.occupationName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR LOWER(o.occupationTel) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR LOWER(o.occupationEmail) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR CAST(o.occupationId AS string) LIKE CONCAT('%', :filter, '%')")
    List<Occupation> searchByFilter(@Param("filter") String filter);

}
