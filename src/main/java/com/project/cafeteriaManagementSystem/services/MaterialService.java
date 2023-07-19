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

import java.util.ArrayList;
import java.util.List;

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
            MaterialDomain materialDomain = materialRepository.findById(id)
                    .orElseThrow(() -> new InvalidDataException("Material não encontrado pelo ID: " + id));
            return materialConverter.convertMaterialDomainToResponse(materialDomain);
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

                MaterialDomain existingMaterial = materialRepository.findByName(materialRequest.getName());

                if (existingMaterial != null) {

                    // Obter a quantidade atual do material e somar no db
                    double currentQuantity = existingMaterial.getQuantity();
                    double newQuantity = materialRequest.getQuantity() + currentQuantity;

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

        public MaterialResponse insertMaterialWithOutLote(MaterialRequest materialRequest) {
            try {

                MaterialDomain existingMaterial = materialRepository.findByName(materialRequest.getName());

                if (existingMaterial != null) {
                    throw new InvalidMaterialDataException("Material já existe no banco de dados.");

                } else {
                    // Convertendo a requisição para o domínio
                    existingMaterial = materialConverter.convertMaterialRequestToDomain(materialRequest);
                }

                existingMaterial = materialRepository.save(existingMaterial);

                // Convertendo o domínio para a resposta
                return materialConverter.convertMaterialDomainToResponse(existingMaterial);

            } catch(InvalidDataException e){
                // Se os dados forem inválidos, lance uma exceção personalizada
                throw new InvalidMaterialDataException(e.getMessage());
            }
        }

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