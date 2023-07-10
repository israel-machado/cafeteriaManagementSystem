package com.project.cafeteriaManagementSystem.model.Lote;

import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoteResponse {

    private String id;
    private Double amountConsumed;
    private BigDecimal totalCost;
    private Date validity;
    private MaterialDomain materialDomain;
}
