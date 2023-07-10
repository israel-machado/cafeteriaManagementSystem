package com.project.cafeteriaManagementSystem.exceptions;

public class InsufficientMaterialStockException extends RuntimeException {
    public InsufficientMaterialStockException(String message) {
        super(message);
    }
}