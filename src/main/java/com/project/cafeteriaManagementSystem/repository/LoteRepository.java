package com.project.cafeteriaManagementSystem.repository;

import com.project.cafeteriaManagementSystem.model.Lote.LoteDomain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteRepository extends MongoRepository<LoteDomain, String> {

    List<LoteDomain> findByDateCreatedBetween(LocalDate startDate, LocalDate endDate);
}
