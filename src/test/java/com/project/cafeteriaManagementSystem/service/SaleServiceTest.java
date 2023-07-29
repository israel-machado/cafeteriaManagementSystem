package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.exception.InsufficientMaterialStockException;
import com.project.cafeteriaManagementSystem.exception.InvalidDataException;
import com.project.cafeteriaManagementSystem.exception.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.mapping.SaleConverterTest;
import com.project.cafeteriaManagementSystem.model.batch.BatchDomainTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomainTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialInfoTest;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDomainTest;
import com.project.cafeteriaManagementSystem.model.sale.SaleDomainTest;
import com.project.cafeteriaManagementSystem.model.sale.SaleItemTest;
import com.project.cafeteriaManagementSystem.model.sale.SaleRequestTest;
import com.project.cafeteriaManagementSystem.model.sale.SaleResponseTest;
import com.project.cafeteriaManagementSystem.repository.MaterialRepositoryTest;
import com.project.cafeteriaManagementSystem.repository.MenuItemRepositoryTest;
import com.project.cafeteriaManagementSystem.repository.SaleRepositoryTest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceTest {

    private final SaleRepositoryTest saleRepositoryTest;
    private final MaterialRepositoryTest materialRepositoryTest;
    private final MenuItemRepositoryTest menuItemRepositoryTest;
    private final SaleConverterTest saleConverterTest;
    private final BatchServiceTest batchServiceTest;
    private final MaterialServiceTest materialServiceTest;

    // Método para obter uma lista de todas as vendas
    public List<SaleResponseTest> getAllSales() {
        List<SaleDomainTest> saleDomainTestList = saleRepositoryTest.findAll();
        return saleConverterTest.convertSaleDomainListToSaleResponseList(saleDomainTestList);
    }

    // Método para obter uma venda pelo ID
    public SaleResponseTest getSaleById(String id) {
        SaleDomainTest saleDomainTest = saleRepositoryTest.findById(id)
                .orElseThrow(() -> new InvalidDataException("Venda não encontrada pelo ID: " + id));

        return saleConverterTest.convertSaleDomainToResponse(saleDomainTest);
    }

    // Método para realizar uma venda
    public SaleResponseTest makeSale(SaleRequestTest saleRequestTest) {
        // Obtém a lista de pratos da requisição da venda pelo ID
        List<MenuItemDomainTest> menuItemDomainTestList = menuItemRepositoryTest.findAllById(saleRequestTest.getMenuItemId());
        // Inicia a variável que servirá para somar o valor de cada prato da venda
        BigDecimal totalSalePrice = BigDecimal.ZERO;

        // Verificar se o estoque de materiais é suficiente e consumir do estoque
        for (MenuItemDomainTest menuItem : menuItemDomainTestList) {
            // Obtém lista das informações dos materiais do prato
            List<MaterialInfoTest> materialInfoTestList = menuItem.getMaterialsRecipe();

            for (MaterialInfoTest materialInfoTest : materialInfoTestList) {
                // Verifica se o material existe pelo nome do MaterialInfo atual
                MaterialDomainTest materialDomainTest = materialRepositoryTest.findByName(materialInfoTest.getMaterialName());
                // Se for nulo, envia uma resposta de erro
                if (materialDomainTest == null) {
                    throw new InvalidMaterialDataException("Material não encontrado no DB pelo nome: " + materialInfoTest.getMaterialName());
                }
                // Verifica se o estoque é suficiente para a quantidade informada para o prato
                if (materialServiceTest.calculateStock(materialDomainTest) > materialInfoTest.getQuantity()) {
                    // Consome do lote por ordem de validade cada material com a respectiva quantidade
                    batchServiceTest.consumeAmountFromBatch(materialDomainTest, materialInfoTest.getQuantity());

                } else { // Se não tiver estoque suficiente retorna uma mensagem de insufiência no estoque
                    throw new InsufficientMaterialStockException("Estoque insuficiente para o material: " + materialDomainTest.getName());
                }
            }

           totalSalePrice = totalSalePrice.add(menuItem.getSalePrice());
        }

        // Custo total da venda
        BigDecimal totalSaleCost = calculateSaleTotalCost(menuItemDomainTestList);

        // Constrói a entidade SaleDomain
        SaleDomainTest saleDomainTest = SaleDomainTest.builder()
                .dateOfSale(saleRequestTest.getDateOfSale())
                .salePrice(totalSalePrice)
                .saleCost(calculateSaleTotalCost(menuItemDomainTestList))
                .profitValue(calculateProfitValue(totalSalePrice, totalSaleCost))
                .profitMargin(calculateProfitMargin(totalSalePrice, totalSaleCost))
                .saleItemTests(generateSaleItems(menuItemDomainTestList))
                .build();

        // Salva a venda no banco de dados usando o repositório de vendas
        saleRepositoryTest.save(saleDomainTest);

        // Converte a entidade SaleDomain para a resposta SaleResponse e a retorna
        return saleConverterTest.convertSaleDomainToResponse(saleDomainTest);
    }

    // Método para calcular o custo total dos materiais consumidos em uma venda
    private BigDecimal calculateSaleTotalCost(List<MenuItemDomainTest> menuItemDomainsListTest) {

        BigDecimal totalCost = BigDecimal.ZERO;

        for (MenuItemDomainTest menuItemDomainTest : menuItemDomainsListTest) {

            List<MaterialInfoTest> materialInfoTestList = menuItemDomainTest.getMaterialsRecipe();

            for (MaterialInfoTest materialInfoTest : materialInfoTestList) {
                // Declara a quantidade que será consumida deste material
                double amountToBeConsumed = materialInfoTest.getQuantity();
                // Obtém o material pelo nome do MaterialInfo atual
                MaterialDomainTest materialDomainTest = materialRepositoryTest.findByName(materialInfoTest.getMaterialName());
                // Obtém a lista de lotes do material que foi encontrado através do MaterialInfo atual
                List<BatchDomainTest> batchDomainTestList = materialDomainTest.getBatchDomainTestList();
                // Ordena a lista de lotes por validade
                Collections.sort(batchDomainTestList);

                // Calcula e consome do lote mais próximo a vencer
                BigDecimal costForQuantity = batchServiceTest.calculateCostForQuantityFromBatch(materialDomainTest, amountToBeConsumed);
                totalCost = totalCost.add(costForQuantity);
            }
        }
        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    // Método para calcular o valor do lucro
    private BigDecimal calculateProfitValue(BigDecimal salePrice, BigDecimal saleCost) {
        // Calcula o lucro (preço de venda - custo de venda) e retorna diretamente
        return salePrice.subtract(saleCost);
    }

    // Método para gerar a margem de lucro da venda
    private BigDecimal calculateProfitMargin(BigDecimal salePrice, BigDecimal saleCost) {
        // Calcula o lucro (preço de venda - custo de venda)
        BigDecimal profit = salePrice.subtract(saleCost);

        // Calcula a margem de lucro (lucro / preço de venda) * 100 e retorna
        return profit.divide(salePrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    // Método para salvar a lista dos itens vendidos
    private List<SaleItemTest> generateSaleItems(List<MenuItemDomainTest> menuItemDomainTestList) {
        List<SaleItemTest> saleItemTestList = new ArrayList<>();

        // Para cada menuItem do SaleRequest, vai salvar um SaleItem com nome e preço da venda da data
        for (MenuItemDomainTest menuItem : menuItemDomainTestList) {
            saleItemTestList.add(SaleItemTest.builder()
                    .name(menuItem.getName())
                    .salePrice(menuItem.getSalePrice())
                    .build());
        }

        return saleItemTestList;
    }

    // Método para obter uma lista de vendas realizadas em um determinado período de dias
    public List<SaleResponseTest> getSalesByDuration(int duration) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusDays(duration);
        List<SaleDomainTest> saleDomainTestList = saleRepositoryTest.findByDateOfSaleBetween(startDate, currentDate);
        return saleConverterTest.convertSaleDomainListToSaleResponseList(saleDomainTestList);
    }

    // Método para obter uma lista de vendas realizadas em um determinado mês
    public List<SaleResponseTest> getSalesForMonth(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        List<SaleDomainTest> saleDomainTestList = saleRepositoryTest.findByDateOfSaleBetween(startDate, endDate);
        return saleConverterTest.convertSaleDomainListToSaleResponseList(saleDomainTestList);
    }

    // Método para calcular o lucro total das vendas realizadas em um determinado período de dias
    public BigDecimal getProfitForTimePeriod(int duration) {
        List<SaleResponseTest> salesForTimePeriod = getSalesByDuration(duration);
        BigDecimal totalProfit = BigDecimal.ZERO;

        // Calcula o lucro total somando o lucro de cada venda no período especificado
        for (SaleResponseTest sale : salesForTimePeriod) {
            totalProfit = totalProfit.add(sale.getProfitValue());
        }

        return totalProfit;
    }
}