package com.project.cafeteriaManagementSystem.services;

import com.project.cafeteriaManagementSystem.exceptions.InsufficientMaterialStockException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidDataException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidMenuItemDataException;
import com.project.cafeteriaManagementSystem.mapping.MenuItemConverter;
import com.project.cafeteriaManagementSystem.model.Lote.LoteDomain;
import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemDetailedResponse;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemDomain;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemRequest;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemResponse;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
import com.project.cafeteriaManagementSystem.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    //GET BY ID
    public MenuItemResponse getMenuItemById(String id) {
        MenuItemDomain menuItemDomain = menuItemRepository.findById(id)
                .orElseThrow(() -> new InvalidMenuItemDataException("Menu do Cardápio não encontrado pelo ID: " + id));
        return menuItemConverter.convertMenuItemDomainToResponse(menuItemDomain);
    }

    //CREATE
    public MenuItemResponse createMenuItem(MenuItemRequest menuItemRequest) {
        List<MaterialDomain> materialDomainList = new ArrayList<>();

        for (MenuItemRequest.MaterialInfo materialInfo : menuItemRequest.getMaterialsRecipe()) {
            String materialName = materialInfo.getMaterialName();
            double quantity = materialInfo.getQuantity();

            MaterialDomain materialDomain = materialRepository.findByName(materialName);
            if (materialDomain == null) {
                throw new InvalidMaterialDataException("Material não encontrado: " + materialName);
            }

            // Verificar se a quantidade disponível no estoque é suficiente
            if (materialDomain.getQuantity() < quantity) {
                throw new InsufficientMaterialStockException("Estoque insuficiente para o material: " + materialName);
            }

            // Adicionar o material com a quantidade informada à lista de materiais da receita
            materialDomain.setQuantity(quantity);
            materialDomainList.add(materialDomain);
        }

        // Calcular o custo total dos materiais
        BigDecimal totalCost = calculateTotalCost(materialDomainList);

        // Calcular o preço de venda com base no custo total e na margem de lucro informada
        BigDecimal saleValue = totalCost.divide(BigDecimal.ONE.subtract(menuItemRequest.getProfitMargin()), RoundingMode.HALF_UP);

        // Criar o objeto MenuItemDomain com as informações calculadas e a lista de materiais associada
        MenuItemDomain menuItemDomain = MenuItemDomain.builder()
                .name(menuItemRequest.getName())
                .saleValue(saleValue)
                .profitMargin(menuItemRequest.getProfitMargin())
                .materialsRecipe(materialDomainList)
                .build();

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

        // Alterando itens da receita
        List<MaterialDomain> materialDomainList = new ArrayList<>();
        for (MenuItemRequest.MaterialInfo materialInfo : menuItemRequest.getMaterialsRecipe()) {
            String materialName = materialInfo.getMaterialName();
            double quantity = materialInfo.getQuantity();

            MaterialDomain materialDomain = materialRepository.findByName(materialName);
            if (materialDomain != null) {
                materialDomain.setQuantity(quantity);
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
            menuItemRepository.delete(menuItemDomain);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidMenuItemDataException("Erro ao excluir o item do cardápio com o ID: " + id + " - " + e.getMessage());
        }
    }

    // SIMPLIFIED LIST

    public List<MenuItemResponse> getSimplifiedMenuItems() {
        List<MenuItemDomain> menuItemDomainList = menuItemRepository.findAll();
        List<MenuItemResponse> simplifiedMenuItems = new ArrayList<>();

        for (MenuItemDomain menuItem : menuItemDomainList) {
            MenuItemResponse simplifiedMenuItem = menuItemConverter.convertMenuItemDomainToResponse(menuItem);

            boolean hasEnoughQuantity = true;
            for (MaterialDomain material : menuItem.getMaterialsRecipe()) {
                double requiredQuantity = material.getQuantity();

                if(!hasEnoughQuantity(material.getId(), requiredQuantity)) {
                    hasEnoughQuantity = false;
                    break;
                }
            }

            if (hasEnoughQuantity) {
                simplifiedMenuItems.add(simplifiedMenuItem);
            }
        }

        return simplifiedMenuItems;
    }

    private boolean hasEnoughQuantity(String id, double requiredQuantity) {
        MaterialDomain materialDomain = materialRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + id));

        return materialDomain.getQuantity() >= requiredQuantity;
    }

    // DETAILED LIST
    public List<MenuItemDetailedResponse> getAllMenuItemsWithDetails() {
        List<MenuItemDomain> menuItemDomainList = menuItemRepository.findAll();
        List<MenuItemDetailedResponse> detailedResponses = new ArrayList<>();

        for (MenuItemDomain menuItem : menuItemDomainList) {
            MenuItemDetailedResponse detailedResponse = menuItemConverter.convertMenuItemDomainToDetailed(menuItem);

            // Cálculo do preço de custo estimado baseado nos lotes consumidos
            BigDecimal totalCost = calculateTotalCost(menuItem.getMaterialsRecipe());

            detailedResponse.setTotalCost(totalCost);

            detailedResponses.add(detailedResponse);
        }

        return detailedResponses;
    }

    public BigDecimal calculateTotalCost(List<MaterialDomain> materialsRecipe) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (MaterialDomain materialDomain : materialsRecipe) {
            List<LoteDomain>  loteDomainList = materialDomain.getLoteDomainList();
            if (loteDomainList != null && !loteDomainList.isEmpty()) {
                totalCost = totalCost.add(getTotalCostFromLotes(loteDomainList));
            }
        }

        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getTotalCostFromLotes(List<LoteDomain> loteDomainList) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (LoteDomain lote : loteDomainList) {
            totalCost = totalCost.add(lote.getTotalCost());
        }

        return totalCost;
    }
}
