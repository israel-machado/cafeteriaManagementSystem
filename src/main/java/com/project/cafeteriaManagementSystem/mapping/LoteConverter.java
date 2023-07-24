package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.Lote.LoteDomain;
import com.project.cafeteriaManagementSystem.model.Lote.LoteResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoteConverter {

    // Método para converter um objeto LoteDomain em um objeto LoteResponse
    public LoteResponse convertLoteDomainToResponse(LoteDomain loteDomain) {
        return LoteResponse.builder()
                .id(loteDomain.getId())
                .amountToBeConsumed(loteDomain.getAmountToBeConsumed())
                .totalCost(loteDomain.getTotalCost())
                .validity(loteDomain.getValidity())
                .remainingQuantity(loteDomain.getRemainingQuantity())
                .build();
    }

    // Método para converter uma lista de objetos LoteDomain em uma lista de objetos LoteResponse
    public List<LoteResponse> convertLoteDomainListToResponseList(List<LoteDomain> loteDomainList) {
        // Verifica se a lista de LoteDomain é nula, se sim, retorna uma lista vazia de LoteResponse
        if (loteDomainList == null) {
            return Collections.emptyList();
        }

        // Utiliza o Stream API do Java para mapear cada objeto LoteDomain para um objeto LoteResponse
        // e coleta os resultados em uma lista usando Collectors.toList()
        return loteDomainList.stream()
                .map(this::convertLoteDomainToResponse)
                .collect(Collectors.toList());
    }
}
