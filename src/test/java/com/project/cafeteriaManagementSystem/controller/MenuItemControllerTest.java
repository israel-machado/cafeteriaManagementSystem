package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDetailedResponseTest;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemRequestTest;
import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemResponseTest;
import com.project.cafeteriaManagementSystem.service.MenuItemServiceTest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menu-item")
public class MenuItemControllerTest {

    private final MenuItemServiceTest menuItemServiceTest;

    // Método para obter todos os itens do cardápio
    @GetMapping
    public ResponseEntity<List<MenuItemResponseTest>> getAll() {
        // Chama o serviço para obter todos os itens do cardápio e retorna uma resposta HTTP 200 OK com a lista de MenuItemResponse no corpo da resposta
        return ResponseEntity.ok().body(menuItemServiceTest.getAllMenu());
    }

    // Método para obter uma lista simplificada de itens do cardápio que possuem materiais suficientes em estoque
    @GetMapping("/customer")
    public ResponseEntity<List<MenuItemResponseTest>> getSimplifiedMenuItems() {
        // Chama o serviço para obter uma lista simplificada de itens do cardápio que possuem materiais suficientes em estoque e retorna uma resposta HTTP 200 OK com essa lista no corpo da resposta
        return ResponseEntity.ok().body(menuItemServiceTest.getSimplifiedMenuItems());
    }

    // Método para obter uma lista detalhada de itens do cardápio
    @GetMapping("/staff")
    public ResponseEntity<List<MenuItemDetailedResponseTest>> getDetailedMenuItems() {
        // Chama o serviço para obter uma lista detalhada de itens do cardápio e retorna uma resposta HTTP 200 OK com essa lista no corpo da resposta
        return ResponseEntity.ok().body(menuItemServiceTest.getAllMenuItemsWithDetails());
    }

    // Método para obter um item do cardápio pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponseTest> getMenuItemById(@PathVariable String id) {
        // Chama o serviço para obter um item do cardápio pelo ID e retorna uma resposta HTTP 200 OK com o MenuItemResponse no corpo da resposta
        return ResponseEntity.ok().body(menuItemServiceTest.getMenuItemById(id));
    }

    // Método para inserir um novo item no cardápio
    @PostMapping
    public ResponseEntity<MenuItemResponseTest> createMenuItem(@Valid @RequestBody MenuItemRequestTest menuItemRequestTest) {
        // Chama o serviço para inserir um novo item no cardápio e retorna uma resposta HTTP 201 Created com o MenuItemResponse criado no corpo da resposta
        MenuItemResponseTest menuItemResponseTest = menuItemServiceTest.createMenuItem(menuItemRequestTest);
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemResponseTest);
    }

    // Método para atualizar um item do cardápio pelo ID
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponseTest> updateMenuItem(@PathVariable String id,
                                                               @Valid @RequestBody MenuItemRequestTest menuItemRequestTest) {
        // Chama o serviço para atualizar um item do cardápio pelo ID e retorna uma resposta HTTP 200 OK com o MenuItemResponse atualizado no corpo da resposta
        return ResponseEntity.ok().body(menuItemServiceTest.updateMenuItem(id, menuItemRequestTest));
    }

    // Método para excluir um item do cardápio pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable String id) {
        // Chama o serviço para excluir um item do cardápio pelo ID e retorna uma resposta HTTP 204 No Content, indicando que a operação foi bem-sucedida, mas não há conteúdo para retornar no corpo da resposta
        menuItemServiceTest.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
