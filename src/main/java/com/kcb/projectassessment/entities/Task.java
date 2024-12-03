package com.kcb.projectassessment.entities;

import com.kcb.projectassessment.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private Date createAt =new Date();

    @Column(nullable = false)
    private Date updatedAt = new Date();
}

