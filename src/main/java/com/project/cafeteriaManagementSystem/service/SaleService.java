package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.exception.InsufficientMaterialStockException;
import com.project.cafeteriaManagementSystem.exception.InvalidDataException;
import com.project.cafeteriaManagementSystem.mapping.SaleConverter;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialResponse;
import com.project.cafeteriaManagementSystem.model.sale.SaleDomain;
import com.project.cafeteriaManagementSystem.model.sale.SaleRequest;
import com.project.cafeteriaManagementSystem.model.sale.SaleResponse;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
import com.project.cafeteriaManagementSystem.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final MaterialRepository materialRepository;
    private final SaleConverter saleConverter;

    // Método para realizar uma venda
    public SaleResponse makeSale(SaleRequest saleRequest) {
        // Verificar se o estoque de materiais é suficiente e consumir do estoque
        for (MaterialResponse materialConsumido : saleRequest.getMaterialsConsumed()) {
            MaterialDomain materialAtual = materialRepository.findById(materialConsumido.getId())
                    .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + materialConsumido.getId()));

            // Obtém as quantidades atuais do material no estoque e a quantidade a ser consumida
            double quantidadeAtual = materialAtual.getQuantity();
            double quantidadeConsumida = materialConsumido.getQuantity();

            // Verifica se o estoque é suficiente para a quantidade consumida
            if (quantidadeAtual < quantidadeConsumida) {
                throw new InsufficientMaterialStockException("Estoque insuficiente para o material: " + materialAtual.getName());
            }

            // Subtrai a quantidade consumida do estoque do material e salva as alterações no banco de dados
            materialAtual.setQuantity(quantidadeAtual - quantidadeConsumida);
            materialRepository.save(materialAtual);
        }

        // Converte a entidade VendaRequest para o domínio VendaDomain
        SaleDomain saleDomain = saleConverter.convertVendaRequestToDomain(saleRequest);

        // Atualiza o estoque de materiais após a venda (chama o método privado updateMaterialStock)
        updateMaterialStock(saleRequest.getMaterialsConsumed());

        // Salva a venda no banco de dados usando o repositório de vendas
        saleRepository.save(saleDomain);

        // Converte a entidade VendaDomain para a resposta VendaResponse e a retorna
        return saleConverter.convertVendaDomainToResponse(saleDomain);
    }

    // Método para obter uma venda pelo ID
    public SaleResponse getSaleById(String id) {
        SaleDomain saleDomain = saleRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Venda não encontrada pelo ID: " + id));

        return saleConverter.convertVendaDomainToResponse(saleDomain);
    }

    // Método privado para atualizar o estoque de materiais após uma venda
    private void updateMaterialStock(List<MaterialResponse> consumedMaterials) {
        for (MaterialResponse materialConsumed : consumedMaterials) {
            MaterialDomain existingMaterial = materialRepository.findById(materialConsumed.getId())
                    .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + materialConsumed.getId()));

            // Obtém as quantidades atuais do material no estoque e a quantidade consumida na venda
            double actualQuantity = existingMaterial.getQuantity();
            double consumedQuantity = materialConsumed.getQuantity();

            // Calcula a nova quantidade após a venda e atualiza o estoque do material no banco de dados
            double newQuantity = actualQuantity - consumedQuantity;
            existingMaterial.setQuantity(newQuantity);
            materialRepository.save(existingMaterial);
        }
    }

    // Método para obter uma lista de todas as vendas
    public List<SaleResponse> getAllSales() {
        List<SaleDomain> saleDomainList = saleRepository.findAll();
        return saleConverter.convertVendaDomainListToVendaResponseList(saleDomainList);
    }

    // Método para obter uma lista de vendas realizadas nos últimos 30 dias
    public List<SaleDomain> getSalesLast30Days() {
        LocalDate currentDate = LocalDate.now();
        LocalDate thirtyDaysAgo = currentDate.minusDays(30);
        return saleRepository.findByDateBetween(thirtyDaysAgo, currentDate);
    }

    // Método para calcular o lucro total das vendas realizadas nos últimos 30 dias
    public BigDecimal calculateProfitLast30Days() {
        List<SaleDomain> vendasLast30Days = getSalesLast30Days();
        BigDecimal totalProfit = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        // Calcula o lucro total somando o lucro de cada venda nos últimos 30 dias
        for (SaleDomain venda : vendasLast30Days) {
            totalCost = calculateTotalCost(venda.getMaterialsConsumed());
            totalProfit = totalProfit.add(venda.getSaleValue().subtract(totalCost));
        }

        return totalProfit;
    }

    // Método para calcular o custo total dos materiais consumidos em uma venda
    public BigDecimal calculateTotalCost(List<MaterialDomain> materialsConsumed) {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (MaterialDomain material : materialsConsumed) {
            BigDecimal materialCost = material.getCost();
            BigDecimal quantityConsumed = BigDecimal.valueOf(material.getQuantity());
            BigDecimal materialTotalCost = materialCost.multiply(quantityConsumed);
            totalCost = totalCost.add(materialTotalCost);
        }

        return totalCost;
    }
}