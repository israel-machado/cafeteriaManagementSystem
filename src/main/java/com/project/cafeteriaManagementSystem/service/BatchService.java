package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.exception.InvalidDataException;
import com.project.cafeteriaManagementSystem.mapping.BatchConverter;
import com.project.cafeteriaManagementSystem.model.batch.BatchDomain;
import com.project.cafeteriaManagementSystem.model.batch.BatchRequest;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponse;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialRequest;
import com.project.cafeteriaManagementSystem.repository.BatchRepository;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
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
public class BatchService {

    private final MaterialRepository materialRepository;
    private final MaterialService materialService;
    private final BatchRepository batchRepository;
    private final BatchConverter batchConverter;

    // Método para criar um novo lote associado a um material específico
    public BatchResponse createBatch(BatchRequest batchRequest) {
        // Verificar se o batchRequest é nulo ou se o materialRequest é nulo
        if (batchRequest == null || batchRequest.getMaterialRequest() == null) {
            throw new IllegalArgumentException("A Requisição do Lote e do Material não podem ser nulas.");
        }

        // Procura um materialDomain através do nome da requisição
        MaterialDomain materialDomain = materialRepository.findByName(batchRequest.getMaterialRequest().getName());

        // Se o nome não retornar um objeto, é criado o material e salvo no DB
        if (materialDomain == null) {
            MaterialRequest materialRequest = batchRequest.getMaterialRequest();
            if (materialRequest.getName() == null || materialRequest.getName().isEmpty()) {
                throw new IllegalArgumentException("O nome da requisição do material não pode ser nulo.");
            }
            materialDomain = materialService.createMaterial(materialRequest);
            materialRepository.save(materialDomain);
        }

        // Calcula o custo total do lote com base nos dados fornecidos no materialRequest
        BigDecimal calculatedTotalCost = calculateTotalCost(batchRequest);

        // Cria o objeto BatchDomain com as informações calculadas e o MaterialDomain associado
        BatchDomain batchDomain = BatchDomain.builder()
                .initialAmount(batchRequest.getInitialAmount())
                .totalCost(calculatedTotalCost)
                .validity(batchRequest.getValidity())
                .dateOfPurchase(batchRequest.getDateOfPurchase())
                .supplierName(batchRequest.getSupplierName())
                .remainingAmount(batchRequest.getInitialAmount())
                .wasteAmount(0.0)
                .materialDomain(materialDomain)
                .build();

        // Salva o novo lote no banco de dados usando o repositório e o retorna
        batchDomain = batchRepository.save(batchDomain);

        // Converte para Response e retorna
        return batchConverter.convertBatchDomainToResponse(batchDomain);
    }

    // Método auxiliar para calcular o custo total do lote com base nos dados fornecidos
    private BigDecimal calculateTotalCost(BatchRequest batchRequest) {
        BigDecimal initialAmount = BigDecimal.valueOf(batchRequest.getInitialAmount());
        BigDecimal totalCost = batchRequest.getTotalCost();
        return totalCost.divide(initialAmount, 2, RoundingMode.HALF_UP);
    }

    // Método para consumir a quantidade especificada de um material a partir dos lotes associados
    public void consumeAmountFromBatch(MaterialDomain materialDomain, double quantityToConsume) {
        List<BatchDomain> batchDomainList = materialDomain.getBatchDomainList();
        Collections.sort(batchDomainList); // Ordena a lista de lotes por validade //TODO sort direto da base

        for (BatchDomain batch : batchDomainList) {
            double availableQuantity = batch.getRemainingAmount();

            if (availableQuantity >= quantityToConsume) {
                // Caso a quantidade disponível no lote seja suficiente para atender a quantidade a consumir
                batch.setRemainingAmount(availableQuantity - quantityToConsume);
                batchRepository.save(batch);

                // Sai do loop, pois toda a quantidade foi consumida
                break;
            } else {
                // Reduz a quantidade a ser consumida pelo que foi consumido deste lote
                quantityToConsume -= availableQuantity;

                // Como zerou a quantidade restante, coloca o remaining como 0 e salva o lote
                batch.setRemainingAmount(0.0);
                batchRepository.save(batch);
            }
        }
    }

    // Método para calcular o custo da quantidade especificada de um material a partir dos lotes associados
    public BigDecimal calculateCostForQuantityFromBatch(MaterialDomain materialDomain, double quantityToConsume) {
        BigDecimal totalCost = BigDecimal.ZERO;
        List<BatchDomain> batchDomainList = materialDomain.getBatchDomainList();
        Collections.sort(batchDomainList); // Ordena a lista de lotes por validade

        for (BatchDomain batch : batchDomainList) {
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
    public List<BatchResponse> getAllBatches() {
        List<BatchDomain> batchDomainList = batchRepository.findAll();
        return batchConverter.convertBatchDomainListToResponseList(batchDomainList);
    }

    // Método para obter um lote pelo ID
    public BatchResponse getBatchById(String id) {
        BatchDomain batchDomain = batchRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Lote não encontrado pelo ID: " + id));

        return batchConverter.convertBatchDomainToResponse(batchDomain);
    }

    // Método para atualizar um lote pelo ID
    public BatchResponse updateBatch(String id, BatchRequest batchRequest) {
        try {
            // Busca o lote pelo ID fornecido
            BatchDomain existingLote = batchRepository.findById(id)
                    .orElseThrow(() -> new InvalidDataException("Lote não encontrado pelo ID: " + id));

            // Atualiza o lote com as informações fornecidas
            existingLote.setInitialAmount(batchRequest.getInitialAmount());
            existingLote.setTotalCost(batchRequest.getTotalCost());
            existingLote.setValidity(batchRequest.getValidity());
            existingLote.setDateOfPurchase(batchRequest.getDateOfPurchase());
            existingLote.setSupplierName(batchRequest.getSupplierName());

            // Salva as atualizações do lote no banco de dados usando o repositório
            batchRepository.save(existingLote);

            // Converte o lote atualizado para uma resposta e o retorna
            return batchConverter.convertBatchDomainToResponse(existingLote);

        } catch (InvalidDataException e) {
            throw new InvalidDataException("Não foi possível atualizar o lote de ID: " + id);
        }
    }

    // Método para excluir um lote pelo ID
    public void deleteBatch(String id) {
        try {
            // Tenta excluir o lote pelo ID usando o repositório
            batchRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidDataException("Lote não encontrado através do ID: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidDataException("Erro ao deletar Lote com o ID: " + id + " - " + e.getMessage());
        }
    }

    // Métodos para obter informações sobre lotes criados de acordo com a quantidade de dias
    // ----------------------------------------------------------------------

    // Método para obter uma lista de lotes criados em um determinado período de dias
    public List<BatchDomain> getBatchesCreatedForTimePeriod(int duration) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusDays(duration);
        return batchRepository.findByDateOfPurchaseBetween(startDate, currentDate);
    }

    // Método para calcular o custo total dos lotes criados em um determinado período de dias
    public BigDecimal calculateTotalCostBatchesForTimePeriod(int duration) {
        List<BatchDomain> batchesCreatedForTimePeriod = getBatchesCreatedForTimePeriod(duration);
        BigDecimal totalCost = BigDecimal.ZERO;

        // Soma o custo total de cada lote da lista
        for (BatchDomain batch : batchesCreatedForTimePeriod) {
            totalCost = totalCost.add(batch.getTotalCost());
        }

        return totalCost;
    }
}
