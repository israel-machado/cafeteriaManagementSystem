package com.project.cafeteriaManagementSystem.model.material;

import com.project.cafeteriaManagementSystem.model.batch.BatchDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "materials")
public class MaterialDomain {

    @Id
    private String id;
    private String name;
    private String unitMeasure;
    private Double stock;
    private Double minimumStockQuantity;
    private List<BatchDomain> batchDomainList;
}
