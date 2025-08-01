package com.project.system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.system.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

}