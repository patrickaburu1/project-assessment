package com.kcb.projectassessment.repositories;

import com.kcb.projectassessment.entities.Task;
import com.kcb.projectassessment.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByProjectIdAndStatusAndDueDate(UUID projectId, TaskStatus status, LocalDate dueDate);

    Optional<Task> findByProjectIdAndTitle(UUID projectId, String title);

    Optional<Task> findByIdAndProjectId(UUID id, UUID projectId);

    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.project.id = :projectId GROUP BY t.status")
    Map<String, Long> countTasksByProjectIdGroupedByStatus(@Param("projectId") UUID projectId);

}
