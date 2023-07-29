package com.project.cafeteriaManagementSystem.model.batch;

import com.project.cafeteriaManagementSystem.model.material.MaterialRequestTest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BatchRequestTest {

    private Double initialAmount;
    private BigDecimal totalCost;
    private LocalDateTime validity;
    private LocalDateTime dateOfPurchase;
    private String supplierName;
    private MaterialRequestTest materialRequestTest;
}
