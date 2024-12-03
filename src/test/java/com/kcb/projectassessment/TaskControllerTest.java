package com.kcb.projectassessment;

import com.kcb.projectassessment.dtos.request.TaskRequest;
import com.kcb.projectassessment.dtos.response.TaskResponse;
import com.kcb.projectassessment.entrpoint.TaskController;
import com.kcb.projectassessment.enums.TaskStatus;
import com.kcb.projectassessment.services.TaskService;
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

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private UUID projectId;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();
    }

    @Test
    void testAddTask_Success() throws Exception {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Task Description");
        taskRequest.setStatus(TaskStatus.TO_DO);
        taskRequest.setDueDate(LocalDate.now());

        TaskResponse taskResponse = TaskResponse.builder()
                .id(UUID.randomUUID())
                .title("Test Task")
                .description("Task Description")
                .status(TaskStatus.TO_DO)
                .dueDate(LocalDate.now())
                .projectId(projectId)
                .build();

        when(taskService.addTaskToProject(eq(projectId), any(TaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(post("/api/v1/projects/{projectId}/tasks", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Task\",\"description\":\"Task Description\",\"status\":\"TO_DO\",\"dueDate\":\"2024-12-03\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("TO_DO"));
    }

    @Test
    void testAddTask_TaskAlreadyExists() throws Exception {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Task Description");
        taskRequest.setStatus(TaskStatus.TO_DO);
        taskRequest.setDueDate(LocalDate.now());

        when(taskService.addTaskToProject(eq(projectId), any(TaskRequest.class)))
                .thenThrow(new RecordAlreadyExistsException("Task with the title 'Test Task' already exists."));

        mockMvc.perform(post("/api/v1/projects/{projectId}/tasks", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Task\",\"description\":\"Task Description\",\"status\":\"TO_DO\",\"dueDate\":\"2024-12-03\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetTasks_Success() throws Exception {
        mockMvc.perform(get("/api/v1/projects/{projectId}/tasks", projectId)
                        .param("status", TaskStatus.TO_DO.toString())
                        .param("dueDate", "2024-12-03"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteTask_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/projects/{projectId}/tasks/{taskId}", projectId, taskId))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(projectId, taskId);
    }

    @Test
    void testDeleteTask_TaskNotFound() throws Exception {
        doThrow(new RecordNotFoundException("Task not found"))
                .when(taskService).deleteTask(eq(projectId), eq(taskId));

        mockMvc.perform(delete("/api/v1/projects/{projectId}/tasks/{taskId}", projectId, taskId))
                .andExpect(status().isNotFound());
    }
}
