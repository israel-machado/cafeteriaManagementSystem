package com.project.cafeteriaManagementSystem.model.Venda;

import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VendaResponse {

    private String id;
    private BigDecimal saleValue;
    private MenuItemDomain menuItemDomain;
    private List<MaterialDomain> materialsConsumed;
}
