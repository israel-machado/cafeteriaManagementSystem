package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.material.MaterialMinimumStockRequest;
import com.project.cafeteriaManagementSystem.model.material.MaterialRequest;
import com.project.cafeteriaManagementSystem.model.material.MaterialResponse;
import com.project.cafeteriaManagementSystem.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    // Método para obter todos os materiais
    @GetMapping
    public ResponseEntity<List<MaterialResponse>> getAllMaterials() {
        // Chama o serviço para obter todos os materiais e retorna uma resposta HTTP 200 OK com a lista de MaterialResponse no corpo
        return ResponseEntity.ok().body(materialService.getAllMaterials());
    }

    // Método para obter um material pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable String id) {
        // Chama o serviço para obter um material pelo ID e retorna uma resposta HTTP 200 OK com o MaterialResponse no corpo
        return ResponseEntity.ok().body(materialService.getMaterialById(id));
    }

    // Método para inserir um novo material
    @PostMapping
    public ResponseEntity<MaterialResponse> createMaterial(@Valid @RequestBody MaterialRequest materialRequest) {
        // Chama o serviço para inserir um novo material e retorna uma resposta HTTP 201 Created com o MaterialResponse criado no corpo da resposta
        MaterialResponse materialResponse = materialService.createMaterial(materialRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(materialResponse);
    }

    // Método para atualizar um material pelo ID
    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponse> updateMaterial(@PathVariable String id,
                                                           @Valid @RequestBody MaterialRequest materialRequest) {
        // Chama o serviço para atualizar um material pelo ID e retorna uma resposta HTTP 200 OK com o MaterialResponse atualizado no corpo da resposta
        return ResponseEntity.ok().body(materialService.updateMaterial(id, materialRequest));
    }

    // Método para excluir um material pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable String id) {
        // Chama o serviço para excluir um material pelo ID e retorna uma resposta HTTP 204 No Content
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }

    // Método para obter materiais com validade próxima ao vencimento
    @GetMapping("/expiring")
    public ResponseEntity<List<MaterialResponse>> getExpiringMaterials(@Valid @RequestParam int daysToExpiration) {
        // Chama o serviço para obter uma lista de materiais com validade próxima ao vencimento e retorna uma resposta HTTP 200 OK com essa lista no corpo da resposta
        List<MaterialResponse> expiringMaterials = materialService.getExpiringMaterials(daysToExpiration);
        return ResponseEntity.ok(expiringMaterials);
    }

    // Método para atualizar o estoque mínimo de um material
    @PutMapping("/minimum-stock")
    public ResponseEntity<MaterialResponse> updateMaterialMinimumStock(@Valid @RequestBody MaterialMinimumStockRequest request) {
        // Chama o serviço para atualizar o estoque mínimo de um material e retorna uma resposta HTTP 200 OK com o MaterialResponse atualizado no corpo da resposta
        MaterialResponse response = materialService.updateMaterialMinimumStock(request);
        return ResponseEntity.ok(response);
    }

    // Método para obter materiais com estoque baixo
    @GetMapping("/low-stock")
    public ResponseEntity<List<MaterialResponse>> getMaterialsWithLowStock() {
        // Chama o serviço para obter uma lista de materiais com estoque baixo e retorna uma resposta HTTP 200 OK com essa lista no corpo da resposta
        List<MaterialResponse> materialsWithLowStock = materialService.getMaterialsWithLowStock();
        return ResponseEntity.ok(materialsWithLowStock);
    }
}
