package com.kcb.projectassessment;

import com.kcb.projectassessment.dtos.request.ProjectRequest;
import com.kcb.projectassessment.dtos.response.ProjectResponse;
import com.kcb.projectassessment.dtos.response.ProjectSummaryResponse;
import com.kcb.projectassessment.entities.Project;
import com.kcb.projectassessment.execption.RecordAlreadyExistsException;
import com.kcb.projectassessment.execption.RecordNotFoundException;
import com.kcb.projectassessment.repositories.ProjectRepository;
import com.kcb.projectassessment.repositories.TaskRepository;
import com.kcb.projectassessment.services.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private UUID projectId;
    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectId = UUID.randomUUID();
        project = new Project();
        project.setId(projectId);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setCreateAt(new Date());
        project.setUpdatedAt(new Date());
        project.setTasks(new ArrayList<>());
    }

    @Test
    void testCreateProject_Success() {

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Test Project");
        projectRequest.setDescription("Project Description");

        when(projectRepository.findByName(projectRequest.getName())).thenReturn(Optional.empty());
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.createProject(projectRequest);

        assertNotNull(response);
        assertEquals("Test Project", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(0, response.getTaskCount());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testCreateProject_ProjectAlreadyExists() {
        // Arrange
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Existing Project");
        projectRequest.setDescription("Project Description");

        when(projectRepository.findByName(projectRequest.getName())).thenReturn(Optional.of(project));

        // Act & Assert
        RecordAlreadyExistsException exception = assertThrows(RecordAlreadyExistsException.class, () -> {
            projectService.createProject(projectRequest);
        });
        assertEquals("Project with the name 'Existing Project' already exists.", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void testGetProjectById_Success() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act
        ProjectResponse response = projectService.getProjectById(projectId);

        // Assert
        assertNotNull(response);
        assertEquals(projectId, response.getId());
        assertEquals("Test Project", response.getName());
        assertEquals("Test Description", response.getDescription());
    }

    @Test
    void testGetProjectById_NotFound() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            projectService.getProjectById(projectId);
        });
        assertEquals("Project not found", exception.getMessage());
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testGetAllProjects_Success() {

        Project project1 = new Project();
        project1.setId(UUID.randomUUID());
        project1.setName("Project 1");
        project1.setDescription("Description 1");
        project1.setTasks(new ArrayList<>());


        Project project2 = new Project();
        project2.setId(UUID.randomUUID());
        project2.setName("Project 2");
        project2.setDescription("Description 2");
        project2.setTasks(new ArrayList<>());

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        List<ProjectResponse> responses = projectService.getAllProjects();

        assertEquals("Project 1", responses.get(0).getName());
        assertEquals("Project 2", responses.get(1).getName());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testGetProjectSummary_Success() {

        Project project1 = new Project();
        project1.setId(UUID.randomUUID());
        project1.setName("Project 1");
        project1.setDescription("Description 1");

        Map<String, Long> taskCountByStatus = new HashMap<>();
        taskCountByStatus.put("Pending", 5L);
        taskCountByStatus.put("Completed", 3L);

        when(projectRepository.findAll()).thenReturn(List.of(project1));
        when(taskRepository.countTasksByProjectIdGroupedByStatus(project1.getId())).thenReturn(taskCountByStatus);


        List<ProjectSummaryResponse> summary = projectService.getProjectSummary();

        assertEquals(1, summary.size());
        assertEquals("Project 1", summary.get(0).getProjectName());
        assertEquals(2, summary.get(0).getTaskCountByStatus().size());
        verify(projectRepository, times(1)).findAll();
        verify(taskRepository, times(1)).countTasksByProjectIdGroupedByStatus(project1.getId());
    }
}

