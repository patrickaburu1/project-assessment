package com.kcb.projectassessment;
import com.kcb.projectassessment.dtos.request.TaskRequest;
import com.kcb.projectassessment.dtos.response.TaskResponse;
import com.kcb.projectassessment.entities.Project;
import com.kcb.projectassessment.entities.Task;
import com.kcb.projectassessment.enums.TaskStatus;
import com.kcb.projectassessment.execption.RecordAlreadyExistsException;
import com.kcb.projectassessment.execption.RecordNotFoundException;
import com.kcb.projectassessment.repositories.ProjectRepository;
import com.kcb.projectassessment.repositories.TaskRepository;
import com.kcb.projectassessment.services.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private UUID projectId;
    private UUID taskId;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        taskRequest = new  TaskRequest();
        taskRequest .setTitle("New Task");
        taskRequest .setDescription("Task Description");
        taskRequest .setStatus(TaskStatus.TO_DO);
        taskRequest    .setDueDate(LocalDate.now().plusDays(1));

    }

    @Test
    void addTaskToProject_ShouldReturnTaskResponse_WhenTaskIsAdded() {
        // Given
        Project project = new Project();
        project.setId(projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectIdAndTitle(projectId, taskRequest.getTitle())).thenReturn(Optional.empty());

        Task savedTask = new Task();
        savedTask.setId(taskId);
        savedTask.setTitle(taskRequest.getTitle());
        savedTask.setDescription(taskRequest.getDescription());
        savedTask.setStatus(taskRequest.getStatus());
        savedTask.setDueDate(taskRequest.getDueDate());
        savedTask.setProject(project);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        TaskResponse response = taskService.addTaskToProject(projectId, taskRequest);

        // Then
        assertNotNull(response);
        assertEquals(taskId, response.getId());
        assertEquals(taskRequest.getTitle(), response.getTitle());
        assertEquals(taskRequest.getDescription(), response.getDescription());
        assertEquals(taskRequest.getStatus(), response.getStatus());
    }

    @Test
    void addTaskToProject_ShouldThrowRecordAlreadyExistsException_WhenTaskTitleAlreadyExists() {
        // Given
        Project project = new Project();
        project.setId(projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        Task existingTask = new Task();
        existingTask.setTitle(taskRequest.getTitle());
        when(taskRepository.findByProjectIdAndTitle(projectId, taskRequest.getTitle())).thenReturn(Optional.of(existingTask));

        // When / Then
        RecordAlreadyExistsException exception = assertThrows(RecordAlreadyExistsException.class, () ->
                taskService.addTaskToProject(projectId, taskRequest));
        assertEquals("Task with the title '" + taskRequest.getTitle() + "' already exists.", exception.getMessage());
    }

    @Test
    void updateTask_ShouldReturnUpdatedTaskResponse_WhenTaskIsUpdated() {

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(TaskStatus.TO_DO);
        existingTask.setDueDate(LocalDate.now());
        existingTask.setProject(new Project());

        when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(existingTask));

        taskRequest.setTitle("Updated Title");
        taskRequest.setDescription("Updated Description");

        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);


        TaskResponse response = taskService.updateTask(projectId, taskId, taskRequest);


        assertNotNull(response);
        assertEquals(taskId, response.getId());
        assertEquals("Updated Title", response.getTitle());
        assertEquals("Updated Description", response.getDescription());
    }

    @Test
    void updateTask_ShouldThrowRecordNotFoundException_WhenTaskNotFound() {

        when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () ->
                taskService.updateTask(projectId, taskId, taskRequest));
        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    void deleteTask_ShouldDeleteTask_WhenTaskIsDeleted() {

        Task task = new Task();
        task.setId(taskId);
        task.setProject(new Project());

        when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(task));

        taskService.deleteTask(projectId, taskId);

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void deleteTask_ShouldThrowRecordNotFoundException_WhenTaskNotFound() {

        when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () ->
                taskService.deleteTask(projectId, taskId));
        assertEquals("Task not found", exception.getMessage());
    }
}
