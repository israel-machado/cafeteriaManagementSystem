package com.project.cafeteriaManagementSystem.services;

import com.project.cafeteriaManagementSystem.exceptions.InsufficientMaterialStockException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidDataException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.mapping.LoteConverter;
import com.project.cafeteriaManagementSystem.model.Lote.LoteDomain;
import com.project.cafeteriaManagementSystem.model.Lote.LoteRequest;
import com.project.cafeteriaManagementSystem.model.Lote.LoteResponse;
import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.Material.MaterialRequest;
import com.project.cafeteriaManagementSystem.repository.LoteRepository;
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
public class LoteService {

    private final LoteRepository loteRepository;
    private final LoteConverter loteConverter;

    // Método para criar um novo lote associado a um material específico
    public LoteDomain createLote(MaterialRequest materialRequest, MaterialDomain materialDomain) {
        // Calcula o custo total do lote com base nos dados fornecidos no materialRequest
        BigDecimal calculatedTotalCost = calculateTotalCost(materialRequest);

        // Cria o objeto LoteDomain com as informações calculadas e o MaterialDomain associado
        LoteDomain loteDomain = LoteDomain.builder()
                .amountToBeConsumed(materialRequest.getQuantity())
                .totalCost(calculatedTotalCost)
                .validity(materialRequest.getLoteRequest().getValidity())
                .materialDomain(materialDomain)
                .build();

        // Salva o novo lote no banco de dados usando o repositório e o retorna
        return loteRepository.save(loteDomain);
    }

    // Método auxiliar para calcular o custo total do lote com base nos dados fornecidos
    private BigDecimal calculateTotalCost(MaterialRequest materialRequest) {
        BigDecimal quantity = BigDecimal.valueOf(materialRequest.getQuantity());
        BigDecimal totalCost = quantity.multiply(materialRequest.getCost());
        totalCost = totalCost.setScale(2, RoundingMode.HALF_UP);
        return totalCost;
    }

    // Método para consumir a quantidade especificada de um material a partir dos lotes associados
    public void consumeMaterialFromLote(MaterialDomain materialDomain, double consumedQuantity) {
        List<LoteDomain> loteDomainList = materialDomain.getLoteDomainList();

        if (loteDomainList != null && !loteDomainList.isEmpty()) {
            // Ordena a lista de lotes pelo critério da data de validade (do mais antigo para o mais recente)
            loteDomainList.sort(Comparator.comparing(LoteDomain::getValidity));

            for (LoteDomain loteAtual : loteDomainList) {
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
            loteRepository.saveAll(loteDomainList);
        }
    }

    // Métodos para obter informações de lotes do banco de dados

    // Método para obter uma lista de todos os lotes
    public List<LoteResponse> getAllLotes() {
        List<LoteDomain> loteDomainList = loteRepository.findAll();
        return loteConverter.convertLoteDomainListToResponseList(loteDomainList);
    }

    // Método para obter um lote pelo ID
    public LoteResponse getLoteById(String id) {
        LoteDomain loteDomain = loteRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Lote não encontrado pelo ID: " + id));

        return loteConverter.convertLoteDomainToResponse(loteDomain);
    }

    // Método para atualizar a validade de um lote pelo ID
    public LoteResponse updateLoteValidity(String id, LoteRequest loteRequest) {
        try {
            // Busca o lote pelo ID fornecido
            LoteDomain existingLote = loteRepository.findById(id)
                    .orElseThrow(() -> new InvalidDataException("Lote não encontrado pelo ID: " + id));

            // Atualiza a validade do lote com a nova data fornecida
            existingLote.setValidity(loteRequest.getValidity());

            // Salva as atualizações do lote no banco de dados usando o repositório
            loteRepository.save(existingLote);

            // Converte o lote atualizado para uma resposta e o retorna
            return loteConverter.convertLoteDomainToResponse(existingLote);

        } catch (InvalidDataException e) {
            throw new InvalidDataException("Não foi possível atualizar o lote de ID: " + id);
        }
    }

    // Método para excluir um lote pelo ID
    public void deleteLote(String id) {
        try {
            // Tenta excluir o lote pelo ID usando o repositório
            loteRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidMaterialDataException("Lote não encontrado através do ID: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidMaterialDataException("Erro ao deletar Lote com o ID: " + id + " - " + e.getMessage());
        }
    }

    // Métodos para obter informações sobre lotes criados nos últimos 30 dias

    // Método para obter uma lista de lotes criados nos últimos 30 dias
    public List<LoteDomain> getLotesCreatedLast30Days() {
        LocalDate currentDate = LocalDate.now();
        LocalDate thirtyDaysAgo = currentDate.minusDays(30);
        return loteRepository.findByDateCreatedBetween(thirtyDaysAgo, currentDate);
    }

    // Método para calcular o custo total dos lotes criados nos últimos 30 dias
    public BigDecimal calculateTotalCostLotesLast30Days() {
        List<LoteDomain> lotesCreatedLast30Days = getLotesCreatedLast30Days();
        BigDecimal totalCost = BigDecimal.ZERO;

        // Soma o custo total de cada lote da lista
        for (LoteDomain lote : lotesCreatedLast30Days) {
            totalCost = totalCost.add(lote.getTotalCost());
        }

        return totalCost;
    }
}
