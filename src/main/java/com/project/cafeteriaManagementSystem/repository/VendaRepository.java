package com.project.cafeteriaManagementSystem.repository;

import com.project.cafeteriaManagementSystem.model.Venda.VendaDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VendaRepository extends MongoRepository<VendaDomain, String> {
}