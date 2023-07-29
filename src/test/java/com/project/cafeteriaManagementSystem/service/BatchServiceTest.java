package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.mapping.BatchConverter;
import com.project.cafeteriaManagementSystem.model.batch.BatchDomain;
import com.project.cafeteriaManagementSystem.model.batch.BatchRequest;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponse;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialRequest;
import com.project.cafeteriaManagementSystem.repository.BatchRepository;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private MaterialService materialService;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private BatchConverter batchConverter;

    @InjectMocks
    private BatchService batchService;

    private BatchRequest batchRequest;
    private MaterialDomain materialDomain;
    private BatchDomain batchDomain;
    private List<BatchDomain> batchDomainList;

    @BeforeEach
    void setUp() {
        // Inicializar os objetos que serão usados nos testes
        batchRequest = new BatchRequest();
        batchRequest.setInitialAmount(100.0);
        batchRequest.setTotalCost(BigDecimal.valueOf(50.0));
        batchRequest.setValidity(LocalDateTime.of(2023, 7, 1, 12, 0));
        batchRequest.setDateOfPurchase(LocalDateTime.of(2023, 6, 30, 12, 0));
        batchRequest.setSupplierName("Fornecedor 1");

        MaterialRequest materialRequest = new MaterialRequest();
        materialRequest.setName("Material 1");

        materialDomain = new MaterialDomain();
        materialDomain.setId("1");
        materialDomain.setName("Material 1");

        batchDomain = new BatchDomain();
        batchDomain.setId("1");
        batchDomain.setMaterialDomain(materialDomain);

        batchDomainList = new ArrayList<>();
        batchDomainList.add(batchDomain);
    }

    @Test
    void testCreateBatch() {
        // Criar um objeto MaterialRequest para simular o material a ser criado
        MaterialRequest materialRequest = MaterialRequest.builder()
                .name("Nome do Material")
                .unitMeasure("kg")
                .build();

        // Criar um objeto BatchRequest para simular o lote a ser criado
        BatchRequest batchRequest = BatchRequest.builder()
                .initialAmount(500.0)
                .totalCost(BigDecimal.valueOf(150.0))
                .validity(LocalDateTime.now().plusMonths(6))
                .dateOfPurchase(LocalDateTime.now().minusDays(5))
                .supplierName("Fornecedor XYZ")
                .materialRequest(materialRequest)
                .build();

        // Configurar os mocks
        when(materialRepository.findByName(anyString())).thenReturn(null);
        when(materialService.createMaterial(any(MaterialRequest.class))).thenReturn(materialDomain);
        when(batchRepository.save(any(BatchDomain.class))).thenReturn(batchDomain);
        when(batchConverter.convertBatchDomainToResponse(any(BatchDomain.class))).thenReturn(new BatchResponse());

        // Chame o método createBatch do BatchService
        BatchResponse batchResponse = batchService.createBatch(batchRequest);

        // Verifique se os métodos do mock foram chamados corretamente
        verify(materialRepository, times(1)).findByName(anyString());
        verify(materialService, times(1)).createMaterial(any(MaterialRequest.class));
        verify(batchRepository, times(1)).save(any(BatchDomain.class));

        // Verifique se o BatchResponse retornado é o esperado
        assertNotNull(batchResponse);
        // Verifique outros campos do BatchResponse conforme necessário
    }

    @Test
    void testGetAllBatches() {
        // Configure o comportamento do mock do batchRepository
        when(batchRepository.findAll()).thenReturn(batchDomainList);

        // Configure o comportamento do mock do batchConverter
        when(batchConverter.convertBatchDomainListToResponseList(batchDomainList)).thenReturn(Collections.singletonList(new BatchResponse()));

        // Chame o método getAllBatches do BatchService
        List<BatchResponse> batchResponses = batchService.getAllBatches();

        // Verifique se o método do mock foi chamado corretamente
        verify(batchRepository, times(1)).findAll();

        // Verifique se a lista retornada não é nula e contém exatamente um elemento
        assertNotNull(batchResponses);
        assertEquals(1, batchResponses.size());
        // Verifique outros campos dos BatchResponse conforme necessário
    }

    @Test
    void testGetBatchById() {
        // Configure o comportamento do mock do batchRepository
        when(batchRepository.findById(anyString())).thenReturn(java.util.Optional.ofNullable(batchDomain));

        // Configure o comportamento do mock do batchConverter
        when(batchConverter.convertBatchDomainToResponse(batchDomain)).thenReturn(new BatchResponse());

        // Chame o método getBatchById do BatchService
        BatchResponse batchResponse = batchService.getBatchById("1");

        // Verifique se o método do mock foi chamado corretamente
        verify(batchRepository, times(1)).findById("1");

        // Verifique se o BatchResponse retornado é o esperado
        assertNotNull(batchResponse);
        // Verifique outros campos do BatchResponse conforme necessário
    }

    @Test
    void testUpdateBatch() {
        // Configure o comportamento do mock do batchRepository
        when(batchRepository.findById(anyString())).thenReturn(java.util.Optional.ofNullable(batchDomain));
        when(batchRepository.save(any(BatchDomain.class))).thenReturn(batchDomain);

        // Configure o comportamento do mock do batchConverter
        when(batchConverter.convertBatchDomainToResponse(batchDomain)).thenReturn(new BatchResponse());

        // Chame o método updateBatch do BatchService
        BatchResponse batchResponse = batchService.updateBatch("1", batchRequest);

        // Verifique se o método do mock foi chamado corretamente
        verify(batchRepository, times(1)).findById("1");
        verify(batchRepository, times(1)).save(batchDomain);

        // Verifique se o BatchResponse retornado é o esperado
        assertNotNull(batchResponse);
        // Verifique outros campos do BatchResponse conforme necessário
    }

    @Test
    void testDeleteBatch() {
        // Chame o método deleteBatch do BatchService
        batchService.deleteBatch("1");

        // Verifique se o método do mock foi chamado corretamente
        verify(batchRepository, times(1)).deleteById("1");
    }
}