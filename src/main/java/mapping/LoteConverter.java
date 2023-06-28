package mapping;

import model.Lote.LoteDomain;
import model.Lote.LoteRequest;
import model.Lote.LoteResponse;

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
                .materialDomain(loteDomain.getMaterialDomain())
                .build();
    }
}
