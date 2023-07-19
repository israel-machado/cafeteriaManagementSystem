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

    @GetMapping
    public ResponseEntity<List<VendaResponse>> getAllSells() {
        return ResponseEntity.ok().body(vendaService.getAllSells());
    }

    @PostMapping
    public ResponseEntity<VendaResponse> sell(@Valid @RequestBody VendaRequest vendaRequest) {
        VendaResponse vendaResponse = vendaService.sell(vendaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaResponse);
    }
}
