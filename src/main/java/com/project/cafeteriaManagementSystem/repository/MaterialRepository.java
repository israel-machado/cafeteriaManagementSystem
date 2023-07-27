package com.project.cafeteriaManagementSystem.repository;

import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaterialRepository extends MongoRepository<MaterialDomain, String> {

    boolean existsById(String id);
    MaterialDomain findByName(String name);
    List<MaterialDomain> findByValidity(LocalDate date);
}