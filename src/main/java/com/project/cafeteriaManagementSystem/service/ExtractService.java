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
        BigDecimal totalCost = batchService.calculateTotalCostBatchesForTimePeriod(duration);

        // Chama o serviço para calcular o lucro total das vendas no período especificado
        BigDecimal totalProfit = saleService.getProfitForTimePeriod(duration);

        // Calcula o lucro bruto (lucro total das vendas - custo total dos lotes)
        BigDecimal grossProfit = totalProfit.subtract(totalCost);

        // Lucro líquido é o mesmo que o lucro bruto, já que não há outros custos considerados

        return new ExtractResponse(totalCost, grossProfit, grossProfit);
    }
}
