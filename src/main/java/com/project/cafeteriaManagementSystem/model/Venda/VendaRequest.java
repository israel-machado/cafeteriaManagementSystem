package com.project.cafeteriaManagementSystem.model.Venda;

import com.project.cafeteriaManagementSystem.model.Material.MaterialResponse;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VendaRequest {
    @NotNull(message = "O valor de venda deve ser informado")
    @DecimalMin(value = "0.01", message = "O valor de venda deve ser maior que zero")
    private BigDecimal saleValue;

    @NotNull(message = "O item do cardápio deve ser informado")
    private MenuItemResponse menuItem;

    @NotEmpty(message = "A lista de materiais consumidos não pode estar vazia")
    private List<MaterialResponse> materialsConsumed;
}
