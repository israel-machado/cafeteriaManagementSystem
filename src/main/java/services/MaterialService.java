package services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import exceptions.InsufficientMaterialStockException;
import exceptions.InsufficientStockException;
import exceptions.InvalidDataException;
import exceptions.InvalidMaterialDataException;
import mapping.MaterialConverter;
import model.Lote.LoteDomain;
import model.Lote.LoteRequest;
import model.Material.MaterialDomain;
import model.Material.MaterialRequest;
import model.Material.MaterialResponse;
import org.bson.Document;
import org.springframework.stereotype.Service;
import repositories.MaterialRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class MaterialService {

        private MaterialConverter materialConverter;
        private MaterialRepository materialRepository;
        private LoteService loteService;

        public MaterialResponse registerMaterial(MaterialRequest materialRequest) {
            try {
                // Validação dos dados
                validateMaterialRequest(materialRequest);

                // Convertendo a requisição para o domínio
                MaterialDomain materialDomain = materialConverter.convertMaterialRequestToDomain(materialRequest);

                // Salvando o material no banco de dados
                materialDomain = materialRepository.save(materialDomain);

                // Criação de um lote para o material
                LoteDomain loteDomain = loteService.createLote(materialRequest, materialDomain);

                // Associação do lote ao material
                materialDomain.getLoteDomainList().add(loteDomain);

                // Atualizando o material no banco de dados com a associação ao lote
                materialDomain = materialRepository.save(materialDomain);

                // Convertendo o domínio para a resposta
                return materialConverter.convertMaterialDomainToResponse(materialDomain);

            } catch (InvalidDataException e) {
                // Se os dados forem inválidos, lance uma exceção personalizada
                throw new InvalidMaterialDataException(e.getMessage());
            } catch (InsufficientStockException e) {
                // Se houver estoque insuficiente, lance uma exceção personalizada
                throw new InsufficientMaterialStockException(e.getMessage());
            }
        }

        private void validateMaterialRequest(MaterialRequest materialRequest) {
            // Realize as validações necessárias nos dados do materialRequest
            // Verifique se os campos obrigatórios estão preenchidos,
            // Se os valores estão corretos, etc. Se alguma validação falhar, lance uma exceção InvalidDataException com a mensagem adequada.

            // Verificar se o nome do material está preenchido ----- NOME
            if (materialRequest.getName() == null || materialRequest.getName().isEmpty()) {
                throw new InvalidDataException("O nome do material é obrigatório");
            }

            // Verificar se o material já existe pelo nome --- IF EXISTS
            if (materialRepository.existsByName(materialRequest.getName())) {
                throw new InvalidDataException("O material já existe");
            }

            // Verificar se a quantidade do material é válida --- QUANTIDADE
            if (materialRequest.getQuantity() == null || materialRequest.getQuantity() <= 0) {
                throw new InvalidDataException("A quantidade do material deve ser maior que zero");
            }

            // Verificar se a unidade de medida do material está preenchida --- UNIDADE DE MEDIDA
            if (materialRequest.getUnitMeasure() == null || materialRequest.getUnitMeasure().isEmpty()) {
                throw new InvalidDataException("A unidade de medida do material é obrigatória");
            }

            // Verificar se o custo do material é válido --- CUSTO
            if (materialRequest.getCost() == null || materialRequest.getCost().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidDataException("O custo do material deve ser maior que zero");
            }

            // Verificar se as informações do lote estão preenchidas corretamente, se aplicável --- LOTE
            LoteRequest loteRequest = materialRequest.getLoteRequest();

            // Verificar se a data de validade do lote é válida
            if (loteRequest.getValidity() == null) {
                throw new InvalidDataException("A data de validade do lote é obrigatória");
            }

            // Obter a data atual
            LocalDate currentDate = LocalDate.now();

            // Converter a data de validade do lote para LocalDate
            LocalDate expirationDate = loteRequest.getValidity().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Calcular a diferença em dias entre a data atual e a data de validade
            long daysUntilExpiration = ChronoUnit.DAYS.between(currentDate, expirationDate);

            // Verificar se a diferença é menor que 7 dias
            if (daysUntilExpiration < 7) {
                throw new InvalidDataException("A data de validade é menor que uma semana.");
            }
        }

        private void checkStockAvailability(MaterialRequest materialRequest) {
//            // Verificar a disponibilidade de estoque para a quantidade solicitada do material
//            // Se não houver estoque suficiente, lance uma exceção InsufficientStockException com a mensagem adequada
//            BigDecimal wantedQuantity = BigDecimal.valueOf(materialRequest.getQuantity());
//            BigDecimal availableStock = getAvailableStock(materialRequest);
//            if (wantedQuantity.compareTo(availableStock) > 0) {
//                throw new InsufficientStockException("Estoque insuficiente para o material");
//            }
        }

        private long getAvailableStock(MaterialRequest materialRequest) {
            String materialName = materialRequest.getName();

            // Criar a conexão com o MongoDB
            try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
                // Obter a referência para o banco de dados desejado
                MongoDatabase database = mongoClient.getDatabase("nome_do_banco_de_dados");

                // Obter a referência para a coleção de materiais
                MongoCollection<Document> collection = database.getCollection("nome_da_colecao_de_materiais");

                // Criar o filtro para buscar o documento do material pelo nome
                Document filter = new Document("name", materialName);

                // Executar a consulta para obter o documento do material
                Document materialDocument = collection.find(filter).first();

                if (materialDocument != null) {
                    // Extrair a quantidade disponível do documento do material
                    long availableStock = materialDocument.getLong("stock");

                    return availableStock;
                } else {
                    // O material não foi encontrado
                    return 0;
                }
            }
        }
}