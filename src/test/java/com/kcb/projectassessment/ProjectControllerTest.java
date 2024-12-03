package com.kcb.projectassessment;

import com.kcb.projectassessment.dtos.request.ProjectRequest;
import com.kcb.projectassessment.dtos.response.ProjectResponse;
import com.kcb.projectassessment.entrpoint.ProjectController;
import com.kcb.projectassessment.services.ProjectService;
import com.kcb.projectassessment.execption.RecordNotFoundException;
import com.kcb.projectassessment.execption.RecordAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private UUID projectId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
        projectId = UUID.randomUUID();
    }

    @Test
    void testCreateProject_Success() throws Exception {

        ProjectResponse projectResponse = ProjectResponse.builder()
                .id(projectId)
                .name("New Project")
                .description("Project Description")
                .taskCount(0)
                .build();

        when(projectService.createProject(any(ProjectRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Project\",\"description\":\"Project Description\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Project"));
    }

    @Test
    void testCreateProject_ProjectAlreadyExists() throws Exception {


        when(projectService.createProject(any(ProjectRequest.class)))
                .thenThrow(new RecordAlreadyExistsException("Project with the name 'New Project' already exists."));

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Project\",\"description\":\"Project Description\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetProjectById_Success() throws Exception {
        ProjectResponse projectResponse = ProjectResponse.builder()
                .id(projectId)
                .name("Existing Project")
                .description("Project Description")
                .taskCount(5)
                .build();

        when(projectService.getProjectById(eq(projectId))).thenReturn(projectResponse);

        mockMvc.perform(get("/api/v1/projects/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.name").value("Existing Project"));
    }

    @Test
    void testGetProjectById_NotFound() throws Exception {
        when(projectService.getProjectById(eq(projectId)))
                .thenThrow(new RecordNotFoundException("Project not found"));

        mockMvc.perform(get("/api/v1/projects/{projectId}", projectId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllProjects_Success() throws Exception {
        ProjectResponse projectResponse1 = ProjectResponse.builder()
                .id(UUID.randomUUID())
                .name("Project 1")
                .build();

        ProjectResponse projectResponse2 = ProjectResponse.builder()
                .id(UUID.randomUUID())
                .name("Project 2")
                .build();

        when(projectService.getAllProjects()).thenReturn(List.of(projectResponse1, projectResponse2));

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Project 1"))
                .andExpect(jsonPath("$[1].name").value("Project 2"));
    }


}
