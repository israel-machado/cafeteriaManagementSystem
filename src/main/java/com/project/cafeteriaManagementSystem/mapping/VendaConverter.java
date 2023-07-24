package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.Venda.VendaDomain;
import com.project.cafeteriaManagementSystem.model.Venda.VendaRequest;
import com.project.cafeteriaManagementSystem.model.Venda.VendaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VendaConverter {

    private final MenuItemConverter menuItemConverter;
    private final MaterialConverter materialConverter;

    // Método para converter um objeto VendaDomain em uma resposta de venda (VendaResponse)
    public VendaResponse convertVendaDomainToResponse(VendaDomain vendaDomain) {
        return VendaResponse.builder()
                .id(vendaDomain.getId())
                .saleValue(vendaDomain.getSaleValue())
                .menuItem(menuItemConverter.convertMenuItemDomainToResponse(vendaDomain.getMenuItemDomain()))
                .materialsConsumed(materialConverter.convertMaterialDomainListToResponseList(vendaDomain.getMaterialsConsumed()))
                .build();
    }

    // Método para converter uma requisição de venda (VendaRequest) em um objeto VendaDomain
    public VendaDomain convertVendaRequestToDomain(VendaRequest vendaRequest) {
        return VendaDomain.builder()
                .saleValue(vendaRequest.getSaleValue())
                .menuItemDomain(menuItemConverter.convertMenuItemResponseToDomain(vendaRequest.getMenuItem()))
                .materialsConsumed(materialConverter.convertMaterialResponseListToDomainList(vendaRequest.getMaterialsConsumed()))
                .build();
    }

    // Utiliza o Stream API do Java para mapear cada objeto VendaDomain para um objeto VendaResponse
    // e coleta os resultados em uma lista usando Collectors.toList()
    public List<VendaResponse> convertVendaDomainListToVendaResponseList(List<VendaDomain> vendaDomainList) {
        return vendaDomainList.stream()
                .map(this::convertVendaDomainToResponse)
                .collect(Collectors.toList());
    }
}
