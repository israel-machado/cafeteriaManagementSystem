package com.project.cafeteriaManagementSystem.services;

import com.project.cafeteriaManagementSystem.model.Lote.LoteDomain;
import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.Material.MaterialRequest;
import com.project.cafeteriaManagementSystem.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository loteRepository;

    public LoteDomain createLote(MaterialRequest materialRequest, MaterialDomain materialDomain) {

        // Calculando o custo total com base nos dados do material
        BigDecimal calculatedTotalCost = calculateTotalCost(materialRequest);

        // Criando o objeto LoteDomain com as informações calculadas e o MaterialDomain associado
        LoteDomain loteDomain = LoteDomain.builder()
                .amountToBeConsumed(materialRequest.getQuantity())
                .totalCost(calculatedTotalCost)
                .validity(materialRequest.getLoteRequest().getValidity())
                .materialDomain(materialDomain)
                .build();

        return loteRepository.save(loteDomain);
    }

    private BigDecimal calculateTotalCost(MaterialRequest materialRequest) {
        BigDecimal quantity = BigDecimal.valueOf(materialRequest.getQuantity());
        BigDecimal totalCost = quantity.multiply(materialRequest.getCost());
        totalCost = totalCost.setScale(2, RoundingMode.HALF_UP);
        return totalCost;
    }
}
