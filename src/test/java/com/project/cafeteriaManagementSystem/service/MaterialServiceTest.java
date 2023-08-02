package com.project.cafeteriaManagementSystem.service;

import com.project.cafeteriaManagementSystem.exception.InvalidDataException;
import com.project.cafeteriaManagementSystem.exception.InvalidMaterialDataException;
import com.project.cafeteriaManagementSystem.mapping.BatchConverter;
import com.project.cafeteriaManagementSystem.mapping.MaterialConverter;
import com.project.cafeteriaManagementSystem.model.batch.BatchDomain;
import com.project.cafeteriaManagementSystem.model.batch.BatchResponse;
import com.project.cafeteriaManagementSystem.model.material.MaterialDomain;
import com.project.cafeteriaManagementSystem.model.material.MaterialMinimumStockRequest;
import com.project.cafeteriaManagementSystem.model.material.MaterialRequest;
import com.project.cafeteriaManagementSystem.model.material.MaterialResponse;
import com.project.cafeteriaManagementSystem.repository.MaterialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private MaterialConverter materialConverter;

    @InjectMocks
    private MaterialService materialService;

    @Test
    void testGetAllMaterials() {
        // Criar um mock de lista de MaterialDomain para o repositório retornar
        List<MaterialDomain> mockMaterialList = new ArrayList<>();
        mockMaterialList.add(new MaterialDomain());
        // Configurar o comportamento do repositório para retornar a lista mockMaterialList
        when(materialRepository.findAll()).thenReturn(mockMaterialList);

        // Criar um mock de lista de MaterialResponse para o converter retornar
        List<MaterialResponse> mockMaterialResponseList = new ArrayList<>();
        mockMaterialResponseList.add(new MaterialResponse());
        // Configurar o comportamento do converter para retornar a lista mockMaterialResponseList
        when(materialConverter.convertMaterialDomainListToResponseList(anyList())).thenReturn(mockMaterialResponseList);

        // Chamar o método do serviço
        List<MaterialResponse> result = materialService.getAllMaterials();

        // Verificar se o repositório foi chamado corretamente
        verify(materialRepository, times(1)).findAll();

        // Verificar se o converter foi chamado corretamente
        verify(materialConverter, times(1)).convertMaterialDomainListToResponseList(eq(mockMaterialList));

        // Verificar o resultado
        assertEquals(mockMaterialResponseList, result);
    }

    @Test
    void testGetMaterialById() {
        // Criar um mock de MaterialDomain para o repositório retornar
        MaterialDomain mockMaterialDomain = new MaterialDomain();
        // Configurar o comportamento do repositório para retornar o mockMaterialDomain
        when(materialRepository.findById(anyString())).thenReturn(Optional.of(mockMaterialDomain));

        // Criar um mock de MaterialResponse para o converter retornar
        MaterialResponse mockMaterialResponse = new MaterialResponse();
        // Configurar o comportamento do converter para retornar o mockMaterialResponse
        when(materialConverter.convertMaterialDomainToResponse(any())).thenReturn(mockMaterialResponse);

        // Chamar o método do serviço
        MaterialResponse result = materialService.getMaterialById("someId");

        // Verificar se o repositório foi chamado corretamente com o ID correto
        verify(materialRepository, times(1)).findById(eq("someId"));

        // Verificar se o converter foi chamado corretamente
        verify(materialConverter, times(1)).convertMaterialDomainToResponse(eq(mockMaterialDomain));

        // Verificar o resultado
        assertEquals(mockMaterialResponse, result);
    }

    @Test
    void testCreateMaterial() {
        // Preparar os dados mockados
        MaterialRequest mockMaterialRequest = new MaterialRequest();
        MaterialDomain mockMaterialDomain = new MaterialDomain();
        MaterialResponse mockMaterialResponse = new MaterialResponse();

        // Configurar o comportamento do materialRepository usando isNull()
        when(materialRepository.save(isNull())).thenReturn(mockMaterialDomain);

        // Chamar o método no serviço
        MaterialDomain result = materialService.createMaterial(mockMaterialRequest);

        // Verificar se o método do converter foi chamado com o MaterialRequest correto
        verify(materialConverter, times(1)).convertMaterialRequestToDomain(eq(mockMaterialRequest));

        // Verificar se o método do repositório foi chamado para salvar o material com argumento nulo
        verify(materialRepository, times(1)).save(isNull());

        // Verificar o resultado
        assertEquals(mockMaterialDomain, result);
    }

    @Test
    void testUpdateMaterial() {
        // Preparar os dados mockados
        String materialId = "someId";
        MaterialRequest mockMaterialRequest = new MaterialRequest();
        MaterialDomain mockMaterialDomain = new MaterialDomain();
        MaterialResponse mockMaterialResponse = new MaterialResponse();

        // Configurar o comportamento do materialConverter
        when(materialConverter.convertMaterialRequestToDomain(eq(mockMaterialRequest))).thenReturn(mockMaterialDomain);
        when(materialConverter.convertMaterialDomainToResponse(eq(mockMaterialDomain))).thenReturn(mockMaterialResponse);

        // Configurar o comportamento do materialRepository
        when(materialRepository.save(eq(mockMaterialDomain))).thenReturn(mockMaterialDomain);
        when(materialRepository.findById(eq(materialId))).thenReturn(Optional.of(mockMaterialDomain));

        // Capturar o argumento do método save do materialRepository
        ArgumentCaptor<MaterialDomain> materialDomainCaptor = ArgumentCaptor.forClass(MaterialDomain.class);

        // Chamar o método no serviço
        MaterialResponse result = materialService.updateMaterial(materialId, mockMaterialRequest);

        // Verificar se o método do repositório foi chamado com o ID correto
        verify(materialRepository, times(1)).findById(eq(materialId));

        // Verificar se os métodos do converter foram chamados corretamente
        verify(materialConverter, times(1)).convertMaterialRequestToDomain(eq(mockMaterialRequest));
        verify(materialConverter, times(1)).convertMaterialDomainToResponse(eq(mockMaterialDomain));

        // Verificar se o método do repositório foi chamado para salvar o material atualizado
        verify(materialRepository, times(1)).save(materialDomainCaptor.capture());

        // Capturar o objeto passado para o método save e verificar se é o mesmo objeto mockMaterialDomain
        MaterialDomain capturedMaterialDomain = materialDomainCaptor.getValue();
        assertEquals(mockMaterialDomain, capturedMaterialDomain);

        // Verificar o resultado
        assertEquals(mockMaterialResponse, result);
    }

    @Test
    void testDeleteMaterial_WhenMaterialExists() {
        // Preparação
        String materialId = "exampleMaterialId";
        MaterialDomain materialDomain = new MaterialDomain();
        when(materialRepository.existsById(materialId)).thenReturn(true);
        doNothing().when(materialRepository).deleteById(materialId);

        // Execução e verificação
        assertDoesNotThrow(() -> materialService.deleteMaterial(materialId));
    }

    @Test
    void testDeleteMaterial_WhenMaterialNotExists() {
        // Preparação
        String materialId = "nonExistentMaterialId";
        when(materialRepository.existsById(materialId)).thenReturn(false);

        // Execução e verificação
        InvalidMaterialDataException exception = assertThrows(
                InvalidMaterialDataException.class,
                () -> materialService.deleteMaterial(materialId)
        );
        assertEquals("Material não encontrado.", exception.getMessage());
    }

    //TODO Expiring

    //TODO UpdateMinimumStock

    //TODO LowStock
}