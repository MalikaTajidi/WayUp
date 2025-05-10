package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.entities.Formation;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findByUserId(int userId);
}
