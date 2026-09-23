package com.example.projetOO.service;

import java.util.List;
import com.example.projetOO.entities.User;
import com.example.projetOO.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.projetOO.exeptions.RessourceNotFoundExeption;

@Service
public class UserService {
    private final UserRepository userRepository;

    private UserService(UserRepository userRepository){
        this.userRepository = userRepository;

    }

    public List<User> getAllUsers(){return userRepository.findAll();}

    public User getUser(Long id){return userRepository.findById(id)
            .orElseThrow(()-> new RessourceNotFoundExeption("User not found "+id));}

    public User createUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(Long id, User newuser){
        User user = getUser(id);
        user.setUsername(newuser.getUsername());
        user.setEmail(newuser.getEmail());

        return userRepository.save(user);
    }

    public void deleteUser(Long id){
        User user = getUser(id);
        userRepository.delete(user);
    }




}
