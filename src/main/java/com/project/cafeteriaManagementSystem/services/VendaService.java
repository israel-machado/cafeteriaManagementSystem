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

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

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
}