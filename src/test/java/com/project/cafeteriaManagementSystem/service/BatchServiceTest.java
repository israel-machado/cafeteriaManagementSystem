package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.exception.InvalidDataException;
import com.project.cafeteriaManagementSystem.mapping.BatchConverterTest;
import com.project.cafeteriaManagementSystem.model.batch.BatchDomainTest;
import com.project.cafeteriaManagementSystem.model.batch.BatchRequestTest;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponseTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomainTest;
import com.project.cafeteriaManagementSystem.repository.BatchRepositoryTest;
import com.project.cafeteriaManagementSystem.repository.MaterialRepositoryTest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchServiceTest {

    private final MaterialRepositoryTest materialRepositoryTest;
    private final MaterialServiceTest materialServiceTest;
    private final BatchRepositoryTest batchRepositoryTest;
    private final BatchConverterTest batchConverterTest;

    // Método para criar um novo lote associado a um material específico
    public BatchResponseTest createBatch(BatchRequestTest batchRequestTest) {
        // Procura um materialDomain através do nome da requisição
        MaterialDomainTest materialDomainTest = materialRepositoryTest.findByName(batchRequestTest.getMaterialRequestTest().getName());
        // Se o nome não retornar um objeto, é criado o material e salvo no DB
        if (materialDomainTest == null) {
            materialDomainTest = materialServiceTest.createMaterial(batchRequestTest.getMaterialRequestTest());
            materialRepositoryTest.save(materialDomainTest);
        }

        // Calcula o custo total do lote com base nos dados fornecidos no materialRequest
        BigDecimal calculatedTotalCost = calculateTotalCost(batchRequestTest);

        // Cria o objeto BatchDomain com as informações calculadas e o MaterialDomain associado
        BatchDomainTest batchDomainTest = BatchDomainTest.builder()
                .initialAmount(batchRequestTest.getInitialAmount())
                .totalCost(calculatedTotalCost)
                .validity(batchRequestTest.getValidity())
                .dateOfPurchase(batchRequestTest.getDateOfPurchase())
                .supplierName(batchRequestTest.getSupplierName())
                .remainingAmount(batchRequestTest.getInitialAmount())
                .wasteAmount(0.0)
                .materialDomainTest(materialDomainTest)
                .build();

        // Salva o novo lote no banco de dados usando o repositório e o retorna
        batchRepositoryTest.save(batchDomainTest);

        // Converte para Response e retorna
        return batchConverterTest.convertBatchDomainToResponse(batchDomainTest);
    }

    // Método auxiliar para calcular o custo total do lote com base nos dados fornecidos
    private BigDecimal calculateTotalCost(BatchRequestTest batchRequestTest) {
        BigDecimal initialAmount = BigDecimal.valueOf(batchRequestTest.getInitialAmount());
        BigDecimal totalCost = batchRequestTest.getTotalCost();
        return totalCost.divide(initialAmount, 2, RoundingMode.HALF_UP);
    }

    // Método para consumir a quantidade especificada de um material a partir dos lotes associados
    public void consumeAmountFromBatch(MaterialDomainTest materialDomainTest, double quantityToConsume) {
        List<BatchDomainTest> batchDomainTestList = materialDomainTest.getBatchDomainTestList();
        Collections.sort(batchDomainTestList); // Ordena a lista de lotes por validade //TODO sort direto da base

        for (BatchDomainTest batch : batchDomainTestList) {
            double availableQuantity = batch.getRemainingAmount();

            if (availableQuantity >= quantityToConsume) {
                // Caso a quantidade disponível no lote seja suficiente para atender a quantidade a consumir
                batch.setRemainingAmount(availableQuantity - quantityToConsume);
                batchRepositoryTest.save(batch);

                // Sai do loop, pois toda a quantidade foi consumida
                break;
            } else {
                // Reduz a quantidade a ser consumida pelo que foi consumido deste lote
                quantityToConsume -= availableQuantity;

                // Como zerou a quantidade restante, coloca o remaining como 0 e salva o lote
                batch.setRemainingAmount(0.0);
                batchRepositoryTest.save(batch);
            }
        }
    }

    // Método para calcular o custo da quantidade especificada de um material a partir dos lotes associados
    public BigDecimal calculateCostForQuantityFromBatch(MaterialDomainTest materialDomainTest, double quantityToConsume) {
        BigDecimal totalCost = BigDecimal.ZERO;
        List<BatchDomainTest> batchDomainTestList = materialDomainTest.getBatchDomainTestList();
        Collections.sort(batchDomainTestList); // Ordena a lista de lotes por validade

        for (BatchDomainTest batch : batchDomainTestList) {
            double availableQuantity = batch.getRemainingAmount();

            if (availableQuantity >= quantityToConsume) {
                // Caso a quantidade disponível no lote seja suficiente para atender a quantidade a consumir
                BigDecimal costForQuantity = batch.getTotalCost()
                        .divide(BigDecimal.valueOf(batch.getInitialAmount()), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(quantityToConsume));
                totalCost = totalCost.add(costForQuantity);
                break; // Sai do loop, pois toda a quantidade foi calculada
            } else {
                // Caso a quantidade disponível no lote não seja suficiente para atender a quantidade a consumir
                BigDecimal costForAvailableQuantity = batch.getTotalCost()
                        .divide(BigDecimal.valueOf(batch.getInitialAmount()), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(availableQuantity));
                totalCost = totalCost.add(costForAvailableQuantity);

                // Reduz a quantidade a ser consumida pelo que foi consumido deste lote
                quantityToConsume -= availableQuantity;
            }
        }
        return totalCost;
    }

    // Métodos para obter informações de lotes do banco de dados
    // ---------------------------------------------------------

    // Método para obter uma lista de todos os lotes
    public List<BatchResponseTest> getAllBatches() {
        List<BatchDomainTest> batchDomainTestList = batchRepositoryTest.findAll();
        return batchConverterTest.convertBatchDomainListToResponseList(batchDomainTestList);
    }

    // Método para obter um lote pelo ID
    public BatchResponseTest getBatchById(String id) {
        BatchDomainTest batchDomainTest = batchRepositoryTest.findById(id)
                .orElseThrow(() -> new InvalidDataException("Lote não encontrado pelo ID: " + id));

        return batchConverterTest.convertBatchDomainToResponse(batchDomainTest);
    }

    // Método para atualizar um lote pelo ID
    public BatchResponseTest updateBatch(String id, BatchRequestTest batchRequestTest) {
        try {
            // Busca o lote pelo ID fornecido
            BatchDomainTest existingLote = batchRepositoryTest.findById(id)
                    .orElseThrow(() -> new InvalidDataException("Lote não encontrado pelo ID: " + id));

            // Atualiza o lote com as informações fornecidas
            existingLote.setInitialAmount(batchRequestTest.getInitialAmount());
            existingLote.setTotalCost(batchRequestTest.getTotalCost());
            existingLote.setValidity(batchRequestTest.getValidity());
            existingLote.setDateOfPurchase(batchRequestTest.getDateOfPurchase());
            existingLote.setSupplierName(batchRequestTest.getSupplierName());

            // Salva as atualizações do lote no banco de dados usando o repositório
            batchRepositoryTest.save(existingLote);

            // Converte o lote atualizado para uma resposta e o retorna
            return batchConverterTest.convertBatchDomainToResponse(existingLote);

        } catch (InvalidDataException e) {
            throw new InvalidDataException("Não foi possível atualizar o lote de ID: " + id);
        }
    }

    // Método para excluir um lote pelo ID
    public void deleteBatch(String id) {
        try {
            // Tenta excluir o lote pelo ID usando o repositório
            batchRepositoryTest.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidDataException("Lote não encontrado através do ID: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidDataException("Erro ao deletar Lote com o ID: " + id + " - " + e.getMessage());
        }
    }

    // Métodos para obter informações sobre lotes criados de acordo com a quantidade de dias
    // ----------------------------------------------------------------------

    // Método para obter uma lista de lotes criados em um determinado período de dias
    public List<BatchDomainTest> getBatchesCreatedForTimePeriod(int duration) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusDays(duration);
        return batchRepositoryTest.findByDateOfPurchaseBetween(startDate, currentDate);
    }

    // Método para calcular o custo total dos lotes criados em um determinado período de dias
    public BigDecimal calculateTotalCostBatchesForTimePeriod(int duration) {
        List<BatchDomainTest> batchesCreatedForTimePeriod = getBatchesCreatedForTimePeriod(duration);
        BigDecimal totalCost = BigDecimal.ZERO;

        // Soma o custo total de cada lote da lista
        for (BatchDomainTest batch : batchesCreatedForTimePeriod) {
            totalCost = totalCost.add(batch.getTotalCost());
        }

        return totalCost;
    }
}
