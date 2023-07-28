package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.exception.InsufficientMaterialStockException;
import com.project.cafeteriaManagementSystem.exception.InvalidDataException;
import com.project.cafeteriaManagementSystem.exception.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.mapping.SaleConverter;
import com.project.cafeteriaManagementSystem.model.batch.BatchDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialInfo;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDomain;
import com.project.cafeteriaManagementSystem.model.sale.SaleDomain;
import com.project.cafeteriaManagementSystem.model.sale.SaleItem;
import com.project.cafeteriaManagementSystem.model.sale.SaleRequest;
import com.project.cafeteriaManagementSystem.model.sale.SaleResponse;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
import com.project.cafeteriaManagementSystem.repository.MenuItemRepository;
import com.project.cafeteriaManagementSystem.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final MaterialRepository materialRepository;
    private final MenuItemRepository menuItemRepository;
    private final SaleConverter saleConverter;
    private final BatchService batchService;

    // Método para obter uma lista de todas as vendas
    public List<SaleResponse> getAllSales() {
        List<SaleDomain> saleDomainList = saleRepository.findAll();
        return saleConverter.convertSaleDomainListToSaleResponseList(saleDomainList);
    }

    // Método para obter uma venda pelo ID
    public SaleResponse getSaleById(String id) {
        SaleDomain saleDomain = saleRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Venda não encontrada pelo ID: " + id));

        return saleConverter.convertSaleDomainToResponse(saleDomain);
    }

    // Método para realizar uma venda
    public SaleResponse makeSale(SaleRequest saleRequest) {
        // Obtém a lista de pratos da requisição da venda pelo ID
        List<MenuItemDomain> menuItemDomainList = menuItemRepository.findAllById(saleRequest.getMenuItemId());
        // Inicia a variável que servirá para somar o valor de cada prato da venda
        BigDecimal totalSalePrice = BigDecimal.ZERO;

        // Verificar se o estoque de materiais é suficiente e consumir do estoque
        for (MenuItemDomain menuItem : menuItemDomainList) {
            // Obtém lista das informações dos materiais do prato
            List<MaterialInfo> materialInfoList = menuItem.getMaterialsRecipe();

            for (MaterialInfo materialInfo : materialInfoList) {
                // Verifica se o material existe pelo nome do MaterialInfo atual
                MaterialDomain materialDomain = materialRepository.findByName(materialInfo.getMaterialName());
                // Se for nulo, envia uma resposta de erro
                if (materialDomain == null) {
                    throw new InvalidMaterialDataException("Material não encontrado no DB pelo nome: " + materialInfo.getMaterialName());
                }
                // Verifica se o estoque é suficiente para a quantidade informada para o prato
                if (materialDomain.getStock() > materialInfo.getQuantity()) {
                    // Obtém as quantidades atuais do material no estoque e a quantidade a ser consumida
                    double materialStock = materialDomain.getStock();
                    double ammountConsumed = materialInfo.getQuantity();

                    // Subtrai a quantidade consumida do estoque do material e salva as alterações no banco de dados
                    materialDomain.setStock(materialStock - ammountConsumed);
                    materialRepository.save(materialDomain);
                } else { // Se não tiver estoque suficiente retorna uma mensagem de insufiência no estoque
                    throw new InsufficientMaterialStockException("Estoque insuficiente para o material: " + materialDomain.getName());
                }
            }

            totalSalePrice.add(menuItem.getSalePrice());
        }

        // Custo total da venda
        BigDecimal totalSaleCost = calculateSaleTotalCost(menuItemDomainList);

        // Constrói a entidade SaleDomain
        SaleDomain saleDomain = SaleDomain.builder()
                .dateOfSale(saleRequest.getDateOfSale())
                .salePrice(totalSalePrice)
                .saleCost(calculateSaleTotalCost(menuItemDomainList))
                .profitValue(calculateProfitValue(totalSalePrice, totalSaleCost))
                .profitMargin(calculateProfitMargin(totalSalePrice, totalSaleCost))
                .saleItems(generateSaleItems(menuItemDomainList))
                .build();

        // Salva a venda no banco de dados usando o repositório de vendas
        saleRepository.save(saleDomain);

        // Converte a entidade SaleDomain para a resposta SaleResponse e a retorna
        return saleConverter.convertSaleDomainToResponse(saleDomain);
    }

    // Método para calcular o custo total dos materiais consumidos em uma venda
    private BigDecimal calculateSaleTotalCost(List<MenuItemDomain> menuItemDomainsList) {

        BigDecimal totalCost = BigDecimal.ZERO;

        for (MenuItemDomain menuItemDomain : menuItemDomainsList) {

            List<MaterialInfo> materialInfoList = menuItemDomain.getMaterialsRecipe();

            for (MaterialInfo materialInfo : materialInfoList) {
                // Declara a quantidade que será consumida deste material
                double amountToBeConsumed = materialInfo.getQuantity();
                // Obtém o material pelo nome do MaterialInfo atual
                MaterialDomain materialDomain = materialRepository.findByName(materialInfo.getMaterialName());
                // Obtém a lista de lotes do material que foi encontrado através do MaterialInfo atual
                List<BatchDomain> batchDomainList = materialDomain.getBatchDomainList();
                // Ordena a lista de lotes por validade
                Collections.sort(batchDomainList);

                // Calcula e consome do lote mais próximo a vencer
                BigDecimal costForQuantity = batchService.calculateCostForQuantityAndConsumeFromBatch(materialDomain, amountToBeConsumed);
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
    private List<SaleItem> generateSaleItems(List<MenuItemDomain> menuItemDomainList) {
        List<SaleItem> saleItemList = new ArrayList<>();

        // Para cada menuItem do SaleRequest, vai salvar um SaleItem com nome e preço da venda da data
        for (MenuItemDomain menuItem : menuItemDomainList) {
            saleItemList.add(SaleItem.builder()
                    .name(menuItem.getName())
                    .salePrice(menuItem.getSalePrice())
                    .build());
        }

        return saleItemList;
    }

    // Método para obter uma lista de vendas realizadas nos últimos 30 dias
    public List<SaleDomain> getSalesLast30Days() {
        LocalDate currentDate = LocalDate.now();
        LocalDate thirtyDaysAgo = currentDate.minusDays(30);
        return saleRepository.findByDateBetween(thirtyDaysAgo, currentDate);
    }

    // Método para calcular o lucro total das vendas realizadas nos últimos 30 dias
    public BigDecimal getProfitLast30Days() {
        List<SaleDomain> SalesLast30Days = getSalesLast30Days();
        BigDecimal totalProfit = BigDecimal.ZERO;

        // Calcula o lucro total somando o lucro de cada venda nos últimos 30 dias
        for (SaleDomain sale : SalesLast30Days) {
            totalProfit = totalProfit.add(sale.getProfitValue());
        }

        return totalProfit;
    }
}