package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.sale.SaleDomain;
import com.project.cafeteriaManagementSystem.model.sale.SaleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SaleConverter {

    // Método para converter um objeto VendaDomain em uma resposta de venda (VendaResponse)
    public SaleResponse convertSaleDomainToResponse(SaleDomain saleDomain) {
        return SaleResponse.builder()
                .id(saleDomain.getId())
                .dateOfSale(saleDomain.getDateOfSale())
                .salePrice(saleDomain.getSalePrice())
                .saleCost(saleDomain.getSaleCost())
                .profitValue(saleDomain.getProfitValue())
                .profitMargin(saleDomain.getProfitMargin())
                .saleItems(saleDomain.getSaleItems())
                .build();
    }

    // Utiliza o Stream API do Java para mapear cada objeto VendaDomain para um objeto VendaResponse
    // e coleta os resultados em uma lista usando Collectors.toList()
    public List<SaleResponse> convertSaleDomainListToSaleResponseList(List<SaleDomain> saleDomainList) {
        return saleDomainList.stream()
                .map(this::convertSaleDomainToResponse)
                .collect(Collectors.toList());
    }
}
