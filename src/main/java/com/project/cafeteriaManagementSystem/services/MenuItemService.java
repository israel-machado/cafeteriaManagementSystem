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

    // Método para atualizar um item no cardápio
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
            for (MaterialDomain material : menuItem.getMaterialsRecipe()) {
                double requiredQuantity = material.getQuantity();

                if(!hasEnoughQuantity(material.getId(), requiredQuantity)) {
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
    private boolean hasEnoughQuantity(String id, double requiredQuantity) {
        MaterialDomain materialDomain = materialRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + id));

        // Verifica se a quantidade em estoque do material é maior ou igual à quantidade requerida
        return materialDomain.getQuantity() >= requiredQuantity;
    }

    // Método para obter uma lista detalhada de itens do cardápio com informações adicionais, como custo estimado
    public List<MenuItemDetailedResponse> getAllMenuItemsWithDetails() {
        // Obtém a lista de todos os itens do cardápio do banco de dados
        List<MenuItemDomain> menuItemDomainList = menuItemRepository.findAll();
        List<MenuItemDetailedResponse> detailedResponses = new ArrayList<>();

        for (MenuItemDomain menuItem : menuItemDomainList) {
            // Converte o objeto MenuItemDomain para uma resposta detalhada (MenuItemDetailedResponse)
            MenuItemDetailedResponse detailedResponse = menuItemConverter.convertMenuItemDomainToDetailed(menuItem);

            // Cálculo do preço de custo estimado baseado nos lotes consumidos
            BigDecimal totalCost = calculateTotalCost(menuItem.getMaterialsRecipe());

            // Define o preço de custo estimado no objeto de resposta
            detailedResponse.setTotalCost(totalCost);

            // Adiciona o item detalhado à lista de respostas
            detailedResponses.add(detailedResponse);
        }

        return detailedResponses;
    }

    // Método para calcular o custo total dos materiais necessários para um item do cardápio
    public BigDecimal calculateTotalCost(List<MaterialDomain> materialsRecipe) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (MaterialDomain materialDomain : materialsRecipe) {
            // Obtém a lista de lotes associados a este material
            List<LoteDomain>  loteDomainList = materialDomain.getLoteDomainList();
            if (loteDomainList != null && !loteDomainList.isEmpty()) {
                // Soma os custos totais de todos os lotes deste material
                totalCost = totalCost.add(getTotalCostFromLotes(loteDomainList));
            }
        }

        // Arredonda o custo total para duas casas decimais e retorna
        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    // Método auxiliar para calcular o custo total de uma lista de lotes
    private BigDecimal getTotalCostFromLotes(List<LoteDomain> loteDomainList) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (LoteDomain lote : loteDomainList) {
            // Soma os custos totais de todos os lotes
            totalCost = totalCost.add(lote.getTotalCost());
        }

        // Retorna o custo total somado de todos os lotes
        return totalCost;
    }
}
