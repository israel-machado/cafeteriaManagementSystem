package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDetailedResponse;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemRequest;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemResponse;
import com.project.cafeteriaManagementSystem.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    // Método para obter todos os itens do cardápio
    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getAll() {
        // Chama o serviço para obter todos os itens do cardápio e retorna uma resposta HTTP 200 OK com a lista de MenuItemResponse no corpo da resposta
        return ResponseEntity.ok().body(menuItemService.getAllMenu());
    }

    // Método para obter uma lista simplificada de itens do cardápio que possuem materiais suficientes em estoque
    @GetMapping("/customers")
    public ResponseEntity<List<MenuItemResponse>> getSimplifiedMenuItems() {
        // Chama o serviço para obter uma lista simplificada de itens do cardápio que possuem materiais suficientes em estoque e retorna uma resposta HTTP 200 OK com essa lista no corpo da resposta
        return ResponseEntity.ok().body(menuItemService.getSimplifiedMenuItems());
    }

    // Método para obter uma lista detalhada de itens do cardápio
    @GetMapping("/staff")
    public ResponseEntity<List<MenuItemDetailedResponse>> getDetailedMenuItems() {
        // Chama o serviço para obter uma lista detalhada de itens do cardápio e retorna uma resposta HTTP 200 OK com essa lista no corpo da resposta
        return ResponseEntity.ok().body(menuItemService.getAllMenuItemsWithDetails());
    }

    // Método para obter um item do cardápio pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(@PathVariable String id) {
        // Chama o serviço para obter um item do cardápio pelo ID e retorna uma resposta HTTP 200 OK com o MenuItemResponse no corpo da resposta
        return ResponseEntity.ok().body(menuItemService.getMenuItemById(id));
    }

    // Método para inserir um novo item no cardápio
    @PostMapping
    public ResponseEntity<MenuItemResponse> insertMenuItem(@Valid @RequestBody MenuItemRequest menuItemRequest) {
        // Chama o serviço para inserir um novo item no cardápio e retorna uma resposta HTTP 201 Created com o MenuItemResponse criado no corpo da resposta
        MenuItemResponse menuItemResponse = menuItemService.createMenuItem(menuItemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemResponse);
    }

    // Método para atualizar um item do cardápio pelo ID
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@PathVariable String id,
                                                           @Valid @RequestBody MenuItemRequest menuItemRequest) {
        // Chama o serviço para atualizar um item do cardápio pelo ID e retorna uma resposta HTTP 200 OK com o MenuItemResponse atualizado no corpo da resposta
        return ResponseEntity.ok().body(menuItemService.updateMenuItem(id, menuItemRequest));
    }

    // Método para excluir um item do cardápio pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable String id) {
        // Chama o serviço para excluir um item do cardápio pelo ID e retorna uma resposta HTTP 204 No Content, indicando que a operação foi bem-sucedida, mas não há conteúdo para retornar no corpo da resposta
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
