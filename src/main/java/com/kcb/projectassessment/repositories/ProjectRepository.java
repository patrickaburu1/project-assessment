package com.kcb.projectassessment.repositories;


import com.kcb.projectassessment.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findByName(String projectName);
}