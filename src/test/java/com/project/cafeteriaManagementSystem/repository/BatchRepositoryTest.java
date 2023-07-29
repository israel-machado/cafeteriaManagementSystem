package com.project.cafeteriaManagementSystem.repository;

import com.project.cafeteriaManagementSystem.model.batch.BatchDomainTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BatchRepositoryTest extends MongoRepository<BatchDomainTest, String> {

    List<BatchDomainTest> findByDateOfPurchaseBetween(LocalDate startDate, LocalDate endDate);
}
