package com.kcb.projectassessment.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @Column(nullable = false)
    private Date createAt =new Date();

    @Column(nullable = false)
    private Date updatedAt = new Date();
}
