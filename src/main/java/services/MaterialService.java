package services;

import mapping.MaterialConverter;
import model.Lote.LoteDomain;
import model.Lote.LoteRequest;
import model.Material.MaterialDomain;
import model.Material.MaterialRequest;
import model.Material.MaterialResponse;
import org.springframework.stereotype.Service;
import repositories.MaterialRepository;
import services.exceptions.InsufficientStockException;
import services.exceptions.InvalidDataException;

import java.math.BigDecimal;

@Service
public class MaterialService {

        private MaterialConverter materialConverter;
        private MaterialRepository materialRepository;
        private LoteService loteService;

        public MaterialResponse registerMaterial(MaterialRequest materialRequest) {
            // Validação dos dados
            validateMaterialRequest(materialRequest);

            // Convertendo a requisição para o domínio
            MaterialDomain materialDomain = materialConverter.convertMaterialRequestToDomain(materialRequest);

            // Salvando o material no banco de dados
            materialDomain = materialRepository.save(materialDomain);

            // Criação de um lote para o material
            LoteDomain loteDomain = loteService.createLote(materialDomain);

            // Associando o lote ao material
            materialDomain.setLoteDomain(loteDomain);

            // Atualizando o material no banco de dados com a associação ao lote
            materialDomain = materialRepository.save(materialDomain);

            // Convertendo o domínio para a resposta
            return materialConverter.convertMaterialDomainToResponse(materialDomain);
        }

        private void validateMaterialRequest(MaterialRequest materialRequest) {
            // Realize as validações necessárias nos dados do materialRequest
            // Por exemplo, verifique se os campos obrigatórios estão preenchidos,
            // se os valores estão corretos, etc. Se alguma validação falhar, lance uma exceção InvalidDataException com a mensagem adequada.
            if (materialRequest.getName() == null || materialRequest.getName().isEmpty()) {
                throw new InvalidDataException("O nome do material é obrigatório");
            }
            // ...
        }

        private void checkStockAvailability(MaterialRequest materialRequest) {
            // Verificar a disponibilidade de estoque para a quantidade solicitada do material
            // Se não houver estoque suficiente, lance uma exceção InsufficientStockException com a mensagem adequada
            BigDecimal wantedQuantity = BigDecimal.valueOf(materialRequest.getQuantity());
            BigDecimal availableStock = getAvailableStock(materialRequest);
            if (wantedQuantity.compareTo(availableStock) > 0) {
                throw new InsufficientStockException("Estoque insuficiente para o material");
            }
        }

        private BigDecimal getAvailableStock(MaterialRequest materialRequest) {
            // Implementar a lógica para obter o estoque disponível do material
            // Pode envolver consultas ao banco de dados ou outras verificações
            // Retorne a quantidade disponível em BigDecimal
            // BigDecimal estoqueDisponivel = ...;
            // return estoqueDisponivel;
        }
}