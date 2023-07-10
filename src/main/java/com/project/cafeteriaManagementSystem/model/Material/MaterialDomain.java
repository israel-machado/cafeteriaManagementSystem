package com.project.cafeteriaManagementSystem.model.Material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.project.cafeteriaManagementSystem.model.Lote.LoteDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

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
    private BigDecimal cost;
    @DBRef
    private List<LoteDomain> loteDomainList;
}
