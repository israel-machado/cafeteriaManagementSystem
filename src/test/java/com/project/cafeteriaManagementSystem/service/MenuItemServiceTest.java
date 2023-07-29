package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.exception.InvalidDataException;
import com.project.cafeteriaManagementSystem.exception.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.exception.InvalidMenuItemDataException;
import com.project.cafeteriaManagementSystem.mapping.MenuItemConverterTest;
import com.project.cafeteriaManagementSystem.model.batch.BatchDomainTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomainTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialInfoTest;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDetailedResponseTest;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDomainTest;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemRequestTest;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemResponseTest;
import com.project.cafeteriaManagementSystem.repository.MaterialRepositoryTest;
import com.project.cafeteriaManagementSystem.repository.MenuItemRepositoryTest;
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
public class MenuItemServiceTest {

    private final MenuItemRepositoryTest menuItemRepositoryTest;
    private final MaterialRepositoryTest materialRepositoryTest;
    private final MaterialServiceTest materialServiceTest;
    private final MenuItemConverterTest menuItemConverterTest;
    private final BatchServiceTest batchServiceTest;

    // Método para obter todos os itens do cardápio
    public List<MenuItemResponseTest> getAllMenu() {
        List<MenuItemDomainTest> menuItemDomainTestList = menuItemRepositoryTest.findAll();
        return menuItemConverterTest.convertMenuItemDomainListToResponse(menuItemDomainTestList);
    }

    // Método para obter um item do cardápio pelo ID
    public MenuItemResponseTest getMenuItemById(String id) {
        MenuItemDomainTest menuItemDomainTest = menuItemRepositoryTest.findById(id)
                .orElseThrow(() -> new InvalidMenuItemDataException("Menu do Cardápio não encontrado pelo ID: " + id));
        return menuItemConverterTest.convertMenuItemDomainToResponse(menuItemDomainTest);
    }

    // Método para criar um novo item no cardápio
    public MenuItemResponseTest createMenuItem(MenuItemRequestTest menuItemRequestTest) {

        // Criar o objeto MenuItemDomain com as informações calculadas e a lista de materiais associada
        MenuItemDomainTest menuItemDomainTest = MenuItemDomainTest.builder()
                .name(menuItemRequestTest.getName())
                .salePrice(menuItemRequestTest.getSalePrice())
                .materialsRecipe(menuItemRequestTest.getMaterialsRecipe())
                .build();

        menuItemRepositoryTest.save(menuItemDomainTest);

        return menuItemConverterTest.convertMenuItemDomainToResponse(menuItemDomainTest);
    }

    // Método para atualizar um item no cardápio
    public MenuItemResponseTest updateMenuItem(String id, MenuItemRequestTest menuItemRequestTest) {
        MenuItemDomainTest existingMenuItem = menuItemRepositoryTest.findById(id)
                .orElseThrow(() -> new InvalidDataException("Item do cardápio não encontrado pelo ID: " + id));

        // Alterando nome
        existingMenuItem.setName(menuItemRequestTest.getName());
        // Alterando o valor
        existingMenuItem.setSalePrice(menuItemRequestTest.getSalePrice());

        // Inicia uma lista nova de material info para alterar itens da receita
        List<MaterialInfoTest> materialInfoTestList = new ArrayList<>();

        for (MaterialInfoTest materialInfoTest : menuItemRequestTest.getMaterialsRecipe()) {
            MaterialInfoTest newMaterialInfoTest = new MaterialInfoTest();
            newMaterialInfoTest.setMaterialName(materialInfoTest.getMaterialName());
            newMaterialInfoTest.setQuantity(materialInfoTest.getQuantity());
            newMaterialInfoTest.setUnitMeasure(materialInfoTest.getUnitMeasure());
            materialInfoTestList.add(materialInfoTest);
        }
        existingMenuItem.setMaterialsRecipe(materialInfoTestList);

        // Salvando o menuItem com as alterações
        menuItemRepositoryTest.save(existingMenuItem);

        return  menuItemConverterTest.convertMenuItemDomainToResponse(existingMenuItem);
    }

    // Método para excluir um item do cardápio pelo ID
    public void deleteMenuItem(String id) {
        MenuItemDomainTest menuItemDomainTest = menuItemRepositoryTest.findById(id)
                .orElseThrow(() -> new InvalidDataException("Item do cardápio não encontrado pelo ID: " + id));

        try {
            // Tenta excluir o item do cardápio usando o repositório
            menuItemRepositoryTest.delete(menuItemDomainTest);
        } catch (DataIntegrityViolationException e) {
            // Se houver alguma violação de integridade (por exemplo, relacionamentos com outras entidades), lance uma exceção personalizada
            throw new InvalidMenuItemDataException("Erro ao excluir o item do cardápio com o ID: " + id + " - " + e.getMessage());
        }
    }

    // Método para obter uma lista simplificada de itens do cardápio que possuem materiais suficientes em estoque
    public List<MenuItemResponseTest> getSimplifiedMenuItems() {
        // Obtém a lista de todos os itens do cardápio do banco de dados
        List<MenuItemDomainTest> menuItemDomainTestList = menuItemRepositoryTest.findAll();
        List<MenuItemResponseTest> simplifiedMenuItems = new ArrayList<>();

        for (MenuItemDomainTest menuItem : menuItemDomainTestList) {
            // Converte o objeto MenuItemDomain para uma resposta simplificada (MenuItemResponse)
            MenuItemResponseTest simplifiedMenuItem = menuItemConverterTest.convertMenuItemDomainToResponse(menuItem);

            // Verifica se todos os materiais necessários para este item do cardápio têm quantidade suficiente em estoque
            boolean hasEnoughQuantity = true;

            for (MaterialInfoTest material : menuItem.getMaterialsRecipe()) {
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
        MaterialDomainTest materialDomainTest = materialRepositoryTest.findByName(name);
        if (materialDomainTest == null) {
            throw new InvalidDataException("Material não encontrado pelo nome: " + name);
        }

        // Verifica se a quantidade em estoque do material é maior ou igual à quantidade requerida
        return materialServiceTest.calculateStock(materialDomainTest) >= requiredQuantity;
    }

    // Método para obter uma lista detalhada de itens do cardápio com informações adicionais, como custo estimado
    public List<MenuItemDetailedResponseTest> getAllMenuItemsWithDetails() {
        // Obtém a lista de todos os itens do cardápio do banco de dados
        List<MenuItemDomainTest> menuItemDomainTestList = menuItemRepositoryTest.findAll();
        List<MenuItemDetailedResponseTest> detailedResponses = new ArrayList<>();

        for (MenuItemDomainTest menuItem : menuItemDomainTestList) {

            // Cálculo do preço de custo estimado baseado nos lotes consumidos
            BigDecimal totalCost = calculateMenuItemTotalCost(menuItem.getMaterialsRecipe());

            // Converte o objeto MenuItemDomain para uma resposta detalhada (MenuItemDetailedResponse)
            MenuItemDetailedResponseTest detailedResponse = menuItemConverterTest.convertMenuItemDomainToDetailed(menuItem, totalCost);

            // Adiciona o item detalhado à lista de respostas
            detailedResponses.add(detailedResponse);
        }

        return detailedResponses;
    }

    // Método para calcular o custo total dos materiais necessários para um item do cardápio
    public BigDecimal calculateMenuItemTotalCost(List<MaterialInfoTest> materialsRecipe) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (MaterialInfoTest materialInfoTest : materialsRecipe) {
            // Declara a quantidade que será consumida deste material
            double amountToBeConsumed = materialInfoTest.getQuantity();
            // Obtém o material pelo nome do MaterialInfo atual
            MaterialDomainTest materialDomainTest = materialRepositoryTest.findByName(materialInfoTest.getMaterialName());
            if (materialDomainTest == null) {
                throw new InvalidMaterialDataException("Não foi possível achar o material pelo nome :" + materialInfoTest.getMaterialName());
            }
            // Obtém a lista de lotes do material que foi encontrado através do MaterialInfo atual
            List<BatchDomainTest> batchDomainTestList = materialDomainTest.getBatchDomainTestList();
            // Ordena a lista de lotes por validade
            Collections.sort(batchDomainTestList);

            // Calcula dos lotes mais próximos a vencer
            BigDecimal costForQuantity = batchServiceTest.calculateCostForQuantityFromBatch(materialDomainTest, amountToBeConsumed);
            totalCost = totalCost.add(costForQuantity);
        }

        // Arredonda o custo total para duas casas decimais e retorna
        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }
}
