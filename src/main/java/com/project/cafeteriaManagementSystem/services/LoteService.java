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

    public LoteDomain createLote(MaterialRequest materialRequest, MaterialDomain materialDomain) {

        // Calculando o custo total com base nos dados do material
        BigDecimal calculatedTotalCost = calculateTotalCost(materialRequest);

        // Criando o objeto LoteDomain com as informações calculadas e o MaterialDomain associado
        LoteDomain loteDomain = LoteDomain.builder()
                .amountToBeConsumed(materialRequest.getQuantity())
                .totalCost(calculatedTotalCost)
                .validity(materialRequest.getLoteRequest().getValidity())
                .materialDomain(materialDomain)
                .build();

        return loteRepository.save(loteDomain);
    }

    private BigDecimal calculateTotalCost(MaterialRequest materialRequest) {
        BigDecimal quantity = BigDecimal.valueOf(materialRequest.getQuantity());
        BigDecimal totalCost = quantity.multiply(materialRequest.getCost());
        totalCost = totalCost.setScale(2, RoundingMode.HALF_UP);
        return totalCost;
    }

    public void consumeMaterialFromLote(MaterialDomain materialDomain, double consumedQuantity) {
        List<LoteDomain> loteDomainList = materialDomain.getLoteDomainList();

        if (loteDomainList != null && !loteDomainList.isEmpty()) {
            // Ordenar a lista de lotes pelo critério da data de validade (do mais antigo para o recente)
            loteDomainList.sort(Comparator.comparing(LoteDomain::getValidity));

            for (LoteDomain loteAtual : loteDomainList) {
                double amountToBeConsumed = loteAtual.getAmountToBeConsumed();

                if (amountToBeConsumed > 0 && consumedQuantity > 0) {
                    double loteConsumedQuantity = Math.min(amountToBeConsumed, consumedQuantity);
                    loteAtual.setRemainingQuantity(loteAtual.getRemainingQuantity() - loteConsumedQuantity);
                    consumedQuantity -= loteConsumedQuantity;
                }
            }

            // Verificar se ainda há quantidade a ser consumida e, se sim, lançar exceção de estoque insuficiente
            if (consumedQuantity > 0) {
                throw new InsufficientMaterialStockException("Estoque insuficiente para o material: " + materialDomain.getName());
            }

            // Salvar as atualizações dos lotes no DB
            loteRepository.saveAll(loteDomainList);
        }
    }

    // GET ALL
    public List<LoteResponse> getAllLotes() {
        List<LoteDomain> loteDomainList = loteRepository.findAll();
        return loteConverter.convertLoteDomainListToResponseList(loteDomainList);
    }

    // GET BY ID
    public LoteResponse getLoteById(String id) {
        LoteDomain loteDomain = loteRepository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Lote não encontrado pelo ID: " + id));

        return loteConverter.convertLoteDomainToResponse(loteDomain);
    }

    // UPDATE
    public LoteResponse updateLoteValidity(String id, LoteRequest loteRequest) {
        try {
            LoteDomain existingLote = loteRepository.findById(id)
                    .orElseThrow(() -> new InvalidDataException("Lote não encontrado pelo ID: " + id));
            LocalDate newValidaty = loteRequest.getValidity();
            existingLote.setValidity(newValidaty);

           loteRepository.save(existingLote);

           return loteConverter.convertLoteDomainToResponse(existingLote);

        } catch (InvalidDataException e) {
            throw new InvalidDataException("Não foi possível atualizar o lote de ID: " + id);
        }
    }

    // DELETE
    public void deleteLote(String id) {
        try {
            loteRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidMaterialDataException("Lote não encontrado através do ID: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidMaterialDataException("Erro ao deletar Lote com o ID: " + id + " - " + e.getMessage());
        }
    }

    // Lotes criados nos últimos 30 dias
    public List<LoteDomain> getLotesCreatedLast30Days() {
        LocalDate currentDate = LocalDate.now();
        LocalDate thirtyDaysAgo = currentDate.minusDays(30);
        return loteRepository.findByDateCreatedBetween(thirtyDaysAgo, currentDate);
    }

    // Custo dos lotes criados nos últimos 30 dias
    public BigDecimal calculateTotalCostLotesLast30Days() {
        List<LoteDomain> lotesCreatedLast30Days = getLotesCreatedLast30Days();
        BigDecimal totalCost = BigDecimal.ZERO;

        for (LoteDomain lote : lotesCreatedLast30Days) {
            totalCost = totalCost.add(lote.getTotalCost());
        }

        return totalCost;
    }
}
