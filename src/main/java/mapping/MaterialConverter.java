package mapping;

import model.Material.MaterialDomain;
import model.Material.MaterialRequest;
import model.Material.MaterialResponse;

public class MaterialConverter {

    public MaterialResponse convertMaterialDomainToResponse(MaterialDomain materialDomain) {
        return MaterialResponse.builder()
                .id(materialDomain.getId())
                .name(materialDomain.getName())
                .quantity(materialDomain.getQuantity())
                .unitMeasure(materialDomain.getUnitMeasure())
                .validity(materialDomain.getValidity())
                .cost(materialDomain.getCost())
                .loteDomain(materialDomain.getLoteDomain())
                .build();
    }

    public MaterialDomain convertMaterialRequestToDomain(MaterialRequest materialRequest) {
        return MaterialDomain.builder()
                .name(materialRequest.getName())
                .quantity(materialRequest.getQuantity())
                .unitMeasure(materialRequest.getUnitMeasure())
                .validity(materialRequest.getValidity())
                .cost(materialRequest.getCost())
                .loteDomain(materialRequest.getLoteDomain())
                .build();
    }
}
