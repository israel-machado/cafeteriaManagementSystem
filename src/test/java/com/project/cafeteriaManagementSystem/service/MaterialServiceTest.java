package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.exception.InvalidDataException;
import com.project.cafeteriaManagementSystem.exception.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.mapping.BatchConverterTest;
import com.project.cafeteriaManagementSystem.mapping.MaterialConverterTest;
import com.project.cafeteriaManagementSystem.model.batch.BatchDomainTest;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponseTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomainTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialMinimumStockRequestTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialRequestTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialResponseTest;
import com.project.cafeteriaManagementSystem.repository.MaterialRepositoryTest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceTest {
    // Atributos e injeções de dependência
    private final MaterialConverterTest materialConverterTest;
    private final MaterialRepositoryTest materialRepositoryTest;
    private final BatchConverterTest batchConverterTest;

    // GET ALL
    public List<MaterialResponseTest> getAllMaterials() {
        // Obtém todos os materiais do repositório e converte para uma lista de MaterialResponse
        List<MaterialDomainTest> materialList = materialRepositoryTest.findAll();
        return materialConverterTest.convertMaterialDomainListToResponseList(materialList);
    }

    // GET BY ID
    public MaterialResponseTest getMaterialById(String id) {
        // Obtém o material pelo ID do repositório e converte para MaterialResponse
        MaterialDomainTest materialDomainTest = materialRepositoryTest.findById(id)
                .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + id));
        return materialConverterTest.convertMaterialDomainToResponse(materialDomainTest);
    }

    // UPDATE
    public MaterialResponseTest updateMaterial(String id, MaterialRequestTest materialRequestTest) {
        try {
            // Verifica se a requisição do material é nula
            if (materialRequestTest == null) {
                throw new InvalidDataException("Requisição do material não pode ser nula.");
            }
            // Converte a requisição para o domínio e define o ID para atualizar o material existente
            MaterialDomainTest materialDomainTest = materialConverterTest.convertMaterialRequestToDomain(materialRequestTest);
            materialDomainTest.setId(id);
            // Salva o material atualizado no repositório
            MaterialDomainTest updatedMaterial = materialRepositoryTest.save(materialDomainTest);
            return materialConverterTest.convertMaterialDomainToResponse(updatedMaterial);
        } catch (InvalidDataException e) {
            throw new InvalidMaterialDataException("Falha ao atualizar o material com o ID: " + id);
        }
    }

    // DELETE
    public void deleteMaterial(String id) {
        try {
            // Deleta o material pelo ID do repositório
            materialRepositoryTest.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidMaterialDataException("Material não encontrado através do ID: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidMaterialDataException("Erro ao deletar material com o ID: " + id + " - " + e.getMessage());
        }
    }

    // INSERT
    public MaterialDomainTest createMaterial(MaterialRequestTest materialRequestTest) {

        // Verifica se o material já existe no banco de dados pelo nome
        MaterialDomainTest existingMaterial = materialRepositoryTest.findByName(materialRequestTest.getName());

        if (existingMaterial != null) {
            // Se o material já existe, retorna uma mensagem de erro
            throw new InvalidDataException("Material já existe no banco de dados.");

        }

        // Se o material não existe, cria um novo material
        MaterialDomainTest materialDomainTest = materialConverterTest.convertMaterialRequestToDomain(materialRequestTest);

        // Salva o material no banco de dados e retorna
        return materialRepositoryTest.save(materialDomainTest);
    }

    // Método para obter os materiais que estão prestes a vencer
    public List<MaterialResponseTest> getExpiringMaterials(int daysToExpiration) {
        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDateThreshold = currentDate.plusDays(daysToExpiration);

        // Obtém todos os materiais do repositório
        List<MaterialDomainTest> materials = materialRepositoryTest.findAll();
        List<MaterialResponseTest> expiringMaterials = new ArrayList<>();

        for (MaterialDomainTest material : materials) {
            // Obtém a lista de lotes do material atual
            List<BatchDomainTest> batchDomainTestList = material.getBatchDomainTestList();

            if (batchDomainTestList.isEmpty()) {
                throw new InvalidDataException("O Material não possui lotes no momento.");
            }
            //Inicializa uma lista de lote do tipo Response
            List<BatchResponseTest> expiringBatches = new ArrayList<>();

            for (BatchDomainTest batch : batchDomainTestList) {
                if (batch.getValidity().isBefore(expirationDateThreshold.atStartOfDay())) {
                    // Se o batch está prestes a vencer, adiciona na lista de lotes expirando
                    expiringBatches.add(batchConverterTest.convertBatchDomainToResponse(batch));
                }
            }
            // Se a lista expiringBatches estiver vazia retorna uma mensagem
            if (expiringBatches.isEmpty()) {
                throw new InvalidDataException("O material não possui nenhum lote prestes a expirar em " + daysToExpiration + " dias");
            }

            // Se há lotes expirando, cria uma resposta para o material e adiciona na lista
            MaterialResponseTest materialResponseTest = materialConverterTest.convertMaterialDomainToResponse(material);
            materialResponseTest.setBatchResponsesListTest(expiringBatches);
            expiringMaterials.add(materialResponseTest);
        }

        return expiringMaterials;
    }

    // Método para atualizar a quantidade mínima de estoque de um material
    public MaterialResponseTest updateMaterialMinimumStock(MaterialMinimumStockRequestTest request) {
        // Obtém o material pelo ID do repositório
        MaterialDomainTest material = materialRepositoryTest.findById(request.getMaterialId())
                .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + request.getMaterialId()));

        // Atualiza a quantidade mínima de estoque do material e salva no repositório
        material.setMinimumStockQuantity(request.getMinimumStockQuantity());
        materialRepositoryTest.save(material);

        // Converte o domínio atualizado para a resposta e retorna
        return materialConverterTest.convertMaterialDomainToResponse(material);
    }

    // Método para atualizar o estoque de todos materiais
    public double calculateStock(MaterialDomainTest materialDomainTest) {
        List<BatchDomainTest> batchDomainTestList = materialDomainTest.getBatchDomainTestList();
        double totalStock = 0.0;

        for (BatchDomainTest batch : batchDomainTestList) {
            totalStock += batch.getRemainingAmount();
        }

        return totalStock;
    }

    // Método para obter os materiais com estoque baixo
    public List<MaterialResponseTest> getMaterialsWithLowStock() {
        // Obtém todos os materiais do repositório
        List<MaterialDomainTest> materialDomainTestList = materialRepositoryTest.findAll();
        List<MaterialResponseTest> materialsWithLowStock = new ArrayList<>();

        for (MaterialDomainTest material : materialDomainTestList) {
            // Obtém a lista de lotes do material atual
            List<BatchDomainTest> batchDomainTestList = material.getBatchDomainTestList();
            if (batchDomainTestList.isEmpty()) {
                throw new InvalidDataException("O Material não possui lotes no momento.");
            }
            // Inicializa a variável do resultado da soma da quantidade restante total dos lotes
            int totalAmount = 0;

            for (BatchDomainTest batch : batchDomainTestList) {
                // Pra cada lote da lista vai somar a quantidade restante no totalAmount
                totalAmount += batch.getRemainingAmount();
            }
            // Verifica se a quantidade atual do material é menor que a quantidade mínima de estoque
            if (material.getMinimumStockQuantity() < totalAmount) {
                // Se a quantidade está baixa, cria uma resposta para o material e adiciona na lista
                throw new InvalidMaterialDataException("O material " + material.getName() + " está com estoque abaixo no mínimo.");
            }

            MaterialResponseTest materialResponseTest = materialConverterTest.convertMaterialDomainToResponse(material);
            materialsWithLowStock.add(materialResponseTest);
        }

        return materialsWithLowStock;
    }
}