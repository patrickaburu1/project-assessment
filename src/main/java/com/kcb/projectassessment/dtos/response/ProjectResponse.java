package com.kcb.projectassessment.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProjectResponse {
    private UUID id;
    private String name;
    private String description;
    private long taskCount;
}
