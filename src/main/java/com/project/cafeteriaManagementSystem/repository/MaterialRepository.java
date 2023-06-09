package com.project.cafeteriaManagementSystem.repository;

import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends MongoRepository<MaterialDomain, String> {

    boolean existsByName(String name);
    boolean existsById(String id);
    MaterialDomain findByName(String name);
}
