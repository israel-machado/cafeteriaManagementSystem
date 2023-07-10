package com.project.cafeteriaManagementSystem.model.Material;

import com.project.cafeteriaManagementSystem.model.Lote.LoteRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialRequest {

    private String name;
    private Double quantity;
    private String unitMeasure;
    private BigDecimal cost;
    private LoteRequest loteRequest;
}
