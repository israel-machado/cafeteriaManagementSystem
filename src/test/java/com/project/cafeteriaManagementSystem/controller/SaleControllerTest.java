package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.sale.SaleRequestTest;
import com.project.cafeteriaManagementSystem.model.sale.SaleResponseTest;
import com.project.cafeteriaManagementSystem.service.SaleServiceTest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sale")
public class SaleControllerTest {

    private final SaleServiceTest saleServiceTest;

    // Método para obter todas as vendas
    @GetMapping
    public ResponseEntity<List<SaleResponseTest>> getAllSales() {
        // Chama o serviço para obter todas as vendas e retorna uma resposta HTTP 200 OK com a lista de VendaResponse no corpo da resposta
        return ResponseEntity.ok().body(saleServiceTest.getAllSales());
    }

    // Método para obter todas as vendas em um determinado período de dias
    @GetMapping("/by-date")
    public ResponseEntity<List<SaleResponseTest>> getSalesByDate(@RequestParam int duration) {
        List<SaleResponseTest> sales = saleServiceTest.getSalesByDuration(duration);
        return ResponseEntity.ok(sales);
    }

    // Método para obter todas as vendas em um determinado mês
    @GetMapping("/by-month")
    public ResponseEntity<List<SaleResponseTest>> getSalesByMonth(@RequestParam int month, @RequestParam int year) {
        List<SaleResponseTest> sales = saleServiceTest.getSalesForMonth(month, year);
        return ResponseEntity.ok(sales);
    }

    // Método para obter uma venda pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseTest> getSaleById(@PathVariable String id) {
        // Chama o serviço para obter os detalhes da venda com o ID fornecido na requisição
        SaleResponseTest saleResponseTest = saleServiceTest.getSaleById(id);
        // Retorna uma resposta HTTP 200 OK com o VendaResponse contendo os detalhes da venda no corpo da resposta
        return ResponseEntity.ok(saleResponseTest);
    }

    // Método para realizar uma venda
    @PostMapping
    public ResponseEntity<SaleResponseTest> makeSale(@Valid @RequestBody SaleRequestTest saleRequestTest) {
        // Chama o serviço para realizar a venda com os dados fornecidos na requisição
        // e retorna uma resposta HTTP 201 Created com o VendaResponse criado no corpo da resposta
        SaleResponseTest saleResponseTest = saleServiceTest.makeSale(saleRequestTest);
        return ResponseEntity.status(HttpStatus.CREATED).body(saleResponseTest);
    }
}
