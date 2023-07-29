package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.mapping.MaterialConverterTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialMinimumStockRequestTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialRequestTest;
import com.project.cafeteriaManagementSystem.model.material.MaterialResponseTest;
import com.project.cafeteriaManagementSystem.service.MaterialServiceTest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/material")
public class MaterialControllerTest {

    private final MaterialServiceTest materialServiceTest;
    private final MaterialConverterTest materialConverterTest;

    // Método para obter todos os materiais
    @GetMapping
    public ResponseEntity<List<MaterialResponseTest>> getAllMaterials() {
        // Chama o serviço para obter todos os materiais e retorna uma resposta HTTP 200 OK com a lista de MaterialResponse no corpo
        return ResponseEntity.ok().body(materialServiceTest.getAllMaterials());
    }

    // Método para obter um material pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponseTest> getMaterialById(@PathVariable String id) {
        // Chama o serviço para obter um material pelo ID e retorna uma resposta HTTP 200 OK com o MaterialResponse no corpo
        return ResponseEntity.ok().body(materialServiceTest.getMaterialById(id));
    }

    // Método para inserir um novo material
    @PostMapping
    public ResponseEntity<MaterialResponseTest> createMaterial(@Valid @RequestBody MaterialRequestTest materialRequestTest) {
        // Chama o serviço para inserir um novo material e retorna uma resposta HTTP 201 Created com o MaterialResponse criado no corpo da resposta
        MaterialResponseTest materialResponseTest = materialConverterTest.convertMaterialDomainToResponse(materialServiceTest.createMaterial(materialRequestTest));
        return ResponseEntity.status(HttpStatus.CREATED).body(materialResponseTest);
    }

    // Método para atualizar um material pelo ID
    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponseTest> updateMaterial(@PathVariable String id,
                                                               @Valid @RequestBody MaterialRequestTest materialRequestTest) {
        // Chama o serviço para atualizar um material pelo ID e retorna uma resposta HTTP 200 OK com o MaterialResponse atualizado no corpo da resposta
        return ResponseEntity.ok().body(materialServiceTest.updateMaterial(id, materialRequestTest));
    }

    // Método para excluir um material pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable String id) {
        // Chama o serviço para excluir um material pelo ID e retorna uma resposta HTTP 204 No Content
        materialServiceTest.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }

    // Método para obter materiais com validade próxima ao vencimento
    @GetMapping("/expiring")
    public ResponseEntity<List<MaterialResponseTest>> getExpiringMaterials(@Valid @RequestParam int daysToExpiration) {
        // Chama o serviço para obter uma lista de materiais com validade próxima ao vencimento e retorna uma resposta HTTP 200 OK com essa lista no corpo da resposta
        List<MaterialResponseTest> expiringMaterials = materialServiceTest.getExpiringMaterials(daysToExpiration);
        return ResponseEntity.ok(expiringMaterials);
    }

    // Método para atualizar o estoque mínimo de um material
    @PutMapping("/minimum-stock")
    public ResponseEntity<MaterialResponseTest> updateMaterialMinimumStock(@Valid @RequestBody MaterialMinimumStockRequestTest request) {
        // Chama o serviço para atualizar o estoque mínimo de um material e retorna uma resposta HTTP 200 OK com o MaterialResponse atualizado no corpo da resposta
        MaterialResponseTest response = materialServiceTest.updateMaterialMinimumStock(request);
        return ResponseEntity.ok(response);
    }

    // Método para obter materiais com estoque baixo
    @GetMapping("/low-stock")
    public ResponseEntity<List<MaterialResponseTest>> getMaterialsWithLowStock() {
        // Chama o serviço para obter uma lista de materiais com estoque baixo e retorna uma resposta HTTP 200 OK com essa lista no corpo da resposta
        List<MaterialResponseTest> materialsWithLowStock = materialServiceTest.getMaterialsWithLowStock();
        return ResponseEntity.ok(materialsWithLowStock);
    }
}
