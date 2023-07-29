package com.project.cafeteriaManagementSystem.model.batch;

import com.project.cafeteriaManagementSystem.model.material.MaterialRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BatchRequestTest {

    @Test
    void testBatchRequest() {
        // Crie um objeto MaterialRequest
        MaterialRequest materialRequest = new MaterialRequest();
        materialRequest.setName("Material 1");

        // Crie um objeto BatchRequest
        BatchRequest batchRequest = new BatchRequest();
        batchRequest.setInitialAmount(100.0);
        batchRequest.setTotalCost(BigDecimal.valueOf(50.0));
        batchRequest.setValidity(LocalDateTime.of(2023, 7, 1, 12, 0));
        batchRequest.setDateOfPurchase(LocalDateTime.of(2023, 6, 30, 12, 0));
        batchRequest.setSupplierName("Fornecedor 1");
        batchRequest.setMaterialRequest(materialRequest);

        // Verifique os valores dos campos do objeto BatchRequest
        assertEquals(100.0, batchRequest.getInitialAmount());
        assertEquals(BigDecimal.valueOf(50.0), batchRequest.getTotalCost());
        assertEquals(LocalDateTime.of(2023, 7, 1, 12, 0), batchRequest.getValidity());
        assertEquals(LocalDateTime.of(2023, 6, 30, 12, 0), batchRequest.getDateOfPurchase());
        assertEquals("Fornecedor 1", batchRequest.getSupplierName());
        assertEquals(materialRequest, batchRequest.getMaterialRequest());
    }
}
