package com.kcb.projectassessment.dtos.response;

import com.kcb.projectassessment.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class TaskResponse {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private UUID projectId;
}
