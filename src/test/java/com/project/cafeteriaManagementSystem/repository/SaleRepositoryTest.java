package com.project.cafeteriaManagementSystem.repository;

import com.project.cafeteriaManagementSystem.model.sale.SaleDomainTest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepositoryTest extends MongoRepository<SaleDomainTest, String> {

    List<SaleDomainTest> findByDateOfSaleBetween(LocalDate startDate, LocalDate endDate);
}