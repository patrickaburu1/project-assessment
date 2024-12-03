package com.kcb.projectassessment.services.impl;

import com.kcb.projectassessment.dtos.request.TaskRequest;
import com.kcb.projectassessment.dtos.response.TaskResponse;
import com.kcb.projectassessment.entities.Project;
import com.kcb.projectassessment.entities.Task;
import com.kcb.projectassessment.enums.TaskStatus;
import com.kcb.projectassessment.execption.RecordAlreadyExistsException;
import com.kcb.projectassessment.execption.RecordNotFoundException;
import com.kcb.projectassessment.execption.UnProcessingRequestException;
import com.kcb.projectassessment.repositories.ProjectRepository;
import com.kcb.projectassessment.repositories.TaskRepository;
import com.kcb.projectassessment.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Override
    public TaskResponse addTaskToProject(UUID projectId, TaskRequest taskRequest) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RecordNotFoundException("Project not found"));

        taskRepository.findByProjectIdAndTitle(projectId,taskRequest.getTitle()).ifPresent(existingProject -> {
            throw new RecordAlreadyExistsException("Task with the title '" + taskRequest.getTitle() + "' already exists.");
        });

        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setDueDate(taskRequest.getDueDate());
        task.setProject(project);
        task.setCreateAt(new Date());
        task.setUpdatedAt(new Date());
        Task savedTask = taskRepository.save(task);

        return TaskResponse.builder()
                .id(savedTask.getId())
                .title(savedTask.getTitle())
                .description(savedTask.getDescription())
                .status(savedTask.getStatus())
                .dueDate(savedTask.getDueDate())
                .projectId(project.getId())
                .build();
    }

    @Override
    public List<TaskResponse> getTasksForProject(UUID projectId, TaskStatus status, LocalDate dueDate) {
        return taskRepository.findByProjectIdAndStatusAndDueDate(projectId, status, dueDate)
                .stream()
                .map(task -> TaskResponse.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .dueDate(task.getDueDate())
                        .projectId(task.getProject().getId())
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    public TaskResponse updateTask(UUID projectId,UUID taskId, TaskRequest taskRequest) {
        Task task = taskRepository.findByIdAndProjectId(taskId,projectId).orElseThrow(() -> new RecordNotFoundException("Task not found"));
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setDueDate(taskRequest.getDueDate());
        task.setUpdatedAt(new Date());
        taskRepository.save(task);

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .projectId(task.getProject().getId())
                .build();
    }

    public void deleteTask(UUID projectId, UUID taskId) {
        Task task = taskRepository.findByIdAndProjectId(taskId,projectId).orElseThrow(() -> new RecordNotFoundException("Task not found"));
        taskRepository.delete(task);
    }
}
