package com.project.cafeteriaManagementSystem.model.menuItem;

import com.project.cafeteriaManagementSystem.model.material.MaterialInfo;
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
public class MenuItemRequest {

    private String name;
    private BigDecimal salePrice;
    private List<MaterialInfo> materialsRecipe;
}
