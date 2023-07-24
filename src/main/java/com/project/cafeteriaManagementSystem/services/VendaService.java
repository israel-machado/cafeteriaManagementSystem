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

    private LoteService loteService;
    private final VendaRepository vendaRepository;
    private final MaterialRepository materialRepository;
    private final VendaConverter vendaConverter;

    // Método para realizar a venda
    public VendaResponse sell(VendaRequest vendaRequest) {

        // Verificar se o estoque de materiais é suficiente
        for (MaterialResponse materialConsumido : vendaRequest.getMaterialsConsumed()) {
            MaterialDomain materialAtual = materialRepository.findById(materialConsumido.getId())
                    .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + materialConsumido.getId()));

            double quantidadeAtual = materialAtual.getQuantity();
            double quantidadeConsumida = materialConsumido.getQuantity();

            if (quantidadeAtual < quantidadeConsumida) {
                throw new InsufficientMaterialStockException("Estoque insuficiente para o material: " + materialAtual.getName());
            }

            // Consumir quantidade do lote atual do loop
            loteService.consumeMaterialFromLote(materialAtual, quantidadeConsumida);
        }

        // Conversão da entidade para domain
        VendaDomain vendaDomain = vendaConverter.convertVendaRequestToDomain(vendaRequest);

        // Atualizar estoque de materiais após venda
        updateMaterialStock(vendaRequest.getMaterialsConsumed());

        // Salvar a venda no banco de dados
        vendaRepository.save(vendaDomain);

        // Retornar a resposta da venda
        return vendaConverter.convertVendaDomainToResponse(vendaDomain);

    }

    private void updateMaterialStock(List<MaterialResponse> consumedMaterials) {
        for (MaterialResponse materialConsumed : consumedMaterials) {

            MaterialDomain existingMaterial = materialRepository.findById(materialConsumed.getId())
                    .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + materialConsumed.getId()));

            double actualQuantity = existingMaterial.getQuantity();
            double consumedQuantity = materialConsumed.getQuantity();

            double newQuantity = actualQuantity - consumedQuantity;

            existingMaterial.setQuantity(newQuantity);
            materialRepository.save(existingMaterial);
        }
    }

    // GET ALL
    public List<VendaResponse> getAllSells() {
        List<VendaDomain> vendaDomainList = vendaRepository.findAll();
        return vendaConverter.convertVendaDomainListToVendaResponseList(vendaDomainList);
    }

    // Vendas nos últimos 30 dias
    public List<VendaDomain> getVendasLast30Days() {
        LocalDate currentDate = LocalDate.now();
        LocalDate thirtyDaysAgo = currentDate.minusDays(30);
        return vendaRepository.findByDateBetween(thirtyDaysAgo, currentDate);
    }

    // Profit nos últimos 30 dias
    public BigDecimal calculateProfitLast30Days() {
        List<VendaDomain> vendasLast30Days = getVendasLast30Days();
        BigDecimal totalProfit = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (VendaDomain venda : vendasLast30Days) {
            totalCost = calculateTotalCost(venda.getMaterialsConsumed());
            totalProfit = totalProfit.add(venda.getSaleValue().subtract(totalCost));
        }

        return totalProfit;
    }

    public BigDecimal calculateTotalCost(List<MaterialDomain> materialsConsumed) {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (MaterialDomain material : materialsConsumed) {
            BigDecimal materialCost = material.getCost();
            BigDecimal quantityConsumed = BigDecimal.valueOf(material.getQuantity());
            BigDecimal materialTotalCost = materialCost.multiply(quantityConsumed);
            totalCost = totalCost.add(materialTotalCost);
        }

        return  totalCost;
    }
}
