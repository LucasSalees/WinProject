package com.project.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.system.entity.Department;
import com.project.system.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.userEmail = :userEmail")
    Optional<User> findByEmail(@Param("userEmail") String userEmail);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.permissions WHERE u.userEmail = :userEmail")
    Optional<User> findByEmailWithPermissions(@Param("userEmail") String userEmail);

    @Query(value = "SELECT senha FROM users WHERE userEmail = ?1", nativeQuery = true)
    String findPasswordByEmail(String userEmail);

    boolean existsByUserEmail(String userEmail);
    
    /**
     * @author edsons
     * @param filter
     * @return Tabela filtrada
     * 
     * Esta query não esta completa, so copiei e colei, 
     * ainda não deu tempo de ajustar para a tabela de usuarios
     */
    @Query("SELECT d FROM Department d " +
            "WHERE LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(d.departmentTel) LIKE CONCAT(CONCAT('%', :filter, '%')) " +
            "OR LOWER(d.departmentEmail) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(d.departmentManager) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR CAST(d.departmentId AS string) LIKE CONCAT('%', :filter, '%')")
     List<User> searchByFilter(@Param("filter") String filter);
}
