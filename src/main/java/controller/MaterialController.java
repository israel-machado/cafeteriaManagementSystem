package controller;

import model.Material.MaterialRequest;
import model.Material.MaterialResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.MaterialService;

import java.util.List;

@RestController
@RequestMapping("/materials")
public class MaterialController {
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
    public ResponseEntity<MaterialResponse> registerMaterial(@RequestBody MaterialRequest materialRequest) {
        MaterialResponse materialResponse = materialService.registerMaterial(materialRequest);
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
