package com.project.cafeteriaManagementSystem.mapping;

import com.project.cafeteriaManagementSystem.model.material.MaterialDomainTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialRequestTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialResponseTest;
import com.project.cafeteriaManagementSystem.service.MaterialServiceTest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MaterialConverterTest {

    private final BatchConverterTest batchConverterTest;
    private final MaterialServiceTest materialServiceTest;

    // Método para converter uma requisição de material (MaterialRequest) em um objeto MaterialDomain
    public MaterialDomainTest convertMaterialRequestToDomain(MaterialRequestTest materialRequestTest) {
        return MaterialDomainTest.builder()
                .name(materialRequestTest.getName())
                .unitMeasure(materialRequestTest.getUnitMeasure())
                .build();
    }

    // Método para converter um objeto MaterialDomain em uma resposta de material (MaterialResponse)
    public MaterialResponseTest convertMaterialDomainToResponse(MaterialDomainTest materialDomainTest) {
        double stock = materialServiceTest.calculateStock(materialDomainTest);
        return MaterialResponseTest.builder()
                .id(materialDomainTest.getId())
                .name(materialDomainTest.getName())
                .stock(stock)
                .unitMeasure(materialDomainTest.getUnitMeasure())
                .minimumStockQuantity(materialDomainTest.getMinimumStockQuantity())
                .batchResponsesListTest(batchConverterTest.convertBatchDomainListToResponseList(materialDomainTest.getBatchDomainTestList()))
                .build();
    }

    // Método para converter uma lista de objetos MaterialDomain em uma lista de objetos MaterialResponse
    public List<MaterialResponseTest> convertMaterialDomainListToResponseList(List<MaterialDomainTest> materialDomainTestList) {
        return materialDomainTestList.stream()
                .map(this::convertMaterialDomainToResponse)
                .collect(Collectors.toList());
    }
}
