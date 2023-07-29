package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialRequest;
import com.project.cafeteriaManagementSystem.model.material.MaterialResponse;
import com.project.cafeteriaManagementSystem.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MaterialConverter {

    private final BatchConverter batchConverter;
    private final MaterialService materialService;

    // Método para converter uma requisição de material (MaterialRequest) em um objeto MaterialDomain
    public MaterialDomain convertMaterialRequestToDomain(MaterialRequest materialRequest) {
        return MaterialDomain.builder()
                .name(materialRequest.getName())
                .unitMeasure(materialRequest.getUnitMeasure())
                .build();
    }

    // Método para converter um objeto MaterialDomain em uma resposta de material (MaterialResponse)
    public MaterialResponse convertMaterialDomainToResponse(MaterialDomain materialDomain) {
        double stock = materialService.calculateStock(materialDomain);
        return MaterialResponse.builder()
                .id(materialDomain.getId())
                .name(materialDomain.getName())
                .stock(stock)
                .unitMeasure(materialDomain.getUnitMeasure())
                .minimumStockQuantity(materialDomain.getMinimumStockQuantity())
                .batchResponsesList(batchConverter.convertBatchDomainListToResponseList(materialDomain.getBatchDomainList()))
                .build();
    }

    // Método para converter uma lista de objetos MaterialDomain em uma lista de objetos MaterialResponse
    public List<MaterialResponse> convertMaterialDomainListToResponseList(List<MaterialDomain> materialDomainList) {
        return materialDomainList.stream()
                .map(this::convertMaterialDomainToResponse)
                .collect(Collectors.toList());
    }
}
