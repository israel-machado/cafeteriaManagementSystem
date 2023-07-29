package com.project.cafeteriaManagementSystem.model.batch;

import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchResponseTest {
    private String id;
    private Double initialAmount;
    private BigDecimal totalCost;
    private LocalDateTime validity;
    private LocalDateTime dateOfPurchase;
    private String supplierName;
    private Double remainingAmount;
    private Double wasteAmount;
    private MaterialDomain materialDomain;
}




