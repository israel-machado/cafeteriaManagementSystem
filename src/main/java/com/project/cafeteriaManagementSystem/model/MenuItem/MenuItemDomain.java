package com.project.cafeteriaManagementSystem.model.MenuItem;

import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "itensCardapio")
public class MenuItemDomain {

    @Id
    private String id;
    private String name;
    private BigDecimal saleValue;
    private BigDecimal profitMargin;
    @DBRef
    private List<MaterialDomain> materialsRecipe;
}
