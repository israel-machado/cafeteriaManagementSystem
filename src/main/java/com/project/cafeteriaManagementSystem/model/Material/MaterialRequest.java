package com.project.cafeteriaManagementSystem.model.Material;

import com.project.cafeteriaManagementSystem.model.Lote.LoteRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialRequest {

    @NotBlank(message = "O nome do material é obrigatório")
    private String name;

    @NotNull(message = "A quantidade do material deve ser informada")
    @DecimalMin(value = "0.01", message = "A quantidade do material deve ser maior que zero")
    private Double quantity;

    @NotBlank(message = "A unidade de medida do material é obrigatória")
    private String unitMeasure;

    @NotNull(message = "O custo do material deve ser informado")
    @DecimalMin(value = "0.01", message = "O custo do material deve ser maior que zero")
    private BigDecimal cost;

    @Valid // Anote o objeto LoteRequest para ativar a validação recursiva
    private LoteRequest loteRequest;
}
