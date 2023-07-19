package com.project.cafeteriaManagementSystem.model.MenuItem;

import com.project.cafeteriaManagementSystem.model.Material.MaterialResponse;
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
public class MenuItemResponse {

    private String id;
    private String name;
    private BigDecimal saleValue;
    private BigDecimal profitMargin;
    private List<MaterialResponse> materialsRecipe;
}
