package com.kcb.projectassessment.entrpoint;

import com.kcb.projectassessment.dtos.request.TaskRequest;
import com.kcb.projectassessment.dtos.response.TaskResponse;
import com.kcb.projectassessment.enums.TaskStatus;
import com.kcb.projectassessment.execption.RecordAlreadyExistsException;
import com.kcb.projectassessment.execption.RecordNotFoundException;
import com.kcb.projectassessment.services.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
@AllArgsConstructor
@Validated
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<?> addTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody TaskRequest taskRequest) {

        try {
            TaskResponse taskResponse = taskService.addTaskToProject(projectId, taskRequest);
            return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);

        } catch (RecordAlreadyExistsException ex) {
            return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.CONFLICT);
        }


    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @PathVariable UUID projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) LocalDate dueDate) {
        List<TaskResponse> taskResponses = taskService.getTasksForProject(projectId, status, dueDate);
        return new ResponseEntity<>(taskResponses, HttpStatus.OK);

    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskRequest taskRequest) {
        TaskResponse taskResponse = taskService.updateTask(projectId, taskId, taskRequest);
        return new ResponseEntity<>(taskResponse, HttpStatus.OK);

    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID projectId, @PathVariable UUID taskId) {
        try {
            taskService.deleteTask(projectId, taskId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (RecordNotFoundException ex) {
            return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
