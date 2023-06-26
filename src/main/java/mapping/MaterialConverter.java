package mapping;

import model.Lote.LoteDomain;
import model.Lote.LoteRequest;
import model.Lote.LoteResponse;
import model.Material.MaterialDomain;
import model.Material.MaterialRequest;
import model.Material.MaterialResponse;

import java.util.ArrayList;
import java.util.List;

public class MaterialConverter {

    public MaterialResponse convertMaterialDomainToResponse(MaterialDomain materialDomain) {
        List<LoteResponse> loteResponseList = convertLoteDomainListToResponseList(materialDomain.getLoteDomainList());

        return MaterialResponse.builder()
                .id(materialDomain.getId())
                .name(materialDomain.getName())
                .quantity(materialDomain.getQuantity())
                .unitMeasure(materialDomain.getUnitMeasure())
                .cost(materialDomain.getCost())
                .loteResponseList(loteResponseList)
                .build();
    }

    public MaterialDomain convertMaterialRequestToDomain(MaterialRequest materialRequest) {
        List<LoteDomain> loteDomainList = convertLoteRequestListToDomainList(materialRequest.getLoteRequestList());

        return MaterialDomain.builder()
                .name(materialRequest.getName())
                .quantity(materialRequest.getQuantity())
                .unitMeasure(materialRequest.getUnitMeasure())
                .cost(materialRequest.getCost())
                .loteDomainList(loteDomainList)
                .build();
    }

    // Lists

    private List<LoteResponse> convertLoteDomainListToResponseList(List<LoteDomain> loteDomainList) {
        List<LoteResponse> loteResponseList = new ArrayList<>();

        for (LoteDomain loteDomain : loteDomainList) {
            LoteResponse loteResponse = LoteResponse.builder()
                    .id(loteDomain.getId())
                    .amountConsumed(loteDomain.getAmountConsumed())
                    .totalCost(loteDomain.getTotalCost())
                    .validity(loteDomain.getValidity())
                    .build();

            loteResponseList.add(loteResponse);
        }

        return loteResponseList;
    }

    private List<LoteDomain> convertLoteRequestListToDomainList(List<LoteRequest> loteRequestList) {
        List<LoteDomain> loteDomainList = new ArrayList<>();

        for (LoteRequest loteRequest : loteRequestList) {
            LoteDomain loteDomain = LoteDomain.builder()
                    .amountConsumed(loteRequest.getAmountConsumed())
                    .totalCost(loteRequest.getTotalCost())
                    .validity(loteRequest.getValidity())
                    .build();

            loteDomainList.add(loteDomain);
        }

        return loteDomainList;
    }
}
