package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.model.extract.ExtractResponseTest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExtractServiceTest {

    private final BatchServiceTest batchServiceTest;
    private final SaleServiceTest saleServiceTest;

    public ExtractResponseTest getCostAndProfitForTimePeriod(int duration) {
        // Chama o serviço para calcular o custo total dos lotes no período especificado
        BigDecimal totalCost = batchServiceTest.calculateTotalCostBatchesForTimePeriod(duration);

        // Chama o serviço para calcular o lucro total das vendas no período especificado
        BigDecimal totalProfit = saleServiceTest.getProfitForTimePeriod(duration);

        // Calcula o lucro bruto (lucro total das vendas - custo total dos lotes)
        BigDecimal grossProfit = totalProfit.subtract(totalCost);

        // Lucro líquido é o mesmo que o lucro bruto, já que não há outros custos considerados

        return new ExtractResponseTest(totalCost, grossProfit, grossProfit);
    }
}
