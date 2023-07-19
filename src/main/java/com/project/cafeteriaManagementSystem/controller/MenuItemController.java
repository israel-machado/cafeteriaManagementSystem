package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.Material.MaterialResponse;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemRequest;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemResponse;
import com.project.cafeteriaManagementSystem.services.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/menu-item")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getAll() {
        return ResponseEntity.ok().body(menuItemService.getAllMenu());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(@Valid @PathVariable String id) {
        return ResponseEntity.ok().body(menuItemService.getMenuItemById(id));
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> insertMenuItem(@Valid @RequestBody MenuItemRequest menuItemRequest) {
        MenuItemResponse menuItemResponse = menuItemService.createMenuItem(menuItemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@Valid @PathVariable String id,
                                                           @RequestBody MenuItemRequest menuItemRequest) {
        return ResponseEntity.ok().body(menuItemService.updateMenuItem(id, menuItemRequest));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@Valid @PathVariable String id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
