package com.project.cafeteriaManagementSystem.model.batch;

import com.project.cafeteriaManagementSystem.model.material.MaterialDomainTest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "batches")
public class BatchDomainTest implements Comparable<BatchDomainTest> {

    @Id
    private String id;
    private Double initialAmount;
    private BigDecimal totalCost;
    private LocalDateTime validity;
    private LocalDateTime dateOfPurchase;
    private String supplierName;
    private Double remainingAmount;
    private Double wasteAmount;
    private MaterialDomainTest materialDomainTest;

    // Implementação do método compareTo() para comparar por validade
    @Override
    public int compareTo(BatchDomainTest otherBatch) {
        return this.validity.compareTo(otherBatch.validity);
    }
}
