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

    // Método para obter todos os lotes
    @GetMapping
    public ResponseEntity<List<LoteResponse>> getAllLotes() {
        // Chama o serviço para obter todos os lotes e retorna uma resposta HTTP 200 OK com a lista de LoteResponse no corpo
        return ResponseEntity.ok().body(loteService.getAllLotes());
    }

    // Método para obter um lote pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<LoteResponse> getLoteById(@Valid @PathVariable String id) {
        // Chama o serviço para obter um lote pelo ID e retorna uma resposta HTTP 200 OK com o LoteResponse no corpo
        return ResponseEntity.ok().body(loteService.getLoteById(id));
    }

    // Método para atualizar a validade de um lote
    @PutMapping("/{id}")
    public ResponseEntity<LoteResponse> updateLote(@Valid @PathVariable String id,
                                                   @RequestBody LoteRequest loteRequest) {
        // Chama o serviço para atualizar a validade de um lote pelo ID e retorna uma resposta HTTP 200 OK com o LoteResponse atualizado no corpo
        return ResponseEntity.ok().body(loteService.updateLoteValidity(id, loteRequest));
    }

    // Método para excluir um lote pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLote(@Valid @PathVariable String id) {
        // Chama o serviço para excluir um lote pelo ID e retorna uma resposta HTTP 204 No Content
        loteService.deleteLote(id);
        return ResponseEntity.noContent().build();
    }
}
