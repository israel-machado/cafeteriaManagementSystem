package model.Material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "materiais")
public class MaterialDomain {

    @Id
    private String id;
    private String name;
    private Double quantity;
    private String unitMeasure;
    private Date validity;
    private BigDecimal cost;
}
