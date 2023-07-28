package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.batch.BatchDomain;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BatchConverter {

    // Método para converter um objeto LoteDomain em um objeto LoteResponse
    public BatchResponse convertBatchDomainToResponse(BatchDomain batchDomain) {
        return BatchResponse.builder()
                .id(batchDomain.getId())
                .quantity(batchDomain.getQuantity())
                .cost(batchDomain.getCost())
                .totalCost(batchDomain.getTotalCost())
                .validity(batchDomain.getValidity())
                .dateOfPurchase(batchDomain.getDateOfPurchase())
                .supplierName(batchDomain.getSupplierName())
                .initialAmount(batchDomain.getInitialAmount())
                .remainingAmount(batchDomain.getRemainingAmount())
                .wasteAmount(batchDomain.getWasteAmount())
                .materialDomain(batchDomain.getMaterialDomain())
                .build();
    }

    // Método para converter uma lista de objetos LoteDomain em uma lista de objetos LoteResponse
    public List<BatchResponse> convertBatchDomainListToResponseList(List<BatchDomain> batchDomainList) {
        // Verifica se a lista de LoteDomain é nula, se sim, retorna uma lista vazia de LoteResponse
        if (batchDomainList == null) {
            return Collections.emptyList();
        }

        // Utiliza o Stream API do Java para mapear cada objeto LoteDomain para um objeto LoteResponse
        // e coleta os resultados em uma lista usando Collectors.toList()
        return batchDomainList.stream()
                .map(this::convertBatchDomainToResponse)
                .collect(Collectors.toList());
    }
}
