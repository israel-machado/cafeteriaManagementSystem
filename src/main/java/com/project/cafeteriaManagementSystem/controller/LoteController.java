package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.Lote.LoteRequest;
import com.project.cafeteriaManagementSystem.model.Lote.LoteResponse;
import com.project.cafeteriaManagementSystem.services.LoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/lotes")
public class LoteController {

    @Autowired
    private LoteService loteService;

    @GetMapping
    public ResponseEntity<List<LoteResponse>> getAllLotes() {
        return ResponseEntity.ok().body(loteService.getAllLotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteResponse> getLoteById(@Valid @PathVariable String id) {
        return ResponseEntity.ok().body(loteService.getLoteById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoteResponse> updateLote(@Valid @PathVariable String id,
                                                   @RequestBody LoteRequest loteRequest) {
        return ResponseEntity.ok().body(loteService.updateLoteValidity(id, loteRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLote(@Valid @PathVariable String id) {
        loteService.deleteLote(id);
        return ResponseEntity.noContent().build();
    }
}
