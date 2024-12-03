package com.kcb.projectassessment.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ProjectSummaryResponse {

    private UUID projectId;
    private String projectName;
    private String description;
    private Map<String, Long> taskCountByStatus;
}
