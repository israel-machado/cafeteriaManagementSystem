package model.Lote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Material.MaterialDomain;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoteRequest {

    private Double amountConsumed;
    private BigDecimal totalCost;
    private Date validity;
    private MaterialDomain materialDomain;
}
