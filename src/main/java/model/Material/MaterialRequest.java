package model.Material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Lote.LoteDomain;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialRequest {

    private String name;
    private Double quantity;
    private String unitMeasure;
    private Date validity;
    private BigDecimal cost;
    private LoteDomain loteDomain;
}
