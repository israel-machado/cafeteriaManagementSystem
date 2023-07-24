package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.Venda.VendaRequest;
import com.project.cafeteriaManagementSystem.model.Venda.VendaResponse;
import com.project.cafeteriaManagementSystem.services.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/sell")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    // Método para obter todas as vendas
    @GetMapping
    public ResponseEntity<List<VendaResponse>> getAllSells() {
        // Chama o serviço para obter todas as vendas e retorna uma resposta HTTP 200 OK com a lista de VendaResponse no corpo da resposta
        return ResponseEntity.ok().body(vendaService.getAllSells());
    }

    // Método para obter uma venda pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<VendaResponse> getSellById(@Valid @PathVariable String id) {
        // Chama o serviço para obter os detalhes da venda com o ID fornecido na requisição
        VendaResponse vendaResponse = vendaService.getSellById(id);
        // Retorna uma resposta HTTP 200 OK com o VendaResponse contendo os detalhes da venda no corpo da resposta
        return ResponseEntity.ok(vendaResponse);
    }

    // Método para realizar uma venda
    @PostMapping
    public ResponseEntity<VendaResponse> sell(@Valid @RequestBody VendaRequest vendaRequest) {
        // Chama o serviço para realizar a venda com os dados fornecidos na requisição
        // e retorna uma resposta HTTP 201 Created com o VendaResponse criado no corpo da resposta
        VendaResponse vendaResponse = vendaService.sell(vendaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaResponse);
    }
}
