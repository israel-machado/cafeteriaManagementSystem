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
public class MaterialResponse {

    private String id;
    private String name;
    private Double quantity;
    private String unitMeasure;
    private Date validity;
    private BigDecimal cost;
}
