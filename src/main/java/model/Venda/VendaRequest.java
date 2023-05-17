package model.Venda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.MenuItem.MenuItemDomain;
import model.Material.MaterialDomain;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VendaRequest {

    private BigDecimal saleValue;
    private MenuItemDomain menuItemDomain;
    private List<MaterialDomain> materialsConsumed;
}
