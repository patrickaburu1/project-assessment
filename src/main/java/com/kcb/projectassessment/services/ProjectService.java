package com.kcb.projectassessment.services;

import com.kcb.projectassessment.dtos.request.ProjectRequest;
import com.kcb.projectassessment.dtos.response.ProjectResponse;
import com.kcb.projectassessment.dtos.response.ProjectSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectResponse createProject(ProjectRequest projectRequest);

    ProjectResponse getProjectById(UUID id);

    List<ProjectResponse> getAllProjects();

    List<ProjectSummaryResponse> getProjectSummary();
}
