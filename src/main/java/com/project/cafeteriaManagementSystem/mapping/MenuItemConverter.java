package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemDomain;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemRequest;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuItemConverter {

    public MenuItemDomain convertMenuItemRequestToDomain(MenuItemRequest menuItemRequest, List<MaterialDomain> materialDomainList) {
        return MenuItemDomain.builder()
                .name(menuItemRequest.getName())
                .saleValue(menuItemRequest.getSaleValue())
                .materialsRecipe(materialDomainList)
                .build();
    }

    public MenuItemResponse convertMenuItemDomainToResponse(MenuItemDomain menuItemDomain) {
        return MenuItemResponse.builder()
                .id(menuItemDomain.getId())
                .name(menuItemDomain.getName())
                .saleValue(menuItemDomain.getSaleValue())
                .materialsRecipe(menuItemDomain.getMaterialsRecipe())
                .build();
    }
}
