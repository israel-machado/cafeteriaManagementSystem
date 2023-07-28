package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDetailedResponse;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDomain;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MenuItemConverter {

    // Método para converter um objeto de domínio MenuItemDomain em uma resposta de MenuItem (MenuItemResponse)
    public MenuItemResponse convertMenuItemDomainToResponse(MenuItemDomain menuItemDomain) {
        return MenuItemResponse.builder()
                .id(menuItemDomain.getId())
                .name(menuItemDomain.getName())
                .salePrice(menuItemDomain.getSalePrice())
                .materialsRecipe(menuItemDomain.getMaterialsRecipe())
                .build();
    }

    // Método para converter um objeto de domínio MenuItemDomain em uma resposta detalhada de MenuItem (MenuItemDetailedResponse)
    public MenuItemDetailedResponse convertMenuItemDomainToDetailed(MenuItemDomain menuItemDomain, BigDecimal totalCost) {
        return MenuItemDetailedResponse.builder()
                .id(menuItemDomain.getId())
                .name(menuItemDomain.getName())
                .salePrice(menuItemDomain.getSalePrice())
                .materialsRecipe(menuItemDomain.getMaterialsRecipe())
                .totalCost(totalCost)
                .build();
    }

    // Utiliza o Stream API do Java para mapear cada objeto MenuItemDomain para um objeto MenuItemResponse
    // e coleta os resultados em uma lista usando Collectors.toList()
    public List<MenuItemResponse> convertMenuItemDomainListToResponse(List<MenuItemDomain> menuItemDomainList) {
        return menuItemDomainList.stream()
                .map(this::convertMenuItemDomainToResponse)
                .collect(Collectors.toList());
    }
}
