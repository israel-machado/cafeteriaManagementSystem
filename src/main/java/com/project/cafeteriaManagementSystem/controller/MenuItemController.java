package com.project.cafeteriaManagementSystem.controller;

import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemRequest;
import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemResponse;
import com.project.cafeteriaManagementSystem.services.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/menu-item")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItemResponse> insertMenuItem(@Valid @RequestBody MenuItemRequest menuItemRequest) {
        MenuItemResponse menuItemResponse = menuItemService.createMenuItem(menuItemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemResponse);
    }

    @PutMapping("{/id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@PathVariable String id, @RequestBody MenuItemRequest menuItemRequest) {
        return ResponseEntity.ok().body(menuItemService.updateMenuItem(id, menuItemRequest));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable String id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
