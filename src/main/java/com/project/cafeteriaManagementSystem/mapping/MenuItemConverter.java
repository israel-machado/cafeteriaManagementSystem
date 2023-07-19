package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemDomain;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemRequest;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MenuItemConverter {

    private final MaterialConverter materialConverter;

    public MenuItemDomain convertMenuItemRequestToDomain(MenuItemRequest menuItemRequest, List<MaterialDomain> materialDomainList, BigDecimal totalCostWithProfit) {
        return MenuItemDomain.builder()
                .name(menuItemRequest.getName())
                .saleValue(totalCostWithProfit)
                .materialsRecipe(materialDomainList)
                .build();
    }

    public MenuItemDomain convertMenuItemResponseToDomain(MenuItemResponse menuItemResponse) {
        return MenuItemDomain.builder()
                .id(menuItemResponse.getId())
                .name(menuItemResponse.getName())
                .saleValue(menuItemResponse.getSaleValue())
                .materialsRecipe(materialConverter.convertMaterialResponseListToDomainList(menuItemResponse.getMaterialsRecipe()))
                .build();
    }

    public MenuItemResponse convertMenuItemDomainToResponse(MenuItemDomain menuItemDomain) {
        return MenuItemResponse.builder()
                .id(menuItemDomain.getId())
                .name(menuItemDomain.getName())
                .saleValue(menuItemDomain.getSaleValue())
                .materialsRecipe(materialConverter.convertMaterialDomainListToResponseList(menuItemDomain.getMaterialsRecipe()))
                .build();
    }

    //LIST

    public List<MenuItemResponse> convertMenuItemDomainListToResponse(List<MenuItemDomain> menuItemDomainList) {
        return menuItemDomainList.stream()
                .map(this::convertMenuItemDomainToResponse)
                .collect(Collectors.toList());
    }
}
