package com.example.projetOO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.projetOO.entities.Entry;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByUserId(Long userId);
}
