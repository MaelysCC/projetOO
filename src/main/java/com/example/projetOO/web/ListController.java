package com.example.projetOO.web;

import com.example.projetOO.entities.Entry;
import com.example.projetOO.entities.Status;
import com.example.projetOO.service.ListService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ListController {
    private final ListService listService;

    public ListController(ListService listService) {
        this.listService = listService;
    }

    @GetMapping("/users/{userId}/entries")
    public List<Entry> getEntries(@PathVariable Long userId) {return listService.getEntriesForUser(userId);}

    @PostMapping("/users/{userId}/entries/{workId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Entry addEntry(@PathVariable Long userId, @PathVariable Long workId, @RequestParam(defaultValue = "PLANNED") Status status) {return listService.addWorkToList(userId, workId, status);}

    @PatchMapping("/entries/{entryId}/progress")
    public Entry updateProgress(@PathVariable Long entryId, @RequestParam int chapter) {
        return listService.updateProgress(entryId, chapter);
    }

    @PatchMapping("/entries/{entryId}/rating")
    public Entry updateRating(@PathVariable Long entryId, @RequestParam int rating) {
        return listService.updateRating(entryId, rating);
    }

    @PatchMapping("/entries/{entryId}/status")
    public Entry updateStatus(@PathVariable Long entryId, @RequestParam Status status) {
        return listService.updateStatus(entryId, status);
    }

    @DeleteMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable Long entryId) {
        listService.deleteEntry(entryId);
    }
}