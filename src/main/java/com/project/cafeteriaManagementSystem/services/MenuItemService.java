package com.project.cafeteriaManagementSystem.services;

import com.project.cafeteriaManagementSystem.exceptions.InvalidDataException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidMenuItemDataException;
import com.project.cafeteriaManagementSystem.mapping.MenuItemConverter;
import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemDomain;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemRequest;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemResponse;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
import com.project.cafeteriaManagementSystem.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MaterialRepository materialRepository;
    private final MenuItemConverter menuItemConverter;

    //GET ALL
    public List<MenuItemResponse> getAllMenu() {
        List<MenuItemDomain> menuItemDomainList = menuItemRepository.findAll();
        return menuItemConverter.convertMenuItemDomainListToResponse(menuItemDomainList);
    }

    //CREATE
    public MenuItemResponse createMenuItem(MenuItemRequest menuItemRequest) {
        List<MaterialDomain> materialDomainList = new ArrayList<>();

        for (String materialName : menuItemRequest.getMaterialsRecipeNames()) {
            MaterialDomain materialDomain = materialRepository.findByName(materialName);
            if (materialDomain != null) {
                materialDomainList.add(materialDomain);
            } else {
                throw new InvalidMaterialDataException("Material não encontrado: " + materialName);
            }
        }

        MenuItemDomain menuItemDomain = menuItemConverter.convertMenuItemRequestToDomain(menuItemRequest, materialDomainList);

        menuItemRepository.save(menuItemDomain);

        return menuItemConverter.convertMenuItemDomainToResponse(menuItemDomain);
    }

    //UPDATE
    public MenuItemResponse updateMenuItem(String id, MenuItemRequest menuItemRequest) {
        MenuItemDomain existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Item do cardápio não encontrado pelo ID: " + id));

        // Alterando nome
        existingMenuItem.setName(menuItemRequest.getName());
        // Alterando o valor
        existingMenuItem.setSaleValue(menuItemRequest.getSaleValue());

        //Alterando itens da receita
        List<MaterialDomain> materialDomainList = new ArrayList<>();
        for (String materialName : menuItemRequest.getMaterialsRecipeNames()) {
            MaterialDomain materialDomain = materialRepository.findByName(materialName);
            if (materialDomain != null) {
                materialDomainList.add(materialDomain);
            } else {
                throw new InvalidMaterialDataException("Material não encontrado: " + materialName);
            }
        }
        existingMenuItem.setMaterialsRecipe(materialDomainList);

        menuItemRepository.save(existingMenuItem);

        return  menuItemConverter.convertMenuItemDomainToResponse(existingMenuItem);
    }

    //DELETE
    public void deleteMenuItem(String id) {
        MenuItemDomain menuItemDomain = menuItemRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Item do cardápio não encontrado pelo ID: " + id));

        try {
            menuItemRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidMenuItemDataException("Erro ao excluir o item do cardápio com o ID: " + id + " - " + e.getMessage());
        }
    }
}
