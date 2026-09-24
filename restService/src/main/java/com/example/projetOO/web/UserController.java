package com.example.projetOO.web;

import com.example.projetOO.entities.User;
import com.example.projetOO.service.GrpcRestService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final GrpcRestService service;

    public UserController(GrpcRestService service) {
        this.service = service;
    }

    @GetMapping
    public List<GrpcRestService.UserResponse> getUsers() {
        return service.getUsers();
    }

    @GetMapping("/{id}")
    public GrpcRestService.UserResponse getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GrpcRestService.UserResponse createUser(@RequestBody User user) {
        return service.createUser(user);
    }

    @PutMapping("/{id}")
    public GrpcRestService.UserResponse updateUser(@PathVariable Long id, @RequestBody User user){
        return service.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id){service.deleteUser(id);}
}
