package com.example.projetOO.service;

import com.example.projetOO.entities.Entry;
import com.example.projetOO.entities.User;
import com.example.projetOO.entities.Work;
import com.example.projetOO.entities.Status;

import com.example.projetOO.exeptions.RessourceNotFoundExeption;
import com.example.projetOO.repository.ListRepository;
import com.example.projetOO.repository.UserRepository;
import com.example.projetOO.repository.WorkRepository;

import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ListService {

    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;


    private ListService(ListRepository listRepository, UserRepository userRepository, WorkRepository workRepository){
        this.listRepository = listRepository;
        this.userRepository = userRepository;
        this.workRepository = workRepository;
    }


    public List<Entry> getEntriesForUser(Long userId) {
        if (!userRepository.existsById(userId)) {throw new RessourceNotFoundExeption("User not found "+userId);}
        return listRepository.findByUserId(userId);
    }

    public Entry addWorkToList(Long userID, Long workID, Status status){
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new RessourceNotFoundExeption("User not found "+userID));
        Work work = workRepository.findById(workID)
                .orElseThrow(()-> new RessourceNotFoundExeption("Work not found "+workID));
        Entry entry = new Entry(user, work, status, 0, 0);
        return listRepository.save(entry);
    }

    public Entry updateProgress(Long entryId, int chapter) {
        Entry entry = listRepository.findById(entryId)
                .orElseThrow(() -> new RessourceNotFoundExeption("Entry not found"));

        entry.setCurrentChapter(chapter);
        return listRepository.save(entry);
    }

    public void deleteEntry(Long entryId) {
        Entry entry = listRepository.findById(entryId)
                .orElseThrow(() -> new RessourceNotFoundExeption("Entry not found"));

        listRepository.delete(entry);
    }

}

