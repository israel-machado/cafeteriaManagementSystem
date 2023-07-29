package com.project.cafeteriaManagementSystem.model.sale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaleItemTest {

    private String name;
    private BigDecimal salePrice;
}
