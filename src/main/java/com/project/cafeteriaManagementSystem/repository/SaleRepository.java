package com.project.cafeteriaManagementSystem.repository;

import com.project.cafeteriaManagementSystem.model.sale.SaleDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepository extends MongoRepository<SaleDomain, String> {

    List<SaleDomain> findByDateBetween(LocalDate startDate, LocalDate endDate);
}