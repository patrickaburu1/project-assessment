package com.kcb.projectassessment.services;

import com.kcb.projectassessment.dtos.request.TaskRequest;
import com.kcb.projectassessment.dtos.response.TaskResponse;
import com.kcb.projectassessment.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskService {

    TaskResponse addTaskToProject(UUID projectId, TaskRequest taskRequest);

    List<TaskResponse> getTasksForProject(UUID projectId, TaskStatus status, LocalDate dueDate);

    TaskResponse updateTask(UUID projectId, UUID taskId, TaskRequest taskRequest);

    void deleteTask(UUID projectId,UUID taskId);
}
