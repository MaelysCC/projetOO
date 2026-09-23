package com.example.projetOO.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_entry_seq")
    @SequenceGenerator(name = "list_entry_seq", sequenceName = "list_entry_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Work work;

    private Status status;
    private int currentChapter;
    private int rating;

    public Entry() {
    }

    public Entry(User user, Work work, Status status, int currentChapter, int rating) {
        this.user = user;
        this.work = work;
        this.status = status;
        this.currentChapter = currentChapter;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(int currentChapter) {
        this.currentChapter = currentChapter;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}