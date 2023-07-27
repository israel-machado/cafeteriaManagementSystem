package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.sale.SaleDomain;
import com.project.cafeteriaManagementSystem.model.sale.SaleRequest;
import com.project.cafeteriaManagementSystem.model.sale.SaleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SaleConverter {

    private final MenuItemConverter menuItemConverter;
    private final MaterialConverter materialConverter;

    // Método para converter um objeto VendaDomain em uma resposta de venda (VendaResponse)
    public SaleResponse convertVendaDomainToResponse(SaleDomain saleDomain) {
        return SaleResponse.builder()
                .id(saleDomain.getId())
                .saleValue(saleDomain.getSaleValue())
                .menuItem(menuItemConverter.convertMenuItemDomainToResponse(saleDomain.getMenuItemDomain()))
                .materialsConsumed(materialConverter.convertMaterialDomainListToResponseList(saleDomain.getMaterialsConsumed()))
                .build();
    }

    // Método para converter uma requisição de venda (VendaRequest) em um objeto VendaDomain
    public SaleDomain convertVendaRequestToDomain(SaleRequest saleRequest) {
        return SaleDomain.builder()
                .saleValue(saleRequest.getSaleValue())
                .menuItemDomain(menuItemConverter.convertMenuItemResponseToDomain(saleRequest.getMenuItem()))
                .materialsConsumed(materialConverter.convertMaterialResponseListToDomainList(saleRequest.getMaterialsConsumed()))
                .build();
    }

    // Utiliza o Stream API do Java para mapear cada objeto VendaDomain para um objeto VendaResponse
    // e coleta os resultados em uma lista usando Collectors.toList()
    public List<SaleResponse> convertVendaDomainListToVendaResponseList(List<SaleDomain> saleDomainList) {
        return saleDomainList.stream()
                .map(this::convertVendaDomainToResponse)
                .collect(Collectors.toList());
    }
}
