package model.Material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialRequest {

    private String nome;
    private Double quantidade;
    private String unidadeMedida;
    private Date validade;
    private BigDecimal custo;
}
