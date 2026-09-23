package com.example.projetOO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.projetOO.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
