package com.project.cafeteriaManagementSystem.services;

import com.project.cafeteriaManagementSystem.exceptions.InsufficientMaterialStockException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidDataException;
import com.project.cafeteriaManagementSystem.mapping.VendaConverter;
import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.Material.MaterialResponse;
import com.project.cafeteriaManagementSystem.model.Venda.VendaDomain;
import com.project.cafeteriaManagementSystem.model.Venda.VendaRequest;
import com.project.cafeteriaManagementSystem.model.Venda.VendaResponse;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
import com.project.cafeteriaManagementSystem.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final MaterialRepository materialRepository;
    private final VendaConverter vendaConverter;

    // Método para realizar uma venda
    public VendaResponse sell(VendaRequest vendaRequest) {
        // Verificar se o estoque de materiais é suficiente e consumir do estoque
        for (MaterialResponse materialConsumido : vendaRequest.getMaterialsConsumed()) {
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
        VendaDomain vendaDomain = vendaConverter.convertVendaRequestToDomain(vendaRequest);

        // Atualiza o estoque de materiais após a venda (chama o método privado updateMaterialStock)
        updateMaterialStock(vendaRequest.getMaterialsConsumed());

        // Salva a venda no banco de dados usando o repositório de vendas
        vendaRepository.save(vendaDomain);

        // Converte a entidade VendaDomain para a resposta VendaResponse e a retorna
        return vendaConverter.convertVendaDomainToResponse(vendaDomain);
    }

    // Método para obter uma venda pelo ID
    public VendaResponse getSellById(String id) {
        VendaDomain vendaDomain = vendaRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Venda não encontrada pelo ID: " + id));

        return vendaConverter.convertVendaDomainToResponse(vendaDomain);
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
    public List<VendaResponse> getAllSells() {
        List<VendaDomain> vendaDomainList = vendaRepository.findAll();
        return vendaConverter.convertVendaDomainListToVendaResponseList(vendaDomainList);
    }

    // Método para obter uma lista de vendas realizadas nos últimos 30 dias
    public List<VendaDomain> getVendasLast30Days() {
        LocalDate currentDate = LocalDate.now();
        LocalDate thirtyDaysAgo = currentDate.minusDays(30);
        return vendaRepository.findByDateBetween(thirtyDaysAgo, currentDate);
    }

    // Método para calcular o lucro total das vendas realizadas nos últimos 30 dias
    public BigDecimal calculateProfitLast30Days() {
        List<VendaDomain> vendasLast30Days = getVendasLast30Days();
        BigDecimal totalProfit = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        // Calcula o lucro total somando o lucro de cada venda nos últimos 30 dias
        for (VendaDomain venda : vendasLast30Days) {
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