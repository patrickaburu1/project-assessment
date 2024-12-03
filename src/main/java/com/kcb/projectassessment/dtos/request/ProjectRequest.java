package com.kcb.projectassessment.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectRequest {

    @NotBlank(message = "Project name is required.")
    private String name;

    private String description;
}
