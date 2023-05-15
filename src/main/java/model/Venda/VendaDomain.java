package model.Venda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.ItemCardapio.ItemCardapioDomain;
import model.Material.MaterialDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "vendas")
public class VendaDomain {

    @Id
    private String id;
    private Double valorVenda;
    @DBRef
    private ItemCardapioDomain itemCardapioDomain;
    @DBRef
    private List<MaterialDomain> materiaisConsumidos;
}
