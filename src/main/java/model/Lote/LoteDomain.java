package model.Lote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Material.MaterialDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "lotes")
public class LoteDomain {

    @Id
    private String id;
    private Double amountConsumed;
    private BigDecimal totalCost;
    @DBRef
    private MaterialDomain materialDomain;
}
