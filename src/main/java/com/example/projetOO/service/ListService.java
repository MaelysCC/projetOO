package com.example.projetOO.service;

import com.example.projetOO.entities.Entry;
import com.example.projetOO.entities.User;
import com.example.projetOO.entities.Work;
import com.example.projetOO.entities.Status;

import com.example.projetOO.exeptions.RessourceNotFoundExeption;
import com.example.projetOO.repository.EntryRepository;
import com.example.projetOO.repository.UserRepository;
import com.example.projetOO.repository.WorkRepository;

import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ListService {

    private final EntryRepository entryRepository;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;


    public ListService(EntryRepository entryRepository, UserRepository userRepository, WorkRepository workRepository){
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
        this.workRepository = workRepository;
    }
}

