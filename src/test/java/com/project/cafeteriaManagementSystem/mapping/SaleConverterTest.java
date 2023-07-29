package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.sale.SaleDomainTest;
import com.project.cafeteriaManagementSystem.model.sale.SaleResponseTest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SaleConverterTest {

    // Método para converter um objeto VendaDomain em uma resposta de venda (VendaResponse)
    public SaleResponseTest convertSaleDomainToResponse(SaleDomainTest saleDomainTest) {
        return SaleResponseTest.builder()
                .id(saleDomainTest.getId())
                .dateOfSale(saleDomainTest.getDateOfSale())
                .salePrice(saleDomainTest.getSalePrice())
                .saleCost(saleDomainTest.getSaleCost())
                .profitValue(saleDomainTest.getProfitValue())
                .profitMargin(saleDomainTest.getProfitMargin())
                .saleItemTests(saleDomainTest.getSaleItemTests())
                .build();
    }

    // Utiliza o Stream API do Java para mapear cada objeto VendaDomain para um objeto VendaResponse
    // e coleta os resultados em uma lista usando Collectors.toList()
    public List<SaleResponseTest> convertSaleDomainListToSaleResponseList(List<SaleDomainTest> saleDomainTestList) {
        return saleDomainTestList.stream()
                .map(this::convertSaleDomainToResponse)
                .collect(Collectors.toList());
    }
}
