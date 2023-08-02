package com.project.cafeteriaManagementSystem.util;

import com.project.cafeteriaManagementSystem.model.batch.BatchDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Calculation {

    // Método para atualizar o estoque de todos materiais
    public double calculateStock(MaterialDomain materialDomain) {
        List<BatchDomain> batchDomainList = materialDomain.getBatchDomainList();
        double totalStock = 0.0;

        for (BatchDomain batch : batchDomainList) {
            totalStock += batch.getRemainingAmount();
        }

        return totalStock;
    }
}
