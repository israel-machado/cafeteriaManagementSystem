package model.ItemCardapio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Material.MaterialDomain;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemCardapioRequest {

    private String nome;
    private BigDecimal valorVenda;
    private List<MaterialDomain> materiaisConsumidos;
}
