package com.project.cafeteriaManagementSystem.model.MenuItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
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

    @NotNull(message = "A taxa de lucro deve ser informada")
    @DecimalMin(value = "0.10", message = "A taxa de lucro mínima é de 10%")
    @DecimalMax(value = "1.00", message = "A taxa de lucro máxima é de 100%")
    private BigDecimal profitMargin;
}
