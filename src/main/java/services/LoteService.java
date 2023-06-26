package services;

import mapping.LoteConverter;
import model.Lote.LoteDomain;
import model.Lote.LoteRequest;
import model.Material.MaterialDomain;
import org.springframework.stereotype.Service;
import repositories.LoteRepository;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Service
public class LoteService {

    private LoteConverter loteConverter;
    private LoteRepository loteRepository;

    public LoteDomain createLote(MaterialDomain materialDomain) {
        // Calculando o custo total com base nos dados do material
        BigDecimal calculatedTotalCost = calculateTotalCost(materialDomain);

        // Quantidade consumida iniciada em 0
        Double initialAmountConsumed = 0.0;

        // Criando a validade inicial do lote (exemplo: 1 ano a partir da data atual)
        Date initialValidity = calculateInitialValidity();

        // Criando o objeto LoteDomain com as informações calculadas e o MaterialDomain associado
        LoteDomain loteDomain = LoteDomain.builder()
                .amountConsumed(initialAmountConsumed)
                .totalCost(calculatedTotalCost)
                .validity(initialValidity)
                .materialDomain(materialDomain)
                .build();

        return loteRepository.save(loteDomain);
    }

    private BigDecimal calculateTotalCost(MaterialDomain materialDomain) {
        BigDecimal quantity = BigDecimal.valueOf(materialDomain.getQuantity());
        return quantity.multiply(materialDomain.getCost());
    }

    private Date calculateInitialValidity() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1); // Exemplo: adicionando 1 ano à data atual
        return calendar.getTime();
    }
}
