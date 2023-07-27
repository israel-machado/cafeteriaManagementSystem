package com.project.cafeteriaManagementSystem.model.menuItem;

import com.project.cafeteriaManagementSystem.model.material.MaterialInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "menuItems")
public class MenuItemDomain {

    @Id
    private String id;
    private String name;
    private BigDecimal salePrice;
    private List<MaterialInfo> materialsRecipe;
}
