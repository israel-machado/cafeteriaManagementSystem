package mapping;

import model.Material.MaterialDomain;
import model.Material.MaterialRequest;
import model.Material.MaterialResponse;

import java.util.List;
import java.util.stream.Collectors;

public class MaterialConverter {

    private LoteConverter loteConverter;

    public MaterialResponse convertMaterialDomainToResponse(MaterialDomain materialDomain) {
        return MaterialResponse.builder()
                .id(materialDomain.getId())
                .name(materialDomain.getName())
                .quantity(materialDomain.getQuantity())
                .unitMeasure(materialDomain.getUnitMeasure())
                .cost(materialDomain.getCost())
                .loteResponseList(loteConverter.convertLoteDomainListToResponseList(materialDomain.getLoteDomainList()))
                .build();
    }

    public MaterialDomain convertMaterialRequestToDomain(MaterialRequest materialRequest) {

        return MaterialDomain.builder()
                .name(materialRequest.getName())
                .quantity(materialRequest.getQuantity())
                .unitMeasure(materialRequest.getUnitMeasure())
                .cost(materialRequest.getCost())
                .build();
    }

    // Lists

    public List<MaterialResponse> convertMaterialDomainListToResponseList(List<MaterialDomain> materialDomainList) {
        return materialDomainList.stream()
                .map(this::convertMaterialDomainToResponse)
                .collect(Collectors.toList());
    }


}
