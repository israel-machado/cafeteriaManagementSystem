package com.project.cafeteriaManagementSystem.model.MenuItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MenuItemRequest {

    @NotBlank(message = "O nome do item do menu é obrigatório")
    private String name;

    @NotNull(message = "O valor de venda deve ser informado")
    @DecimalMin(value = "0.01", message = "O valor de venda deve ser maior que zero")
    private BigDecimal saleValue;

    @NotEmpty(message = "A lista de materiais não pode estar vazia")
    private List<String> materialsRecipeNames;
}
