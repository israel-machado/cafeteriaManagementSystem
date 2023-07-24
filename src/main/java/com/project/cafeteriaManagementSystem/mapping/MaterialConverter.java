package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.exceptions.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.Material.MaterialRequest;
import com.project.cafeteriaManagementSystem.model.Material.MaterialResponse;
import com.project.cafeteriaManagementSystem.model.Material.MaterialWithoutLoteRequest;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MaterialConverter {

    private final MaterialRepository materialRepository;
    private final LoteConverter loteConverter;

    public MaterialDomain convertMaterialRequestToDomain(MaterialRequest materialRequest) {

        return MaterialDomain.builder()
                .name(materialRequest.getName())
                .quantity(materialRequest.getQuantity())
                .unitMeasure(materialRequest.getUnitMeasure())
                .cost(materialRequest.getCost())
                .build();
    }

    public MaterialResponse convertMaterialDomainToResponse(MaterialDomain materialDomain) {
        return MaterialResponse.builder()
                .id(materialDomain.getId())
                .name(materialDomain.getName())
                .quantity(materialDomain.getQuantity())
                .unitMeasure(materialDomain.getUnitMeasure())
                .cost(materialDomain.getCost())
                .minimumStockQuantity(materialDomain.getMinimumStockQuantity())
                .loteResponseList(loteConverter.convertLoteDomainListToResponseList(materialDomain.getLoteDomainList()))
                .build();
    }

    public MaterialDomain convertMaterialResponseToDomain(MaterialResponse materialResponse) {

        return MaterialDomain.builder()
                .id(materialResponse.getId())
                .name(materialResponse.getName())
                .quantity(materialResponse.getQuantity())
                .unitMeasure(materialResponse.getUnitMeasure())
                .cost(materialResponse.getCost())
                .minimumStockQuantity(materialResponse.getMinimumStockQuantity())
                .build();
    }

    public MaterialDomain convertMaterialWOLoteRequestToDomain(MaterialWithoutLoteRequest materialRequest) {

        return MaterialDomain.builder()
                .name(materialRequest.getName())
                .quantity(materialRequest.getQuantity())
                .unitMeasure(materialRequest.getUnitMeasure())
                .cost(materialRequest.getCost())
                .build();
    }

    public MaterialDomain convertMaterialNameAndQuantityToDomain(String materialName, double quantity) {
        MaterialDomain materialDomain = materialRepository.findByName(materialName);

        if (materialDomain == null) {
            throw new InvalidMaterialDataException("Material não encontrado: " + materialName);
        }
        try {
            MaterialDomain materialWithQuantity = materialDomain.clone();
            materialWithQuantity.setQuantity(quantity);
            return materialWithQuantity;

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Erro ao clonar o objeto MaterialDomain: " + e.getMessage());
        }
    }

    // Lists

    public List<MaterialResponse> convertMaterialDomainListToResponseList(List<MaterialDomain> materialDomainList) {
        return materialDomainList.stream()
                .map(this::convertMaterialDomainToResponse)
                .collect(Collectors.toList());
    }

    public List<MaterialDomain> convertMaterialResponseListToDomainList(List<MaterialResponse> materialResponseList) {
        return materialResponseList.stream()
                .map(this::convertMaterialResponseToDomain)
                .collect(Collectors.toList());
    }
}
