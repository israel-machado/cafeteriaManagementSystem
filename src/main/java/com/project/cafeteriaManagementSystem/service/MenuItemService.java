package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.exception.InvalidDataException;
import com.project.cafeteriaManagementSystem.exception.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.exception.InvalidMenuItemDataException;
import com.project.cafeteriaManagementSystem.mapping.MenuItemConverter;
import com.project.cafeteriaManagementSystem.model.batch.BatchDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialInfo;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDetailedResponse;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDomain;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemRequest;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemResponse;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
import com.project.cafeteriaManagementSystem.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MaterialRepository materialRepository;
    private final MenuItemConverter menuItemConverter;
    private final BatchService batchService;

    // Método para obter todos os itens do cardápio
    public List<MenuItemResponse> getAllMenu() {
        List<MenuItemDomain> menuItemDomainList = menuItemRepository.findAll();
        return menuItemConverter.convertMenuItemDomainListToResponse(menuItemDomainList);
    }

    // Método para obter um item do cardápio pelo ID
    public MenuItemResponse getMenuItemById(String id) {
        MenuItemDomain menuItemDomain = menuItemRepository.findById(id)
                .orElseThrow(() -> new InvalidMenuItemDataException("Menu do Cardápio não encontrado pelo ID: " + id));
        return menuItemConverter.convertMenuItemDomainToResponse(menuItemDomain);
    }

    // Método para criar um novo item no cardápio
    public MenuItemResponse createMenuItem(MenuItemRequest menuItemRequest) {

        // Criar o objeto MenuItemDomain com as informações calculadas e a lista de materiais associada
        MenuItemDomain menuItemDomain = MenuItemDomain.builder()
                .name(menuItemRequest.getName())
                .salePrice(menuItemRequest.getSalePrice())
                .materialsRecipe(menuItemRequest.getMaterialsRecipe())
                .build();

        menuItemRepository.save(menuItemDomain);

        return menuItemConverter.convertMenuItemDomainToResponse(menuItemDomain);
    }

    // Método para atualizar um item no cardápio
    public MenuItemResponse updateMenuItem(String id, MenuItemRequest menuItemRequest) {
        MenuItemDomain existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Item do cardápio não encontrado pelo ID: " + id));

        // Alterando nome
        existingMenuItem.setName(menuItemRequest.getName());
        // Alterando o valor
        existingMenuItem.setSalePrice(menuItemRequest.getSalePrice());

        // Alterando itens da receita
        List<MaterialInfo> materialInfoList = new ArrayList<>();

        for (MaterialInfo materialInfo : menuItemRequest.getMaterialsRecipe()) {
            MaterialInfo newMaterialInfo = null;
            newMaterialInfo.setMaterialName(materialInfo.getMaterialName());
            newMaterialInfo.setQuantity(materialInfo.getQuantity());
            newMaterialInfo.setUnitMeasure(materialInfo.getUnitMeasure());
            materialInfoList.add(materialInfo);
        }
        existingMenuItem.setMaterialsRecipe(materialInfoList);

        // Salvando o menuItem com as alterações
        menuItemRepository.save(existingMenuItem);

        return  menuItemConverter.convertMenuItemDomainToResponse(existingMenuItem);
    }

    // Método para excluir um item do cardápio pelo ID
    public void deleteMenuItem(String id) {
        MenuItemDomain menuItemDomain = menuItemRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Item do cardápio não encontrado pelo ID: " + id));

        try {
            // Tenta excluir o item do cardápio usando o repositório
            menuItemRepository.delete(menuItemDomain);
        } catch (DataIntegrityViolationException e) {
            // Se houver alguma violação de integridade (por exemplo, relacionamentos com outras entidades), lance uma exceção personalizada
            throw new InvalidMenuItemDataException("Erro ao excluir o item do cardápio com o ID: " + id + " - " + e.getMessage());
        }
    }

    // Método para obter uma lista simplificada de itens do cardápio que possuem materiais suficientes em estoque
    public List<MenuItemResponse> getSimplifiedMenuItems() {
        // Obtém a lista de todos os itens do cardápio do banco de dados
        List<MenuItemDomain> menuItemDomainList = menuItemRepository.findAll();
        List<MenuItemResponse> simplifiedMenuItems = new ArrayList<>();

        for (MenuItemDomain menuItem : menuItemDomainList) {
            // Converte o objeto MenuItemDomain para uma resposta simplificada (MenuItemResponse)
            MenuItemResponse simplifiedMenuItem = menuItemConverter.convertMenuItemDomainToResponse(menuItem);

            // Verifica se todos os materiais necessários para este item do cardápio têm quantidade suficiente em estoque
            boolean hasEnoughQuantity = true;

            for (MaterialInfo material : menuItem.getMaterialsRecipe()) {
                double requiredQuantity = material.getQuantity();

                if(!hasEnoughQuantity(material.getMaterialName(), requiredQuantity)) {
                    // Se algum material não tiver quantidade suficiente em estoque, seta a flag para falso e interrompe o loop
                    hasEnoughQuantity = false;
                    break;
                }
            }

            // Se todos os materiais têm quantidade suficiente em estoque, adiciona o item à lista de resposta
            if (hasEnoughQuantity) {
                simplifiedMenuItems.add(simplifiedMenuItem);
            }
        }

        return simplifiedMenuItems;
    }

    // Método auxiliar para verificar se um material tem quantidade suficiente em estoque
    private boolean hasEnoughQuantity(String name, double requiredQuantity) {
        MaterialDomain materialDomain = materialRepository.findByName(name);
        if (materialDomain == null) {
            throw new InvalidDataException("Material não encontrado pelo nome: " + name);
        }

        // Verifica se a quantidade em estoque do material é maior ou igual à quantidade requerida
        return materialDomain.getStock() >= requiredQuantity;
    }

    // Método para obter uma lista detalhada de itens do cardápio com informações adicionais, como custo estimado
    public List<MenuItemDetailedResponse> getAllMenuItemsWithDetails() {
        // Obtém a lista de todos os itens do cardápio do banco de dados
        List<MenuItemDomain> menuItemDomainList = menuItemRepository.findAll();
        List<MenuItemDetailedResponse> detailedResponses = new ArrayList<>();

        for (MenuItemDomain menuItem : menuItemDomainList) {

            // Cálculo do preço de custo estimado baseado nos lotes consumidos
            BigDecimal totalCost = calculateMenuItemTotalCost(menuItem.getMaterialsRecipe());

            // Converte o objeto MenuItemDomain para uma resposta detalhada (MenuItemDetailedResponse)
            MenuItemDetailedResponse detailedResponse = menuItemConverter.convertMenuItemDomainToDetailed(menuItem, totalCost);

            // Adiciona o item detalhado à lista de respostas
            detailedResponses.add(detailedResponse);
        }

        return detailedResponses;
    }

    // Método para calcular o custo total dos materiais necessários para um item do cardápio
    public BigDecimal calculateMenuItemTotalCost(List<MaterialInfo> materialsRecipe) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (MaterialInfo materialInfo : materialsRecipe) {
            // Declara a quantidade que será consumida deste material
            double amountToBeConsumed = materialInfo.getQuantity();
            // Obtém o material pelo nome do MaterialInfo atual
            MaterialDomain materialDomain = materialRepository.findByName(materialInfo.getMaterialName());
            if (materialDomain == null) {
                throw new InvalidMaterialDataException("Não foi possível achar o material pelo nome :" + materialInfo.getMaterialName());
            }
            // Obtém a lista de lotes do material que foi encontrado através do MaterialInfo atual
            List<BatchDomain> batchDomainList = materialDomain.getBatchDomainList();
            // Ordena a lista de lotes por validade
            Collections.sort(batchDomainList);

            // Calcula e consome do lote mais próximo a vencer
            BigDecimal costForQuantity = batchService.calculateCostForQuantityFromBatch(materialDomain, amountToBeConsumed);
            totalCost = totalCost.add(costForQuantity);
        }

        // Arredonda o custo total para duas casas decimais e retorna
        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    // Método auxiliar para calcular o custo total de uma lista de lotes
    private BigDecimal getTotalCostFromBatches(List<BatchDomain> batchDomainList) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (BatchDomain lote : batchDomainList) {
            // Soma os custos totais de todos os lotes
            totalCost = totalCost.add(lote.getTotalCost());
        }

        // Retorna o custo total somado de todos os lotes
        return totalCost;
    }
}
