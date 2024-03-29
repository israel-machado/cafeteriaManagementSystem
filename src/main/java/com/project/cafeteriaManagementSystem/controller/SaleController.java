package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.sale.SaleRequest;
import com.project.cafeteriaManagementSystem.model.sale.SaleResponse;
import com.project.cafeteriaManagementSystem.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sale")
public class SaleController {

    private final SaleService saleService;

    // Método para obter todas as vendas
    @GetMapping
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        // Chama o serviço para obter todas as vendas e retorna uma resposta HTTP 200 OK com a lista de VendaResponse no corpo da resposta
        return ResponseEntity.ok().body(saleService.getAllSales());
    }

    // Método para obter todas as vendas em um determinado período de dias
    @GetMapping("/by-date")
    public ResponseEntity<List<SaleResponse>> getSalesByDate(@RequestParam int duration) {
        List<SaleResponse> sales = saleService.getSalesByDuration(duration);
        return ResponseEntity.ok(sales);
    }

    // Método para obter todas as vendas em um determinado mês
    @GetMapping("/by-month")
    public ResponseEntity<List<SaleResponse>> getSalesByMonth(@RequestParam int month, @RequestParam int year) {
        List<SaleResponse> sales = saleService.getSalesForMonth(month, year);
        return ResponseEntity.ok(sales);
    }

    // Método para obter uma venda pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable String id) {
        // Chama o serviço para obter os detalhes da venda com o ID fornecido na requisição
        SaleResponse saleResponse = saleService.getSaleById(id);
        // Retorna uma resposta HTTP 200 OK com o VendaResponse contendo os detalhes da venda no corpo da resposta
        return ResponseEntity.ok(saleResponse);
    }

    // Método para realizar uma venda
    @PostMapping
    public ResponseEntity<SaleResponse> makeSale(@Valid @RequestBody SaleRequest saleRequest) {
        // Chama o serviço para realizar a venda com os dados fornecidos na requisição
        // e retorna uma resposta HTTP 201 Created com o VendaResponse criado no corpo da resposta
        SaleResponse saleResponse = saleService.makeSale(saleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(saleResponse);
    }
}
