package com.project.cafeteriaManagementSystem.services;

import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import com.project.cafeteriaManagementSystem.repository.LoteRepository;
import com.project.cafeteriaManagementSystem.model.Lote.LoteDomain;
import com.project.cafeteriaManagementSystem.model.Material.MaterialRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Service
public class LoteService {

    private LoteRepository loteRepository;

    public LoteDomain createLote(MaterialRequest materialRequest, MaterialDomain materialDomain) {

        // Calculando o custo total com base nos dados do material
        BigDecimal calculatedTotalCost = calculateTotalCost(materialRequest);

        // Quantidade consumida iniciada em 0
        Double initialAmountConsumed = 0.0;

        // Criando a validade inicial do lote
        Date initialValidity = calculateInitialValidity();

        // Criando o objeto LoteDomain com as informações calculadas e o MaterialDomain associado
        LoteDomain loteDomain = LoteDomain.builder()
                .amountConsumed(initialAmountConsumed)
                .totalCost(calculatedTotalCost)
                .validity(initialValidity)
                .materialDomain(materialDomain)
                .build();

        return loteRepository.save(loteDomain);
    }

    private BigDecimal calculateTotalCost(MaterialRequest materialRequest) {
        BigDecimal quantity = BigDecimal.valueOf(materialRequest.getQuantity());
        return quantity.multiply(materialRequest.getCost());
    }

    private Date calculateInitialValidity() {
        //TODO Checar questão de informar a data
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }
}
