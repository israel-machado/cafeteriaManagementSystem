package model.Material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Lote.LoteRequest;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialRequest {

    private String name;
    private Double quantity;
    private String unitMeasure;
    private BigDecimal cost;
    private List<LoteRequest> loteRequestList;
}
