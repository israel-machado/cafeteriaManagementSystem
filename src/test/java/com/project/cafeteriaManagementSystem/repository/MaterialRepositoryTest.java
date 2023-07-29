package com.project.cafeteriaManagementSystem.repository;

import com.project.cafeteriaManagementSystem.model.material.MaterialDomainTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepositoryTest extends MongoRepository<MaterialDomainTest, String> {

    boolean existsById(String id);
    MaterialDomainTest findByName(String name);
}