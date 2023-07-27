package com.project.cafeteriaManagementSystem.model.sale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "sales")
public class SaleDomain {

    @Id
    private String id;
    private LocalDateTime dateOfSale;
    private BigDecimal salePrice;
    private BigDecimal saleCost;
    private BigDecimal profitValue;
    private BigDecimal profitMargin;
    private List<SaleItem> saleItems;
}
