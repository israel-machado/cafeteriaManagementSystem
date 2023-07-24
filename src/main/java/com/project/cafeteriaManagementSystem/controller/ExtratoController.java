package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.Extrato.ExtratoResponse;
import com.project.cafeteriaManagementSystem.services.LoteService;
import com.project.cafeteriaManagementSystem.services.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/extrato")
public class ExtratoController {

    @Autowired
    private VendaService vendaService;
    @Autowired
    private LoteService loteService;

    // Método para tratar a solicitação HTTP GET em "/extrato"
    @GetMapping
    public ResponseEntity<ExtratoResponse> getExtratoLast30Days() {
        // Chama o serviço para calcular o custo total dos lotes nos últimos 30 dias
        BigDecimal totalCostLotesLast30Days = loteService.calculateTotalCostLotesLast30Days();

        // Chama o serviço para calcular o lucro total das vendas nos últimos 30 dias
        BigDecimal totalProfitLast30Days = vendaService.calculateProfitLast30Days();

        // Cria um objeto ExtratoResponse com os valores calculados
        ExtratoResponse extratoResponse = new ExtratoResponse(totalCostLotesLast30Days, totalProfitLast30Days);

        // Retorna uma resposta HTTP 200 OK com o objeto ExtratoResponse no corpo da resposta
        return ResponseEntity.ok(extratoResponse);
    }
}
