package com.project.cafeteriaManagementSystem.model.material;

import com.project.cafeteriaManagementSystem.model.batch.BatchDomain;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialResponse {

    private String id;
    private String name;
    private String unitMeasure;
    private Double stock;
    private Double minimumStockQuantity;
    private List<BatchDomain> batchDomainList;
}
