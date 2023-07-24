package com.project.cafeteriaManagementSystem.services;

import com.project.cafeteriaManagementSystem.exceptions.InsufficientMaterialStockException;
import com.project.cafeteriaManagementSystem.exceptions.InsufficientStockException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidDataException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.mapping.LoteConverter;
import com.project.cafeteriaManagementSystem.mapping.MaterialConverter;
import com.project.cafeteriaManagementSystem.model.Lote.LoteDomain;
import com.project.cafeteriaManagementSystem.model.Lote.LoteResponse;
import com.project.cafeteriaManagementSystem.model.Material.*;
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
    private final LoteService loteService;
    private final LoteConverter loteConverter;

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
    public MaterialResponse insertMaterial(MaterialRequest materialRequest) {
        try {
            // Verifica se o material já existe no banco de dados pelo nome
            MaterialDomain existingMaterial = materialRepository.findByName(materialRequest.getName());

            if (existingMaterial != null) {
                // Se o material já existe, atualiza a quantidade no estoque
                double currentQuantity = existingMaterial.getQuantity();
                double newQuantity = materialRequest.getQuantity() + currentQuantity;
                existingMaterial.setQuantity(newQuantity);
            } else {
                // Se o material não existe, cria um novo material
                existingMaterial = materialConverter.convertMaterialRequestToDomain(materialRequest);
            }

            // Salva o material no banco de dados
            existingMaterial = materialRepository.save(existingMaterial);

            // Criação de um lote para o material
            LoteDomain loteDomain = loteService.createLote(materialRequest, existingMaterial);

            // Associação do lote ao material
            List<LoteDomain> loteDomainList = existingMaterial.getLoteDomainList();
            if (loteDomainList == null) {
                loteDomainList = new ArrayList<>();
                existingMaterial.setLoteDomainList(loteDomainList);
            }
            loteDomainList.add(loteDomain);

            // Atualizando o material no banco de dados com a associação ao lote
            existingMaterial = materialRepository.save(existingMaterial);

            // Convertendo o domínio para a resposta
            return materialConverter.convertMaterialDomainToResponse(existingMaterial);

        } catch (InvalidDataException e) {
            // Se os dados forem inválidos, lance uma exceção personalizada
            throw new InvalidMaterialDataException(e.getMessage());
        } catch (InsufficientStockException e) {
            // Se houver estoque insuficiente, lance uma exceção personalizada
            throw new InsufficientMaterialStockException(e.getMessage());
        }
    }

    // Método para inserir um novo material sem lote
    public MaterialResponse insertMaterialWithOutLote(MaterialWithoutLoteRequest materialRequest) {
        try {
            // Verifica se o material já existe no banco de dados pelo nome
            MaterialDomain existingMaterial = materialRepository.findByName(materialRequest.getName());

            if (existingMaterial != null) {
                // Se o material já existe, lança uma exceção informando que já existe no banco de dados
                throw new InvalidMaterialDataException("Material já existe no banco de dados.");
            } else {
                // Se o material não existe, converte a requisição para o domínio e salva no banco de dados
                existingMaterial = materialConverter.convertMaterialWOLoteRequestToDomain(materialRequest);
            }

            existingMaterial = materialRepository.save(existingMaterial);

            // Convertendo o domínio para a resposta
            return materialConverter.convertMaterialDomainToResponse(existingMaterial);

        } catch (InvalidDataException e) {
            // Se os dados forem inválidos, lance uma exceção personalizada
            throw new InvalidMaterialDataException(e.getMessage());
        }
    }

    // Método para obter os materiais que estão prestes a vencer
    public List<MaterialResponse> getExpiringMaterials(int daysToExpiration) {
        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDateThreshold = currentDate.plusDays(daysToExpiration);

        // Obtém todos os materiais do repositório
        List<MaterialDomain> materials = materialRepository.findAll();
        List<MaterialResponse> expiringMaterials = new ArrayList<>();

        for (MaterialDomain material : materials) {
            List<LoteDomain> lotes = material.getLoteDomainList();
            if (lotes != null && !lotes.isEmpty()) {
                List<LoteResponse> expiringLotes = new ArrayList<>();
                for (LoteDomain lote : lotes) {
                    if (lote.getValidity().isBefore(expirationDateThreshold)) {
                        // Se o lote está prestes a vencer, adiciona na lista de lotes expirando
                        expiringLotes.add(loteConverter.convertLoteDomainToResponse(lote));
                    }
                }
                if (!expiringLotes.isEmpty()) {
                    // Se há lotes expirando, cria uma resposta para o material e adiciona na lista
                    MaterialResponse materialResponse = materialConverter.convertMaterialDomainToResponse(material);
                    materialResponse.setLoteResponseList(expiringLotes);
                    expiringMaterials.add(materialResponse);
                }
            }
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
        List<MaterialDomain> materials = materialRepository.findAll();
        List<MaterialResponse> materialsWithLowStock = new ArrayList<>();

        for (MaterialDomain material : materials) {
            // Verifica se a quantidade atual do material é menor que a quantidade mínima de estoque
            if (material.getQuantity() < material.getMinimumStockQuantity()) {
                // Se a quantidade está baixa, cria uma resposta para o material e adiciona na lista
                MaterialResponse materialResponse = materialConverter.convertMaterialDomainToResponse(material);
                materialsWithLowStock.add(materialResponse);
            }
        }

        return materialsWithLowStock;
    }
}