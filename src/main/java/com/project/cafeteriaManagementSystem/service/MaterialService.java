package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.exception.InvalidDataException;
import com.project.cafeteriaManagementSystem.exception.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.mapping.BatchConverter;
import com.project.cafeteriaManagementSystem.mapping.MaterialConverter;
import com.project.cafeteriaManagementSystem.model.batch.BatchDomain;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponse;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialMinimumStockRequest;
import com.project.cafeteriaManagementSystem.model.material.MaterialRequest;
import com.project.cafeteriaManagementSystem.model.material.MaterialResponse;
import com.project.cafeteriaManagementSystem.repository.BatchRepository;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {
    // Atributos e injeções de dependência
    private final MaterialConverter materialConverter;
    private final MaterialRepository materialRepository;
    private final BatchConverter batchConverter;
    private final BatchRepository batchRepository;

    // GET ALL
    public List<MaterialResponse> getAllMaterials() {
        // Obtém todos os materiais do repositório e converte para uma lista de MaterialResponse
        List<MaterialDomain> materialList = materialRepository.findAll();
        return materialConverter.convertMaterialDomainListToResponseList(materialList);
    }

    // GET BY ID
    public MaterialResponse getMaterialById(String id) {
        // Obtém o material pelo ID do repositório e converte para MaterialResponse
        MaterialDomain materialDomain = materialRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + id));
        return materialConverter.convertMaterialDomainToResponse(materialDomain);
    }

    // UPDATE
    public MaterialResponse updateMaterial(String id, MaterialRequest materialRequest) {
        try {
            // Verifica se a requisição do material é nula
            if (materialRequest == null) {
                throw new InvalidDataException("Requisição do material não pode ser nula.");
            }
            // Converte a requisição para o domínio e define o ID para atualizar o material existente
            MaterialDomain materialDomain = materialConverter.convertMaterialRequestToDomain(materialRequest);
            materialDomain.setId(id);
            // Salva o material atualizado no repositório
            MaterialDomain updatedMaterial = materialRepository.save(materialDomain);
            return materialConverter.convertMaterialDomainToResponse(updatedMaterial);
        } catch (InvalidDataException e) {
            throw new InvalidMaterialDataException("Falha ao atualizar o material com o ID: " + id);
        }
    }

    // DELETE
    public void deleteMaterial(String id) {
        try {
            // Deleta o material pelo ID do repositório
            materialRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidMaterialDataException("Material não encontrado através do ID: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidMaterialDataException("Erro ao deletar material com o ID: " + id + " - " + e.getMessage());
        }
    }

    // INSERT
    public MaterialResponse createMaterial(MaterialRequest materialRequest) {

        // Verifica se o material já existe no banco de dados pelo nome
        MaterialDomain existingMaterial = materialRepository.findByName(materialRequest.getName());

        // Inicia uma variável do tipo MaterialDomain
        MaterialDomain materialDomain;

        if (existingMaterial != null) {
            // Se o material já existe, retorna uma mensagem de erro
            throw new InvalidDataException("Material já existe no banco de dados.");

        } else {
            // Se o material não existe, cria um novo material
            materialDomain = materialConverter.convertMaterialRequestToDomain(materialRequest);

            // Salva o material no banco de dados
            materialDomain = materialRepository.save(materialDomain);
        }

        // Convertendo o domínio para a resposta
        return materialConverter.convertMaterialDomainToResponse(materialDomain);
    }

    // Método para obter os materiais que estão prestes a vencer
    public List<MaterialResponse> getExpiringMaterials(int daysToExpiration) {
        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDateThreshold = currentDate.plusDays(daysToExpiration);

        // Obtém todos os materiais do repositório
        List<MaterialDomain> materials = materialRepository.findAll();
        List<MaterialResponse> expiringMaterials = new ArrayList<>();

        for (MaterialDomain material : materials) {
            // Obtém a lista de lotes do material atual
            List<BatchDomain> batchDomainList = material.getBatchDomainList();

            if (batchDomainList.isEmpty() && batchDomainList == null) {
                throw new InvalidDataException("O Material não possui lotes no momento.");
            }
            //Inicializa uma lista de lote do tipo Response
            List<BatchResponse> expiringBatches = new ArrayList<>();

            for (BatchDomain batch : batchDomainList) {
                if (batch.getValidity().isBefore(expirationDateThreshold.atStartOfDay())) {
                    // Se o batch está prestes a vencer, adiciona na lista de lotes expirando
                    expiringBatches.add(batchConverter.convertBatchDomainToResponse(batch));
                }
            }
            // Se a lista expiringBatches estiver vazia retorna uma mensagem
            if (expiringBatches.isEmpty()) {
                throw new InvalidDataException("O material não possui nenhum lote prestes a expirar em " + daysToExpiration + " dias");
            }

            // Se há lotes expirando, cria uma resposta para o material e adiciona na lista
            MaterialResponse materialResponse = materialConverter.convertMaterialDomainToResponse(material);
            materialResponse.setBatchResponsesList(expiringBatches);
            expiringMaterials.add(materialResponse);
        }

        return expiringMaterials;
    }

    // Método para atualizar a quantidade mínima de estoque de um material
    public MaterialResponse updateMaterialMinimumStock(MaterialMinimumStockRequest request) {
        // Obtém o material pelo ID do repositório
        MaterialDomain material = materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + request.getMaterialId()));

        // Atualiza a quantidade mínima de estoque do material e salva no repositório
        material.setMinimumStockQuantity(request.getMinimumStockQuantity());
        materialRepository.save(material);

        // Converte o domínio atualizado para a resposta e retorna
        return materialConverter.convertMaterialDomainToResponse(material);
    }

    // Método para obter os materiais com estoque baixo
    public List<MaterialResponse> getMaterialsWithLowStock() {
        // Obtém todos os materiais do repositório
        List<MaterialDomain> materialDomainList = materialRepository.findAll();
        List<MaterialResponse> materialsWithLowStock = new ArrayList<>();

        for (MaterialDomain material : materialDomainList) {
            // Obtém a lista de lotes do material atual
            List<BatchDomain> batchDomainList = material.getBatchDomainList();
            if (batchDomainList.isEmpty() && batchDomainList == null) {
                throw new InvalidDataException("O Material não possui lotes no momento.");
            }
            // Inicializa a variável do resultado da soma da quantidade restante total dos lotes
            int totalAmount = 0;

            for (BatchDomain batch : batchDomainList) {
                // Pra cada lote da lista vai somar a quantidade restante no totalAmount
                totalAmount += batch.getRemainingAmount();
            }
            // Verifica se a quantidade atual do material é menor que a quantidade mínima de estoque
            if (material.getMinimumStockQuantity() < totalAmount) {
                // Se a quantidade está baixa, cria uma resposta para o material e adiciona na lista
                throw new InvalidMaterialDataException("O material " + material.getName() + " está com estoque abaixo no mínimo.");
            }

            MaterialResponse materialResponse = materialConverter.convertMaterialDomainToResponse(material);
            materialsWithLowStock.add(materialResponse);
        }

        return materialsWithLowStock;
    }
}