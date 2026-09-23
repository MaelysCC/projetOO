package com.example.projetOO.web;
import com.example.projetOO.entities.Work;
import com.example.projetOO.service.WorkService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/works")
public class WorkController {
    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    @GetMapping
    public List<Work> getWorks() {return workService.getAllWork();}

    @GetMapping("/{id}")
    public Work getWork(@PathVariable Long id) {return workService.getWork(id);}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Work createWork(@RequestBody Work work) {return workService.createWork(work);}

    @PutMapping("/{id}")
    public Work updateWork(@PathVariable Long id, @RequestBody Work work) {
        return workService.updateWork(id, work);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWork(@PathVariable Long id) {
        workService.deleteWork(id);
    }
}
