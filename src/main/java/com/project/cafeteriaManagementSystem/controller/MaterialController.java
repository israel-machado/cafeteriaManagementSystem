package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.Material.MaterialMinimumStockRequest;
import com.project.cafeteriaManagementSystem.model.Material.MaterialRequest;
import com.project.cafeteriaManagementSystem.model.Material.MaterialResponse;
import com.project.cafeteriaManagementSystem.model.Material.MaterialWithoutLoteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.cafeteriaManagementSystem.services.MaterialService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping
    public ResponseEntity<List<MaterialResponse>> getAllMaterials() {
        return ResponseEntity.ok().body(materialService.getAllMaterials());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getMaterialById(@Valid @PathVariable String id) {
        return ResponseEntity.ok().body(materialService.getMaterialById(id));
    }

    @PostMapping
    public ResponseEntity<MaterialResponse> insertMaterial(@Valid @RequestBody MaterialRequest materialRequest) {
        MaterialResponse materialResponse = materialService.insertMaterial(materialRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(materialResponse);
    }

    @PostMapping("/register-db")
    public ResponseEntity<MaterialResponse> insertMaterialWithOutLote(@Valid @RequestBody MaterialWithoutLoteRequest materialRequest) {
        MaterialResponse materialResponse = materialService.insertMaterialWithOutLote(materialRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(materialResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponse> updateMaterial(@Valid @PathVariable String id,
                                                           @RequestBody MaterialRequest materialRequest) {
        return ResponseEntity.ok().body(materialService.updateMaterial(id, materialRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@Valid @PathVariable String id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<MaterialResponse>> getExpiringMaterials(@Valid @RequestParam int daysToExpiration) {
        List<MaterialResponse> expiringMaterials = materialService.getExpiringMaterials(daysToExpiration);
        return ResponseEntity.ok(expiringMaterials);
    }

    @PutMapping("/minimum-stock")
    public ResponseEntity<MaterialResponse> updateMaterialMinimumStock(@Valid @RequestBody MaterialMinimumStockRequest request) {
        MaterialResponse response = materialService.updateMaterialMinimumStock(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<MaterialResponse>> getMaterialsWithLowStock() {
        List<MaterialResponse> materialsWithLowStock = materialService.getMaterialsWithLowStock();
        return ResponseEntity.ok(materialsWithLowStock);
    }
}
