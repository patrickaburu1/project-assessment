package com.kcb.projectassessment.services.impl;

import com.kcb.projectassessment.dtos.request.ProjectRequest;
import com.kcb.projectassessment.dtos.response.ProjectResponse;
import com.kcb.projectassessment.dtos.response.ProjectSummaryResponse;
import com.kcb.projectassessment.entities.Project;
import com.kcb.projectassessment.execption.RecordAlreadyExistsException;
import com.kcb.projectassessment.execption.RecordNotFoundException;
import com.kcb.projectassessment.repositories.ProjectRepository;
import com.kcb.projectassessment.repositories.TaskRepository;
import com.kcb.projectassessment.services.ProjectService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest) {

        projectRepository.findByName(projectRequest.getName()).ifPresent(existingProject -> {
            throw new RecordAlreadyExistsException("Project with the name '" + projectRequest.getName() + "' already exists.");
        });

        Project project = new Project();
        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setCreateAt(new Date());
        project.setUpdatedAt(new Date());
        Project savedProject = projectRepository.save(project);

        return ProjectResponse.builder()
                .id(savedProject.getId())
                .name(savedProject.getName())
                .description(savedProject.getDescription())
                .taskCount(0)
                .build();
    }

    @Override
    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findAll().stream()
                .map(project -> ProjectResponse.builder()
                        .id(project.getId())
                        .name(project.getName())
                        .description(project.getDescription())
                        .taskCount(project.getTasks().size())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponse getProjectById(UUID projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RecordNotFoundException("Project not found"));

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .taskCount(project.getTasks().size())
                .build();
    }


    @Override
    public List<ProjectSummaryResponse> getProjectSummary() {

        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(project -> {
                    // Count tasks grouped by their status for the current project
                    Map<String, Long> taskCountByStatus = taskRepository.countTasksByProjectIdGroupedByStatus(project.getId());

                    // Map project data to ProjectSummaryResponse
                    return new ProjectSummaryResponse(
                            project.getId(),
                            project.getName(),
                            project.getDescription(),
                            taskCountByStatus
                    );
                })
                .collect(Collectors.toList());
    }

}
