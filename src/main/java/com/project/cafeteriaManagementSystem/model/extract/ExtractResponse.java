package com.project.cafeteriaManagementSystem.model.extract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExtractResponse {

    private BigDecimal totalCostLotesLast30Days;
    private BigDecimal totalProfitLast30Days;
}