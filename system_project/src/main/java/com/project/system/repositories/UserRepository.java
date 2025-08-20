package com.project.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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

    @Query("SELECT u FROM User u " +
           "WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR LOWER(u.userEmail) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR LOWER(u.userRole) IN :roleNames " + 
           "OR LOWER(u.userFunction.functionName) LIKE LOWER(CONCAT('%', :filter, '%')) " +
           "OR STR(u.userId) LIKE CONCAT('%', :filter, '%')")
    List<User> searchByFilterAndRole(@Param("filter") String filter, @Param("roleNames") List<String> roleNames);

}
