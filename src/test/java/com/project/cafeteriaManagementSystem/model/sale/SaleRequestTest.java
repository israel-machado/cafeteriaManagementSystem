package com.project.cafeteriaManagementSystem.model.sale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaleRequestTest {

    private LocalDateTime dateOfSale;
    private List<String> menuItemId;
}
