package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.mapping.MaterialConverter;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialMinimumStockRequest;
import com.project.cafeteriaManagementSystem.model.material.MaterialRequest;
import com.project.cafeteriaManagementSystem.model.material.MaterialResponse;
import com.project.cafeteriaManagementSystem.service.MaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaterialControllerTest {

    @Mock
    private MaterialService materialService;

    @Mock
    private MaterialConverter materialConverter;

    @InjectMocks
    private MaterialController materialController;

    @Test
    void testGetAllMaterials() {
        // Criar um mock de lista de MaterialResponse para o serviço retornar
        List<MaterialResponse> mockMaterialList = new ArrayList<>();
        mockMaterialList.add(new MaterialResponse());
        // Configurar o comportamento do serviço para retornar a lista mockMaterialList
        when(materialService.getAllMaterials()).thenReturn(mockMaterialList);

        // Chamar o método do controller
        ResponseEntity<List<MaterialResponse>> responseEntity = materialController.getAllMaterials();

        // Verificar se o serviço foi chamado corretamente
        verify(materialService, times(1)).getAllMaterials();

        // Verificar o resultado
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockMaterialList, responseEntity.getBody());
    }

    @Test
    void testGetMaterialById() {
        // Criar um mock de MaterialResponse para o serviço retornar
        MaterialResponse mockMaterialResponse = new MaterialResponse();
        // Configurar o comportamento do serviço para retornar o mockMaterialResponse
        when(materialService.getMaterialById(anyString())).thenReturn(mockMaterialResponse);

        // Chamar o método do controller
        ResponseEntity<MaterialResponse> responseEntity = materialController.getMaterialById("someId");

        // Verificar se o serviço foi chamado corretamente com o ID correto
        verify(materialService, times(1)).getMaterialById(eq("someId"));

        // Verificar o resultado
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockMaterialResponse, responseEntity.getBody());
    }

    @Test
    void testCreateMaterial() {
        // Criar um mock de MaterialRequest e MaterialDomain para o serviço retornar
        MaterialRequest mockMaterialRequest = new MaterialRequest();
        MaterialDomain mockMaterialDomain = new MaterialDomain();
        // Configurar o comportamento do serviço para retornar o mockMaterialDomain
        when(materialService.createMaterial(any())).thenReturn(mockMaterialDomain);

        // Criar um mock de MaterialResponse para o converter retornar
        MaterialResponse mockMaterialResponse = new MaterialResponse();
        // Configurar o comportamento do converter para retornar o mockMaterialResponse
        when(materialConverter.convertMaterialDomainToResponse(eq(mockMaterialDomain))).thenReturn(mockMaterialResponse);

        // Chamar o método do controller
        ResponseEntity<MaterialResponse> responseEntity = materialController.createMaterial(mockMaterialRequest);

        // Verificar se o serviço foi chamado corretamente com o MaterialRequest correto
        verify(materialService, times(1)).createMaterial(eq(mockMaterialRequest));

        // Verificar o resultado
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockMaterialResponse, responseEntity.getBody());
    }

    @Test
    void testUpdateMaterial() {
        // Criar um mock de MaterialRequest e MaterialResponse para o serviço retornar
        MaterialRequest mockMaterialRequest = new MaterialRequest();
        MaterialResponse mockMaterialResponse = new MaterialResponse();
        // Configurar o comportamento do serviço para retornar o mockMaterialResponse
        when(materialService.updateMaterial(anyString(), any())).thenReturn(mockMaterialResponse);

        // Chamar o método do controller
        ResponseEntity<MaterialResponse> responseEntity = materialController.updateMaterial("someId", mockMaterialRequest);

        // Verificar se o serviço foi chamado corretamente com o ID correto e o MaterialRequest correto
        verify(materialService, times(1)).updateMaterial(eq("someId"), eq(mockMaterialRequest));

        // Verificar o resultado
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockMaterialResponse, responseEntity.getBody());
    }

    @Test
    void testDeleteMaterial() {
        // Chamar o método do controller
        ResponseEntity<Void> responseEntity = materialController.deleteMaterial("someId");

        // Verificar se o serviço foi chamado corretamente com o ID correto
        verify(materialService, times(1)).deleteMaterial(eq("someId"));

        // Verificar o resultado
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testGetExpiringMaterials() {
        int daysToExpiration = 30;

        // Criar um mock de lista de MaterialResponse para o serviço retornar
        List<MaterialResponse> mockExpiringMaterials = new ArrayList<>();
        mockExpiringMaterials.add(new MaterialResponse());
        // Configurar o comportamento do serviço para retornar a lista mockExpiringMaterials
        when(materialService.getExpiringMaterials(anyInt())).thenReturn(mockExpiringMaterials);

        // Chamar o método do controller
        ResponseEntity<List<MaterialResponse>> responseEntity = materialController.getExpiringMaterials(daysToExpiration);

        // Verificar se o serviço foi chamado corretamente com o parâmetro correto
        verify(materialService, times(1)).getExpiringMaterials(eq(daysToExpiration));

        // Verificar o resultado
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockExpiringMaterials, responseEntity.getBody());
    }

    @Test
    void testUpdateMaterialMinimumStock() {
        // Criar um mock de MaterialMinimumStockRequest
        MaterialMinimumStockRequest mockRequest = new MaterialMinimumStockRequest();
        // Criar um mock de MaterialResponse para o serviço retornar
        MaterialResponse mockMaterialResponse = new MaterialResponse();
        // Configurar o comportamento do serviço para retornar o mockMaterialResponse
        when(materialService.updateMaterialMinimumStock(any())).thenReturn(mockMaterialResponse);

        // Chamar o método do controller
        ResponseEntity<MaterialResponse> responseEntity = materialController.updateMaterialMinimumStock(mockRequest);

        // Verificar se o serviço foi chamado corretamente com o MaterialMinimumStockRequest correto
        verify(materialService, times(1)).updateMaterialMinimumStock(eq(mockRequest));

        // Verificar o resultado
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockMaterialResponse, responseEntity.getBody());
    }

    @Test
    void testGetMaterialsWithLowStock() {
        // Criar um mock de lista de MaterialResponse para o serviço retornar
        List<MaterialResponse> mockMaterialsWithLowStock = new ArrayList<>();
        mockMaterialsWithLowStock.add(new MaterialResponse());
        // Configurar o comportamento do serviço para retornar a lista mockMaterialsWithLowStock
        when(materialService.getMaterialsWithLowStock()).thenReturn(mockMaterialsWithLowStock);

        // Chamar o método do controller
        ResponseEntity<List<MaterialResponse>> responseEntity = materialController.getMaterialsWithLowStock();

        // Verificar se o serviço foi chamado corretamente
        verify(materialService, times(1)).getMaterialsWithLowStock();

        // Verificar o resultado
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockMaterialsWithLowStock, responseEntity.getBody());
    }
}