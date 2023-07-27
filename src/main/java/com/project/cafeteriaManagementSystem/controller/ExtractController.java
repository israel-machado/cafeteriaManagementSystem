package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.extract.ExtractResponse;
import com.project.cafeteriaManagementSystem.service.BatchService;
import com.project.cafeteriaManagementSystem.service.ExtractService;
import com.project.cafeteriaManagementSystem.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/extract")
public class ExtractController {

    private SaleService saleService;
    private BatchService batchService;
    private ExtractService extractService;

    // Método para tratar a solicitação HTTP GET em "/extrato"
    @GetMapping
    public ResponseEntity<ExtractResponse> getExtractLast30Days() {

        // Cria um objeto ExtratoResponse com os valores calculados
        ExtractResponse extractResponse = extractService.getCostAndProfitLast30Days();

        // Retorna uma resposta HTTP 200 OK com o objeto ExtratoResponse no corpo da resposta
        return ResponseEntity.ok(extractResponse);
    }
}
