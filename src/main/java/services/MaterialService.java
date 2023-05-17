package services;

import mapping.MaterialConverter;
import model.Material.MaterialDomain;
import model.Material.MaterialRequest;
import model.Material.MaterialResponse;
import org.springframework.stereotype.Service;
import repositories.MaterialRepository;

import java.math.BigDecimal;

@Service
public class MaterialService {

    @Service
    public class MaterialService {

        private MaterialConverter materialConverter;
        private MaterialRepository materialRepository;

        public MaterialResponse registerMaterial(MaterialRequest materialRequest) throws InvalidDataException, InsufficientStockException {
            // Validação dos dados
            validateMaterialRequest(materialRequest);

            // Cálculo do custo total
            BigDecimal custoTotal = calculateTotalCost(materialRequest);

            // Verificação de disponibilidade
            checkStockAvailability(materialRequest);

            // Convertendo a requisição para o domínio
            MaterialDomain materialDomain = materialConverter.convertMaterialRequestToDomain(materialRequest);
            materialDomain.setCustoTotal(custoTotal);

            // Salvando o material no banco de dados
            materialRepository.save(materialDomain);

            // Convertendo o domínio para a resposta
            return materialConverter.convertMaterialDomainToResponse(materialDomain);
        }

        private void validateMaterialRequest(MaterialRequest materialRequest) throws InvalidDataException {
            // Realize as validações necessárias nos dados do materialRequest
            // Por exemplo, verifique se os campos obrigatórios estão preenchidos,
            // se os valores estão corretos, etc. Se alguma validação falhar, lance uma exceção InvalidDataException com a mensagem adequada.
            // Exemplo:
            if (materialRequest.getNome() == null || materialRequest.getNome().isEmpty()) {
                throw new InvalidDataException("O nome do material é obrigatório");
            }
            // ...
        }

        private BigDecimal calculateTotalCost(MaterialRequest materialRequest) {
            // Realizar o cálculo do custo total com base na quantidade e custo unitário do material
            // Exemplo:
            BigDecimal quantidade = materialRequest.getQuantidade();
            BigDecimal custoUnitario = materialRequest.getCustoUnitario();
            return quantidade.multiply(custoUnitario);
        }

        private void checkStockAvailability(MaterialRequest materialRequest) throws InsufficientStockException {
            // Verificar a disponibilidade de estoque para a quantidade solicitada do material
            // Se não houver estoque suficiente, lance uma exceção InsufficientStockException com a mensagem adequada
            // Exemplo:
            BigDecimal quantidadeSolicitada = materialRequest.getQuantidade();
            BigDecimal estoqueDisponivel = getAvailableStock(materialRequest);
            if (quantidadeSolicitada.compareTo(estoqueDisponivel) > 0) {
                throw new InsufficientStockException("Estoque insuficiente para o material");
            }
        }

        private BigDecimal getAvailableStock(MaterialRequest materialRequest) {
            // Implementar a lógica para obter o estoque disponível do material
            // Pode envolver consultas ao banco de dados ou outras verificações
            // Retorne a quantidade disponível em BigDecimal
            // Exemplo:
            // BigDecimal estoqueDisponivel = ...;
            // return estoqueDisponivel;
        }
}
