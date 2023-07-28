package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.batch.BatchRequest;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponse;
import com.project.cafeteriaManagementSystem.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batches")
public class BatchController {

    private final BatchService batchService;

    // Método para obter todos os lotes
    @GetMapping
    public ResponseEntity<List<BatchResponse>> getAllBatches() {
        // Chama o serviço para obter todos os lotes e retorna uma resposta HTTP 200 OK com a lista de LoteResponse no corpo
        return ResponseEntity.ok().body(batchService.getAllBatches());
    }

    // Método para obter um lote pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<BatchResponse> getBatchById(@PathVariable String id) {
        // Chama o serviço para obter um lote pelo ID e retorna uma resposta HTTP 200 OK com o LoteResponse no corpo
        return ResponseEntity.ok().body(batchService.getBatchById(id));
    }

    // Método para criar um lote
    public ResponseEntity<BatchResponse> createBatch(@Valid @RequestBody BatchRequest batchRequest) {
        BatchResponse batchResponse = batchService.createBatch(batchRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(batchResponse);
    }

    // Método para atualizar a validade de um lote
    @PutMapping("/{id}")
    public ResponseEntity<BatchResponse> updateBatch(@PathVariable String id,
                                                     @Valid @RequestBody BatchRequest batchRequest) {
        // Chama o serviço para atualizar a validade de um lote pelo ID e retorna uma resposta HTTP 200 OK com o LoteResponse atualizado no corpo
        return ResponseEntity.ok().body(batchService.updateBatch(id, batchRequest));
    }

    // Método para excluir um lote pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable String id) {
        // Chama o serviço para excluir um lote pelo ID e retorna uma resposta HTTP 204 No Content
        batchService.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }
}
