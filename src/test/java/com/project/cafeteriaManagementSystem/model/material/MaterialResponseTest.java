package com.project.cafeteriaManagementSystem.model.material;

import com.project.cafeteriaManagementSystem.model.batch.BatchResponseTest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialResponseTest {

    private String id;
    private String name;
    private String unitMeasure;
    private Double stock;
    private Double minimumStockQuantity;
    private List<BatchResponseTest> batchResponsesListTest;
}
