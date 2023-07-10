package com.project.cafeteriaManagementSystem.model.Lote;

import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "lotes")
public class LoteDomain {

    @Id
    private String id;
    private Double amountToBeConsumed;
    private BigDecimal totalCost;
    private LocalDate validity;
    @DBRef
    private MaterialDomain materialDomain;
}
