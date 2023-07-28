package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.extract.ExtractResponse;
import com.project.cafeteriaManagementSystem.service.ExtractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/extract")
public class ExtractController {

    private final ExtractService extractService;

    // Método para tratar a solicitação HTTP GET em "/extract"
    @GetMapping
    public ResponseEntity<ExtractResponse> getExtractForTimePeriod(@RequestParam int duration) {
        // Cria um objeto ExtractResponse com os valores calculados para o período especificado
        ExtractResponse extractResponse = extractService.getCostAndProfitForTimePeriod(duration);

        // Retorna uma resposta HTTP 200 OK com o objeto ExtractResponse no corpo da resposta
        return ResponseEntity.ok(extractResponse);
    }
}
