package com.project.cafeteriaManagementSystem.model.Lote;

import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoteResponse {

    private String id;
    private Double amountToBeConsumed;
    private BigDecimal totalCost;
    private LocalDate validity;
    private MaterialDomain materialDomain;
    private Double remainingQuantity;
}
