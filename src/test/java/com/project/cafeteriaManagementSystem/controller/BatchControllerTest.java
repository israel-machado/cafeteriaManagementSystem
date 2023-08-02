package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.batch.BatchRequest;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponse;
import com.project.cafeteriaManagementSystem.service.BatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BatchControllerTest {

    @Mock
    private BatchService batchService;

    @InjectMocks
    private BatchController batchController;

    @Test
    void testGetAllBatches() {
        // Criar um mock de lista de BatchResponse para o serviço retornar
        List<BatchResponse> mockBatchList = new ArrayList<>();
        mockBatchList.add(new BatchResponse());
        // Configurar o comportamento do serviço para retornar a lista mockBatchList
        when(batchService.getAllBatches()).thenReturn(mockBatchList);

        // Chamar o método do controller
        ResponseEntity<List<BatchResponse>> responseEntity = batchController.getAllBatches();

        // Verificar se o serviço foi chamado corretamente
        verify(batchService, times(1)).getAllBatches();

        // Verificar o resultado
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBatchList, responseEntity.getBody());
    }

    @Test
    void testGetBatchById() {
        // Criar um mock de BatchResponse para o serviço retornar
        BatchResponse mockBatchResponse = new BatchResponse();
        // Configurar o comportamento do serviço para retornar o mockBatchResponse
        when(batchService.getBatchById(anyString())).thenReturn(mockBatchResponse);

        // Chamar o método do controller
        ResponseEntity<BatchResponse> responseEntity = batchController.getBatchById("someId");

        // Verificar se o serviço foi chamado corretamente com o ID correto
        verify(batchService, times(1)).getBatchById(eq("someId"));

        // Verificar o resultado
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBatchResponse, responseEntity.getBody());
    }

    @Test
    void testCreateBatch() {
        // Criar um mock de BatchRequest e BatchResponse para o serviço retornar
        BatchRequest mockBatchRequest = new BatchRequest();
        BatchResponse mockBatchResponse = new BatchResponse();
        // Configurar o comportamento do serviço para retornar o mockBatchResponse
        when(batchService.createBatch(any())).thenReturn(mockBatchResponse);

        // Chamar o método do controller
        ResponseEntity<BatchResponse> responseEntity = batchController.createBatch(mockBatchRequest);

        // Verificar se o serviço foi chamado corretamente com o BatchRequest correto
        verify(batchService, times(1)).createBatch(eq(mockBatchRequest));

        // Verificar o resultado
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockBatchResponse, responseEntity.getBody());
    }

    @Test
    void testUpdateBatch() {
        // Criar um mock de BatchRequest e BatchResponse para o serviço retornar
        BatchRequest mockBatchRequest = new BatchRequest();
        BatchResponse mockBatchResponse = new BatchResponse();
        // Configurar o comportamento do serviço para retornar o mockBatchResponse
        when(batchService.updateBatch(anyString(), any())).thenReturn(mockBatchResponse);

        // Chamar o método do controller
        ResponseEntity<BatchResponse> responseEntity = batchController.updateBatch("someId", mockBatchRequest);

        // Verificar se o serviço foi chamado corretamente com o ID correto e o BatchRequest correto
        verify(batchService, times(1)).updateBatch(eq("someId"), eq(mockBatchRequest));

        // Verificar o resultado
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBatchResponse, responseEntity.getBody());
    }

    @Test
    void testDeleteBatch() {
        // Chamar o método do controller
        ResponseEntity<Void> responseEntity = batchController.deleteBatch("someId");

        // Verificar se o serviço foi chamado corretamente com o ID correto
        verify(batchService, times(1)).deleteBatch(eq("someId"));

        // Verificar o resultado
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
}