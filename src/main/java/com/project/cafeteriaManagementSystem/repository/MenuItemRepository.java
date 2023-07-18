package com.project.cafeteriaManagementSystem.repository;

import com.project.cafeteriaManagementSystem.model.MenuItem.MenuItemDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MenuItemRepository extends MongoRepository<MenuItemDomain, String> {
}