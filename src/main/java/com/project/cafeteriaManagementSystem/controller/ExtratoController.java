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

    @GetMapping
    public ResponseEntity<ExtratoResponse> getExtratoLast30Days() {
        BigDecimal totalCostLotesLast30Days = loteService.calculateTotalCostLotesLast30Days();
        BigDecimal totalProfitLast30Days = vendaService.calculateProfitLast30Days();

        ExtratoResponse extratoResponse = new ExtratoResponse(totalCostLotesLast30Days, totalProfitLast30Days);
        return ResponseEntity.ok(extratoResponse);
    }
}
