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

    public ExtractResponse getCostAndProfitForTimePeriod(int duration) {
        // Chama o serviço para calcular o custo total dos lotes no período especificado
        BigDecimal totalCostBatches = batchService.calculateTotalCostBatchesForTimePeriod(duration);

        // Chama o serviço para calcular o lucro total das vendas no período especificado
        BigDecimal totalProfit = saleService.getProfitForTimePeriod(duration);

        return new ExtractResponse(totalCostBatches, totalProfit);
    }
}
