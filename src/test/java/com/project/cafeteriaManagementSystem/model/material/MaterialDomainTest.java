package com.project.cafeteriaManagementSystem.model.material;

import com.project.cafeteriaManagementSystem.model.batch.BatchDomainTest;
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
public class MaterialDomainTest {

    @Id
    private String id;
    private String name;
    private String unitMeasure; //TODO deixar stock apenas no Response / Refazer métodos que envolvem stock no BatchService
    private Double minimumStockQuantity;
    private List<BatchDomainTest> batchDomainTestList;
}
