package com.project.system.repositories;

import com.project.system.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT log FROM AuditLog log WHERE " +
           "LOWER(log.userName) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
           "LOWER(log.action) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
           "LOWER(log.module) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
           "LOWER(log.className) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
           "LOWER(log.description) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
           "LOWER(log.affectedId) LIKE LOWER(CONCAT('%', :filter, '%'))")
    Page<AuditLog> searchByFilterPaginated(@Param("filter") String filter, Pageable pageable);
}
