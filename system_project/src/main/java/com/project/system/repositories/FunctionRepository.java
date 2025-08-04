package com.project.system.repositories;

import com.project.system.entity.Function;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Long> {

    @Query("SELECT f FROM Function f " +
           "WHERE LOWER(f.functionName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR LOWER(f.functionTel) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR LOWER(f.functionEmail) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR CAST(f.functionId AS string) LIKE CONCAT('%', :filter, '%')")
    List<Function> searchByFilter(@Param("filter") String filter);
}
