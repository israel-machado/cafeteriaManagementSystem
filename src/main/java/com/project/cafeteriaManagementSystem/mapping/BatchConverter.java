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
    public BatchResponse convertLoteDomainToResponse(BatchDomain batchDomain) {
        return BatchResponse.builder()
                .id(batchDomain.getId())
                .amountToBeConsumed(batchDomain.getAmountToBeConsumed())
                .totalCost(batchDomain.getTotalCost())
                .validity(batchDomain.getValidity())
                .remainingQuantity(batchDomain.getRemainingQuantity())
                .build();
    }

    // Método para converter uma lista de objetos LoteDomain em uma lista de objetos LoteResponse
    public List<BatchResponse> convertLoteDomainListToResponseList(List<BatchDomain> batchDomainList) {
        // Verifica se a lista de LoteDomain é nula, se sim, retorna uma lista vazia de LoteResponse
        if (batchDomainList == null) {
            return Collections.emptyList();
        }

        // Utiliza o Stream API do Java para mapear cada objeto LoteDomain para um objeto LoteResponse
        // e coleta os resultados em uma lista usando Collectors.toList()
        return batchDomainList.stream()
                .map(this::convertLoteDomainToResponse)
                .collect(Collectors.toList());
    }
}
