package mapping;

import model.Lote.LoteDomain;
import model.Lote.LoteRequest;
import model.Lote.LoteResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LoteConverter {

    public LoteDomain convertLoteRequestToDomain(LoteRequest loteRequest) {
        return LoteDomain.builder()
                .amountConsumed(loteRequest.getAmountConsumed())
                .totalCost(loteRequest.getTotalCost())
                .validity(loteRequest.getValidity())
                .materialDomain(loteRequest.getMaterialDomain())
                .build();
    }

    public LoteResponse convertLoteDomainToResponse(LoteDomain loteDomain) {
        return LoteResponse.builder()
                .id(loteDomain.getId())
                .amountConsumed(loteDomain.getAmountConsumed())
                .totalCost(loteDomain.getTotalCost())
                .validity(loteDomain.getValidity())
                .build();
    }

    //Lists

    public List<LoteResponse> convertLoteDomainListToResponseList(List<LoteDomain> loteDomainList) {
        return loteDomainList.stream()
                .map(this::convertLoteDomainToResponse)
                .collect(Collectors.toList());
    }
}
