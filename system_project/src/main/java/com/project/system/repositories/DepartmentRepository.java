package com.project.system.repositories;

import com.project.system.entity.Department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT d FROM Department d " +
           "WHERE LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR LOWER(d.departmentTel) LIKE CONCAT(CONCAT('%', :filter, '%')) " +
           "OR LOWER(d.departmentEmail) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR LOWER(d.departmentManager) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR CAST(d.departmentId AS string) LIKE CONCAT('%', :filter, '%')")
    List<Department> searchByFilter(@Param("filter") String filter);
    
    @Query("SELECT d FROM Department d " +
            "WHERE LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(d.departmentTel) LIKE CONCAT(CONCAT('%', :filter, '%')) " +
            "OR LOWER(d.departmentEmail) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(d.departmentManager) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR CAST(d.departmentId AS string) LIKE CONCAT('%', :filter, '%')")
    Page<Department> searchByFilterPaginated(@Param("filter") String filter, Pageable pageable);
}

