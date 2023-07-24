package com.project.cafeteriaManagementSystem.repository;

import com.project.cafeteriaManagementSystem.model.Venda.VendaDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface VendaRepository extends MongoRepository<VendaDomain, String> {

    List<VendaDomain> findByDateBetween(LocalDate startDate, LocalDate endDate);
}