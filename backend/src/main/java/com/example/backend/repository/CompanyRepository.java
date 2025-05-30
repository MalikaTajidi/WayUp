package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entities.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
