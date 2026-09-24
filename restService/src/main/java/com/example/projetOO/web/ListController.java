package com.example.projetOO.web;

import com.example.projetOO.entities.Status;
import com.example.projetOO.service.GrpcRestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ListController {
    private final GrpcRestService service;

    public ListController(GrpcRestService service) {
        this.service = service;
    }

    @GetMapping("/users/{userId}/entries")
    public List<GrpcRestService.EntryResponse> getEntries(@PathVariable Long userId) {return service.getEntries(userId);}

    @PostMapping("/users/{userId}/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public GrpcRestService.EntryResponse addEntry(@PathVariable Long userId,
            @RequestBody GrpcRestService.EntryRequest request) {
        return service.createEntry(userId, request);
    }

    @PostMapping("/users/{userId}/entries/{workId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GrpcRestService.EntryResponse addEntry(@PathVariable Long userId, @PathVariable Long workId,
            @RequestParam(defaultValue = "PLANNED") Status status) {
        return service.createEntry(userId, new GrpcRestService.EntryRequest(workId, status.name(), 0, 0));
    }

    @PutMapping("/entries/{entryId}")
    public GrpcRestService.EntryResponse updateEntry(@PathVariable Long entryId,
            @RequestBody GrpcRestService.EntryRequest request) {
        return service.updateEntry(entryId, request);
    }

    @PatchMapping("/entries/{entryId}/progress")
    public GrpcRestService.EntryResponse updateProgress(@PathVariable Long entryId, @RequestParam int chapter) {
        return service.updateProgress(entryId, chapter);
    }

    @PatchMapping("/entries/{entryId}/rating")
    public GrpcRestService.EntryResponse updateRating(@PathVariable Long entryId, @RequestParam int rating) {
        return service.updateRating(entryId, rating);
    }

    @PatchMapping("/entries/{entryId}/status")
    public GrpcRestService.EntryResponse updateStatus(@PathVariable Long entryId, @RequestParam Status status) {
        return service.updateStatus(entryId, status.name());
    }

    @DeleteMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable Long entryId) {
        service.deleteEntry(entryId);
    }
}
