package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDetailedResponse;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDomain;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MenuItemConverter {

    private final MaterialConverter materialConverter;

    // Método para converter uma resposta de MenuItem (MenuItemResponse) em um objeto de domínio MenuItemDomain
    public MenuItemDomain convertMenuItemResponseToDomain(MenuItemResponse menuItemResponse) {
        return MenuItemDomain.builder()

                .build();
    }

    // Método para converter um objeto de domínio MenuItemDomain em uma resposta de MenuItem (MenuItemResponse)
    public MenuItemResponse convertMenuItemDomainToResponse(MenuItemDomain menuItemDomain) {
        return MenuItemResponse.builder()

                .build();
    }

    // Método para converter um objeto de domínio MenuItemDomain em uma resposta detalhada de MenuItem (MenuItemDetailedResponse)
    public MenuItemDetailedResponse convertMenuItemDomainToDetailed(MenuItemDomain menuItemDomain) {
        return MenuItemDetailedResponse.builder()

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
