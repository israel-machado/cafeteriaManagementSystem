package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.model.extract.ExtractResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtractServiceTest {

    @Mock
    private BatchService batchService;

    @Mock
    private SaleService saleService;

    @InjectMocks
    private ExtractService extractService;

    @Test
    void testGetCostAndProfitForTimePeriod() {
        // Mock do resultado retornado pelo BatchService
        BigDecimal totalCost = BigDecimal.valueOf(1000);
        when(batchService.calculateTotalCostBatchesForTimePeriod(anyInt())).thenReturn(totalCost);

        // Mock do resultado retornado pelo SaleService
        BigDecimal totalProfit = BigDecimal.valueOf(500);
        when(saleService.getProfitForTimePeriod(anyInt())).thenReturn(totalProfit);

        // Chama o método do service
        int duration = 30;
        ExtractResponse result = extractService.getCostAndProfitForTimePeriod(duration);

        // Verifica se os métodos dos serviços foram chamados corretamente
        verify(batchService, times(1)).calculateTotalCostBatchesForTimePeriod(duration);
        verify(saleService, times(1)).getProfitForTimePeriod(duration);

        // Verifica se o resultado retornado pelo service é o esperado
        BigDecimal expectedGrossProfit = totalProfit.subtract(totalCost);
        ExtractResponse expectedResponse = ExtractResponse.builder()
                .totalCost(totalCost)
                .grossProfit(expectedGrossProfit)
                .netProfit(expectedGrossProfit)
                .build();
        assertEquals(expectedResponse, result);
    }
}