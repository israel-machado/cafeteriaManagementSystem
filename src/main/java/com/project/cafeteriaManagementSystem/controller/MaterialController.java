package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.Material.MaterialRequest;
import com.project.cafeteriaManagementSystem.model.Material.MaterialResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.cafeteriaManagementSystem.services.MaterialService;

import java.util.List;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping
    public ResponseEntity<List<MaterialResponse>> getAll() {
        return ResponseEntity.ok().body(materialService.getAllMaterials());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable String id) {
        return ResponseEntity.ok().body(materialService.getMaterialById(id));
    }

    @PostMapping
    public ResponseEntity<MaterialResponse> createMaterial(@RequestBody MaterialRequest materialRequest) {
        MaterialResponse materialResponse = materialService.createMaterial(materialRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(materialResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponse> updateMaterial(@PathVariable String id, @RequestBody MaterialRequest materialRequest) {
        return ResponseEntity.ok().body(materialService.updateMaterial(id, materialRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable String id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }

}
