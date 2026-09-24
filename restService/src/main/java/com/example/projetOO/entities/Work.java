package com.example.projetOO.entities;

import jakarta.persistence.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_seq")
    @SequenceGenerator(name = "work_seq", sequenceName = "work_id_seq", allocationSize = 1)
    private Long id;

    private String title;
    private String author;
    private String description;

    @Enumerated(EnumType.STRING)
    private WorkType type;
    @Enumerated(EnumType.STRING)
    private Status status;

    public Work() {
    }

    public Work(String title, WorkType type, String author, String description, Status status) {
        this.title = title;
        this.type = type;
        this.author = author;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WorkType getType() {
        return type;
    }

    public void setType(WorkType type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}