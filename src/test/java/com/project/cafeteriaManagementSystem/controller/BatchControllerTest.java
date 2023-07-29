package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.batch.BatchResponse;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponseTest;
import com.project.cafeteriaManagementSystem.service.BatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchControllerTest {

    @Mock
    private BatchService batchService;

    @InjectMocks
    private BatchController batchController;

    @Test
    void testGetAllBatches() {
        // Mock of a list of BatchResponse
        List<BatchResponse> batchResponses = Arrays.asList(
                new BatchResponse("1", 100.0, BigDecimal.valueOf(50.0), LocalDateTime.now(), LocalDateTime.now().minusDays(1), "Fornecedor 1", 100.0, 0.0, null),
                new BatchResponse("2", 200.0, BigDecimal.valueOf(100.0), LocalDateTime.now(), LocalDateTime.now().minusDays(2), "Fornecedor 2", 200.0, 0.0, null)
        );

        when(batchService.getAllBatches()).thenReturn(batchResponses);

        // Use doAnswer to handle the conversion from List<BatchResponse> to List<BatchResponseTest>
        doAnswer(invocation -> {
            List<BatchResponse> response = invocation.getArgument(0);
            List<BatchResponseTest> testResponse = response.stream()
                    .map(this::convertToTestResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(testResponse);
        }).when(batchController).getAllBatches();

        ResponseEntity<List<BatchResponse>> responseEntity = batchController.getAllBatches();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // You can verify the contents of the response as needed
    }

    private BatchResponseTest convertToTestResponse(BatchResponse response) {
        // Convert BatchResponse to BatchResponseTest manually here
        // For example:
        return new BatchResponseTest(response.getId(), response.getInitialAmount(), response.getTotalCost(),
                response.getValidity(), response.getDateOfPurchase(), response.getSupplierName(),
                response.getRemainingAmount(), response.getWasteAmount(), response.getMaterialDomain());
    }
}