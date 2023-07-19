package com.project.cafeteriaManagementSystem.model.Venda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemDomain;
import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "vendas")
public class VendaDomain {

    @Id
    private String id;
    private BigDecimal saleValue;

    @DBRef
    private MenuItemDomain menuItemDomain;

    @DBRef
    private List<MaterialDomain> materialsConsumed;
}
