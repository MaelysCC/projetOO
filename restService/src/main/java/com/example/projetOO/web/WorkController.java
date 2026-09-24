package com.example.projetOO.web;
import com.example.projetOO.entities.Work;
import com.example.projetOO.service.GrpcRestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/works")
public class WorkController {
    private final GrpcRestService service;

    public WorkController(GrpcRestService service) {
        this.service = service;
    }

    @GetMapping
    public List<GrpcRestService.WorkResponse> getWorks() {return service.getWorks();}

    @GetMapping("/{id}")
    public GrpcRestService.WorkResponse getWork(@PathVariable Long id) {return service.getWork(id);}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GrpcRestService.WorkResponse createWork(@RequestBody Work work) {return service.createWork(work);}

    @PutMapping("/{id}")
    public GrpcRestService.WorkResponse updateWork(@PathVariable Long id, @RequestBody Work work) {
        return service.updateWork(id, work);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWork(@PathVariable Long id) {
        service.deleteWork(id);
    }
}
