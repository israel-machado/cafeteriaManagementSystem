package com.project.cafeteriaManagementSystem.model.sale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaleResponseTest {

    private String id;
    private LocalDateTime dateOfSale;
    private BigDecimal salePrice;
    private BigDecimal saleCost;
    private BigDecimal profitValue;
    private BigDecimal profitMargin;
    private List<SaleItemTest> saleItemTests;
}
