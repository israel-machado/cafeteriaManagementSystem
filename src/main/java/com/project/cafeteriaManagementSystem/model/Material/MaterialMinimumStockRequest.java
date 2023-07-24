package com.project.cafeteriaManagementSystem.model.Material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialMinimumStockRequest {

    @NotNull(message = "O ID do material deve ser informado")
    private String materialId;

    @NotNull(message = "O estoque mínimo deve ser informado")
    @Min(value = 1, message = "O estoque mínimo deve ser maior que zero")
    private double minimumStockQuantity;
}
