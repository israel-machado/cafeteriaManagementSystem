package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.batch.BatchDomainTest;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponseTest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BatchConverterTest {

    // Método para converter um objeto LoteDomain em um objeto LoteResponse
    public BatchResponseTest convertBatchDomainToResponse(BatchDomainTest batchDomainTest) {
        return BatchResponseTest.builder()
                .id(batchDomainTest.getId())
                .initialAmount(batchDomainTest.getInitialAmount())
                .totalCost(batchDomainTest.getTotalCost())
                .validity(batchDomainTest.getValidity())
                .dateOfPurchase(batchDomainTest.getDateOfPurchase())
                .supplierName(batchDomainTest.getSupplierName())
                .initialAmount(batchDomainTest.getInitialAmount())
                .remainingAmount(batchDomainTest.getRemainingAmount())
                .wasteAmount(batchDomainTest.getWasteAmount())
                .materialDomainTest(batchDomainTest.getMaterialDomainTest())
                .build();
    }

    // Método para converter uma lista de objetos LoteDomain em uma lista de objetos LoteResponse
    public List<BatchResponseTest> convertBatchDomainListToResponseList(List<BatchDomainTest> batchDomainTestList) {
        // Verifica se a lista de LoteDomain é nula, se sim, retorna uma lista vazia de LoteResponse
        if (batchDomainTestList == null) {
            return Collections.emptyList();
        }

        // Utiliza o Stream API do Java para mapear cada objeto LoteDomain para um objeto LoteResponse
        // e coleta os resultados em uma lista usando Collectors.toList()
        return batchDomainTestList.stream()
                .map(this::convertBatchDomainToResponse)
                .collect(Collectors.toList());
    }
}
