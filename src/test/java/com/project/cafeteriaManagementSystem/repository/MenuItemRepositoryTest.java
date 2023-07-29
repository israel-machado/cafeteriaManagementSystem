package com.project.cafeteriaManagementSystem.repository;

import com.project.cafeteriaManagementSystem.model.menuItem.MenuItemDomainTest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MenuItemRepositoryTest extends MongoRepository<MenuItemDomainTest, String> {
}