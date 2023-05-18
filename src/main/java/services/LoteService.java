package services;

import mapping.LoteConverter;
import model.Lote.LoteDomain;
import model.Lote.LoteRequest;
import model.Material.MaterialDomain;
import org.springframework.stereotype.Service;
import repositories.LoteRepository;

import java.math.BigDecimal;

@Service
public class LoteService {

    private LoteConverter loteConverter;
    private LoteRepository loteRepository;

    public LoteDomain createLote(MaterialDomain materialDomain) {
        // Calculando o custo total com base nos dados do material
        BigDecimal calculatedTotalCost = calculateTotalCost(materialDomain);

        // Quantidade consumida iniciada em 0
        Double initialAmountConsumed = 0.0;

        // Criando o objeto LoteRequest com as informações calculadas e o MaterialDomain associado
        LoteRequest loteRequest = LoteRequest.builder()
                .amountConsumed(initialAmountConsumed)
                .totalCost(calculatedTotalCost)
                .materialDomain(materialDomain)
                .build();

        LoteDomain loteDomain = loteConverter.convertLoteRequestToDomain(loteRequest);

        return loteRepository.save(loteDomain);
    }

    private BigDecimal calculateTotalCost(MaterialDomain materialDomain) {
        BigDecimal quantity = BigDecimal.valueOf(materialDomain.getQuantity());
        return quantity.multiply(materialDomain.getCost());
    }
}
