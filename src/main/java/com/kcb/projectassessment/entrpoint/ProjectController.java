package com.kcb.projectassessment.entrpoint;

import com.kcb.projectassessment.dtos.request.ProjectRequest;
import com.kcb.projectassessment.dtos.response.ProjectResponse;
import com.kcb.projectassessment.dtos.response.ProjectSummaryResponse;
import com.kcb.projectassessment.execption.RecordAlreadyExistsException;
import com.kcb.projectassessment.execption.RecordNotFoundException;
import com.kcb.projectassessment.services.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@AllArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;


    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectRequest projectRequest) {
        try {
            ProjectResponse projectResponse = projectService.createProject(projectRequest);
            return new ResponseEntity<>(projectResponse, HttpStatus.CREATED); // 201 Created

        } catch (RecordAlreadyExistsException ex) {
            return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.CONFLICT);
        }


    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projectResponses = projectService.getAllProjects();
        return new ResponseEntity<>(projectResponses, HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable UUID projectId) {
        try {
            ProjectResponse projectResponse = projectService.getProjectById(projectId);
            return ResponseEntity.ok(projectResponse);
        } catch (RecordNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<List<ProjectSummaryResponse>> getProjectSummary() {
        List<ProjectSummaryResponse> projectSummaryResponses = projectService.getProjectSummary();
        return new ResponseEntity<>(projectSummaryResponses, HttpStatus.OK);
    }
}
