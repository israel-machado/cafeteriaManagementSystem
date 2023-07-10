package com.project.cafeteriaManagementSystem.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.project.cafeteriaManagementSystem.exceptions.InsufficientMaterialStockException;
import com.project.cafeteriaManagementSystem.exceptions.InsufficientStockException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidDataException;
import com.project.cafeteriaManagementSystem.exceptions.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.mapping.MaterialConverter;
import com.project.cafeteriaManagementSystem.model.Lote.LoteDomain;
import com.project.cafeteriaManagementSystem.model.Lote.LoteRequest;
import com.project.cafeteriaManagementSystem.model.Material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.Material.MaterialRequest;
import com.project.cafeteriaManagementSystem.model.Material.MaterialResponse;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaterialService {

        private final MaterialConverter materialConverter;
        private final MaterialRepository materialRepository;
        private final LoteService loteService;

        // GET ALL
        public List<MaterialResponse> getAllMaterials() {
            List<MaterialDomain> materialList = materialRepository.findAll();
            return materialConverter.convertMaterialDomainListToResponseList(materialList);
        }

        // GET BY ID
        public MaterialResponse getMaterialById(String id) {
            Optional<MaterialDomain> materialDomain = materialRepository.findById(id);
            return materialDomain.map(materialConverter::convertMaterialDomainToResponse)
                    .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + id));
        }

        // UPDATE
        public MaterialResponse updateMaterial(String id, MaterialRequest materialRequest) {
            try {
                if (materialRequest == null) {
                    throw new InvalidDataException("Requisição do material não pode ser nula.");
                }
                MaterialDomain materialDomain = materialConverter.convertMaterialRequestToDomain(materialRequest);
                materialDomain.setId(id);
                MaterialDomain updatedMaterial = materialRepository.save(materialDomain);
                return materialConverter.convertMaterialDomainToResponse(updatedMaterial);
            } catch (InvalidDataException e) {
                throw new InvalidMaterialDataException("Falha ao atualizar o material com o ID: " + id);
            }
        }

        // DELETE
        public void deleteMaterial(String id) {
            try {
                materialRepository.deleteById(id);
            } catch (EmptyResultDataAccessException e) {
                throw new InvalidMaterialDataException("Material não encontrado através do ID: " + id);
            } catch (DataIntegrityViolationException e) {
                throw new InvalidMaterialDataException("Erro ao deletar material com o ID: " + id + " - " + e.getMessage());
            }
        }

        // INSERT
        public MaterialResponse insertMaterial(MaterialRequest materialRequest) {
            try {
                // Validação dos dados
                validateMaterialRequest(materialRequest);

                MaterialDomain existingMaterial = materialRepository.findByName(materialRequest.getName());

                if (existingMaterial != null) {

                    // Obter a quantidade atual do material e somar no db
                    double currentQuantity = existingMaterial.getQuantity();
                    double newQuantity = currentQuantity + materialRequest.getQuantity();

                    existingMaterial.setQuantity(newQuantity);

                } else {

                    // Convertendo a requisição para o domínio
                    existingMaterial = materialConverter.convertMaterialRequestToDomain(materialRequest);
                }

                existingMaterial = materialRepository.save(existingMaterial);

                // Criação de um lote para o material
                LoteDomain loteDomain = loteService.createLote(materialRequest, existingMaterial);

                // Associação do lote ao material
                List<LoteDomain> loteDomainList = existingMaterial.getLoteDomainList();

                if (loteDomainList == null) {
                    loteDomainList = new ArrayList<>();
                    existingMaterial.setLoteDomainList(loteDomainList);
                }
                loteDomainList.add(loteDomain);

                // Atualizando o material no banco de dados com a associação ao lote
                existingMaterial = materialRepository.save(existingMaterial);

                // Convertendo o domínio para a resposta
                return materialConverter.convertMaterialDomainToResponse(existingMaterial);


            } catch(InvalidDataException e){
                // Se os dados forem inválidos, lance uma exceção personalizada
                throw new InvalidMaterialDataException(e.getMessage());
            } catch(InsufficientStockException e){
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
            LocalDate expirationDate = materialRequest.getLoteRequest().getValidity();

            // Calcular a diferença em dias entre a data atual e a data de validade
            long daysUntilExpiration = ChronoUnit.DAYS.between(currentDate, expirationDate);

            // Verificar se a diferença é menor que 7 dias
            if (daysUntilExpiration < 7) {
                throw new InvalidDataException("A data de validade é menor que uma semana.");
            }
        }

//        private void checkStockAvailability(MaterialRequest materialRequest) {
//            Fazer quando tiver fazendo a parte da venda
//            }
//        }

    private double getAvailableStock(String id) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            // Criar conexão com o MongoDB
            MongoDatabase database = mongoClient.getDatabase("cafeteria_db"); // Nome do seu banco de dados
            MongoCollection<Document> collection = database.getCollection("materiais"); // Nome da coleção de materiais

            // Criar filtro para buscar o documento do material pelo ID
            Document filter = new Document("_id", new ObjectId(id));

            // Executar a consulta para obter o documento do material
            Document materialDocument = collection.find(filter).first();

            if (materialDocument != null) {
                // Extrair a quantidade do material do documento
                double quantity = materialDocument.getDouble("quantity");

                // Retornar a quantidade disponível
                return quantity;
            } else {
                // O material não foi encontrado
                return 0;
            }
        }
    }
}