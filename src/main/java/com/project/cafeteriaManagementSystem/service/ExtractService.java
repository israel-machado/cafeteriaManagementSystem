package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.model.extract.ExtractResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExtractService {

    private final BatchService batchService;
    private final SaleService saleService;

    public ExtractResponse getCostAndProfitLast30Days() {
        // Chama o serviço para calcular o custo total dos lotes nos últimos 30 dias
        BigDecimal totalCostLotesLast30Days = batchService.calculateTotalCostBatchesLast30Days();

        // Chama o serviço para calcular o lucro total das vendas nos últimos 30 dias
        BigDecimal totalProfitLast30Days = saleService.getProfitLast30Days();

        return new ExtractResponse(totalCostLotesLast30Days, totalProfitLast30Days);
    }
}
