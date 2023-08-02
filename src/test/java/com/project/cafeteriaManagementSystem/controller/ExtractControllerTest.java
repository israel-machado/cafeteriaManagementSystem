package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.extract.ExtractResponse;
import com.project.cafeteriaManagementSystem.service.ExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtractControllerTest {

    @Mock
    private ExtractService extractService;

    @InjectMocks
    private ExtractController extractController;

    @Test
    void testGetExtractForTimePeriod() {
        // Mock do resultado retornado pelo serviço
        BigDecimal totalCost = BigDecimal.valueOf(1000);
        BigDecimal grossProfit = BigDecimal.valueOf(500);
        BigDecimal netProfit = BigDecimal.valueOf(500);

        ExtractResponse mockResponse = ExtractResponse.builder()
                .totalCost(totalCost)
                .grossProfit(grossProfit)
                .netProfit(netProfit)
                .build();

        // Configura o mock do serviço para retornar o resultado esperado
        int duration = 30;
        when(extractService.getCostAndProfitForTimePeriod(duration)).thenReturn(mockResponse);

        // Chama o método do controller
        ResponseEntity<ExtractResponse> responseEntity = extractController.getExtractForTimePeriod(duration);

        // Verifica se o status code da resposta é 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verifica se o objeto ExtractResponse retornado no corpo da resposta é o esperado
        assertEquals(mockResponse, responseEntity.getBody());
    }
}