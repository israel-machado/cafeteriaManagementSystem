package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.batch.BatchRequestTest;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponseTest;
import com.project.cafeteriaManagementSystem.service.BatchServiceTest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchControllerTest {

    private final BatchServiceTest batchServiceTest;

    // Método para obter todos os lotes
    @GetMapping
    public ResponseEntity<List<BatchResponseTest>> getAllBatches() {
        // Chama o serviço para obter todos os lotes e retorna uma resposta HTTP 200 OK com a lista de LoteResponse no corpo
        return ResponseEntity.ok().body(batchServiceTest.getAllBatches());
    }

    // Método para obter um lote pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<BatchResponseTest> getBatchById(@PathVariable String id) {
        // Chama o serviço para obter um lote pelo ID e retorna uma resposta HTTP 200 OK com o LoteResponse no corpo
        return ResponseEntity.ok().body(batchServiceTest.getBatchById(id));
    }

    // Método para criar um lote
    @PostMapping
    public ResponseEntity<BatchResponseTest> createBatch(@Valid @RequestBody BatchRequestTest batchRequestTest) {
        BatchResponseTest batchResponseTest = batchServiceTest.createBatch(batchRequestTest);
        return ResponseEntity.status(HttpStatus.CREATED).body(batchResponseTest);
    }

    // Método para atualizar a validade de um lote
    @PutMapping("/{id}")
    public ResponseEntity<BatchResponseTest> updateBatch(@PathVariable String id,
                                                         @Valid @RequestBody BatchRequestTest batchRequestTest) {
        // Chama o serviço para atualizar a validade de um lote pelo ID e retorna uma resposta HTTP 200 OK com o LoteResponse atualizado no corpo
        return ResponseEntity.ok().body(batchServiceTest.updateBatch(id, batchRequestTest));
    }

    // Método para excluir um lote pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable String id) {
        // Chama o serviço para excluir um lote pelo ID e retorna uma resposta HTTP 204 No Content
        batchServiceTest.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }
}
