package model.Lote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Material.MaterialDomain;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoteRequest {

    private Double amountConsumed;
    private MaterialDomain materialDomain;
}
