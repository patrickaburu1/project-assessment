package com.kcb.projectassessment.dtos.request;

import com.kcb.projectassessment.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {
    @NotBlank(message = "Task title is required.")
    private String title;

    private String description;

    @NotNull(message = "Status is required.")
    private TaskStatus status;

    @FutureOrPresent(message = "Due date must be today or in the future.")
    private LocalDate dueDate;
}
