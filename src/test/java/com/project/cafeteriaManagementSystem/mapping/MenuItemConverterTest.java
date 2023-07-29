package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDetailedResponseTest;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDomainTest;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemResponseTest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MenuItemConverterTest {

    // Método para converter um objeto de domínio MenuItemDomain em uma resposta de MenuItem (MenuItemResponse)
    public MenuItemResponseTest convertMenuItemDomainToResponse(MenuItemDomainTest menuItemDomainTest) {
        return MenuItemResponseTest.builder()
                .id(menuItemDomainTest.getId())
                .name(menuItemDomainTest.getName())
                .salePrice(menuItemDomainTest.getSalePrice())
                .materialsRecipe(menuItemDomainTest.getMaterialsRecipe())
                .build();
    }

    // Método para converter um objeto de domínio MenuItemDomain em uma resposta detalhada de MenuItem (MenuItemDetailedResponse)
    public MenuItemDetailedResponseTest convertMenuItemDomainToDetailed(MenuItemDomainTest menuItemDomainTest, BigDecimal totalCost) {
        return MenuItemDetailedResponseTest.builder()
                .id(menuItemDomainTest.getId())
                .name(menuItemDomainTest.getName())
                .salePrice(menuItemDomainTest.getSalePrice())
                .materialsRecipe(menuItemDomainTest.getMaterialsRecipe())
                .totalCost(totalCost)
                .build();
    }

    // Utiliza o Stream API do Java para mapear cada objeto MenuItemDomain para um objeto MenuItemResponse
    // e coleta os resultados em uma lista usando Collectors.toList()
    public List<MenuItemResponseTest> convertMenuItemDomainListToResponse(List<MenuItemDomainTest> menuItemDomainTestList) {
        return menuItemDomainTestList.stream()
                .map(this::convertMenuItemDomainToResponse)
                .collect(Collectors.toList());
    }
}
