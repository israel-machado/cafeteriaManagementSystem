package com.project.cafeteriaManagementSystem.model.menuItem;

import com.project.cafeteriaManagementSystem.model.material.MaterialInfoTest;
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
public class MenuItemDetailedResponseTest {

    private String id;
    private String name;
    private BigDecimal salePrice;
    private List<MaterialInfoTest> materialsRecipe;
    private BigDecimal totalCost;
}
