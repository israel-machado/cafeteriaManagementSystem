package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.exception.InsufficientMaterialStockException;
import com.project.cafeteriaManagementSystem.exception.InvalidDataException;
import com.project.cafeteriaManagementSystem.exception.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.mapping.BatchConverter;
import com.project.cafeteriaManagementSystem.model.batch.BatchDomain;
import com.project.cafeteriaManagementSystem.model.batch.BatchRequest;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponse;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialRequest;
import com.project.cafeteriaManagementSystem.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final BatchConverter batchConverter;

    // Método para criar um novo lote associado a um material específico
    public BatchDomain createBatch(MaterialRequest materialRequest, MaterialDomain materialDomain) {
        // Calcula o custo total do lote com base nos dados fornecidos no materialRequest
        BigDecimal calculatedTotalCost = calculateTotalCost(materialRequest);

        // Cria o objeto LoteDomain com as informações calculadas e o MaterialDomain associado
        BatchDomain batchDomain = BatchDomain.builder()
                .amountToBeConsumed(materialRequest.getQuantity())
                .totalCost(calculatedTotalCost)
                .validity(materialRequest.getBatchRequest().getValidity())
                .materialDomain(materialDomain)
                .build();

        // Salva o novo lote no banco de dados usando o repositório e o retorna
        return batchRepository.save(batchDomain);
    }

    // Método auxiliar para calcular o custo total do lote com base nos dados fornecidos
    private BigDecimal calculateTotalCost(MaterialRequest materialRequest) {
        BigDecimal quantity = BigDecimal.valueOf(materialRequest.getQuantity());
        BigDecimal totalCost = quantity.multiply(materialRequest.getCost());
        totalCost = totalCost.setScale(2, RoundingMode.HALF_UP);
        return totalCost;
    }

    // Método para consumir a quantidade especificada de um material a partir dos lotes associados
    public void consumeMaterialFromBatch(MaterialDomain materialDomain, double consumedQuantity) {
        List<BatchDomain> batchDomainList = materialDomain.getBatchDomainList();

        if (batchDomainList != null && !batchDomainList.isEmpty()) {
            // Ordena a lista de lotes pelo critério da data de validade (do mais antigo para o mais recente)
            batchDomainList.sort(Comparator.comparing(BatchDomain::getValidity));

            for (BatchDomain loteAtual : batchDomainList) {
                double amountToBeConsumed = loteAtual.getAmountToBeConsumed();

                if (amountToBeConsumed > 0 && consumedQuantity > 0) {
                    // Calcula a quantidade de lote a ser consumida para o lote atual
                    double loteConsumedQuantity = Math.min(amountToBeConsumed, consumedQuantity);
                    // Atualiza a quantidade restante no lote atual
                    loteAtual.setRemainingQuantity(loteAtual.getRemainingQuantity() - loteConsumedQuantity);
                    // Atualiza a quantidade total a ser consumida
                    consumedQuantity -= loteConsumedQuantity;
                }
            }

            // Verifica se ainda há quantidade a ser consumida e, se sim, lança uma exceção de estoque insuficiente
            if (consumedQuantity > 0) {
                throw new InsufficientMaterialStockException("Estoque insuficiente para o material: " + materialDomain.getName());
            }

            // Salva as atualizações dos lotes no banco de dados usando o repositório
            batchRepository.saveAll(batchDomainList);
        }
    }

    // Métodos para obter informações de lotes do banco de dados

    // Método para obter uma lista de todos os lotes
    public List<BatchResponse> getAllBatches() {
        List<BatchDomain> batchDomainList = batchRepository.findAll();
        return batchConverter.convertLoteDomainListToResponseList(batchDomainList);
    }

    // Método para obter um lote pelo ID
    public BatchResponse getBatchById(String id) {
        BatchDomain batchDomain = batchRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Lote não encontrado pelo ID: " + id));

        return batchConverter.convertLoteDomainToResponse(batchDomain);
    }

    // Método para atualizar a validade de um lote pelo ID
    public BatchResponse updateBatchValidity(String id, BatchRequest batchRequest) {
        try {
            // Busca o lote pelo ID fornecido
            BatchDomain existingLote = batchRepository.findById(id)
                    .orElseThrow(() -> new InvalidDataException("Lote não encontrado pelo ID: " + id));

            // Atualiza a validade do lote com a nova data fornecida
            existingLote.setValidity(batchRequest.getValidity());

            // Salva as atualizações do lote no banco de dados usando o repositório
            batchRepository.save(existingLote);

            // Converte o lote atualizado para uma resposta e o retorna
            return batchConverter.convertLoteDomainToResponse(existingLote);

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
            throw new InvalidMaterialDataException("Lote não encontrado através do ID: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidMaterialDataException("Erro ao deletar Lote com o ID: " + id + " - " + e.getMessage());
        }
    }

    // Métodos para obter informações sobre lotes criados nos últimos 30 dias

    // Método para obter uma lista de lotes criados nos últimos 30 dias
    public List<BatchDomain> getBatchesCreatedLast30Days() {
        LocalDate currentDate = LocalDate.now();
        LocalDate thirtyDaysAgo = currentDate.minusDays(30);
        return batchRepository.findByDateCreatedBetween(thirtyDaysAgo, currentDate);
    }

    // Método para calcular o custo total dos lotes criados nos últimos 30 dias
    public BigDecimal calculateTotalCostBatchesLast30Days() {
        List<BatchDomain> lotesCreatedLast30Days = getBatchesCreatedLast30Days();
        BigDecimal totalCost = BigDecimal.ZERO;

        // Soma o custo total de cada lote da lista
        for (BatchDomain lote : lotesCreatedLast30Days) {
            totalCost = totalCost.add(lote.getTotalCost());
        }

        return totalCost;
    }
}
