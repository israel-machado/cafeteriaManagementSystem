package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.Lote.LoteDomain;
import com.project.cafeteriaManagementSystem.model.Lote.LoteResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoteConverter {

    public LoteResponse convertLoteDomainToResponse(LoteDomain loteDomain) {
        return LoteResponse.builder()
                .id(loteDomain.getId())
                .amountToBeConsumed(loteDomain.getAmountToBeConsumed())
                .totalCost(loteDomain.getTotalCost())
                .validity(loteDomain.getValidity())
                .build();
    }

    //Lists

    public List<LoteResponse> convertLoteDomainListToResponseList(List<LoteDomain> loteDomainList) {
        return loteDomainList.stream()
                .map(this::convertLoteDomainToResponse)
                .collect(Collectors.toList());
    }
}
