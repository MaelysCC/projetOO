package com.example.projetOO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.projetOO.entities.Work;

public interface WorkRepository extends JpaRepository<Work, Long> {
}
