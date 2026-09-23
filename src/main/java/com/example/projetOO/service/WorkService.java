package com.example.projetOO.service;


import org.springframework.stereotype.Service;
import java.util.List;

import com.example.projetOO.exeptions.RessourceNotFoundExeption;
import com.example.projetOO.repository.WorkRepository;
import com.example.projetOO.entities.Work;


@Service
public class WorkService {

    private final WorkRepository workRepository;


    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    public List<Work> getAllWork(){return workRepository.findAll();}
    public Work getWork(Long id){return workRepository.findById(id)
            .orElseThrow(() -> new RessourceNotFoundExeption("Work not found "+id));}

    public Work createWork(Work work){return workRepository.save(work);}
    public Work updateWork (Long id, Work newwork){
        Work work = getWork(id);

        work.setTitle(newwork.getTitle());
        work.setType(newwork.getType());
        work.setAuthor(newwork.getAuthor());
        work.setDescription(newwork.getDescription());
        work.setStatus(newwork.getStatus());

        return workRepository.save(work);
    }

    public void deleteWork (Long id){
        Work work = getWork(id);
        workRepository.delete(work);
    }


}
