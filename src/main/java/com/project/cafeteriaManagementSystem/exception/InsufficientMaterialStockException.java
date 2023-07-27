package com.project.cafeteriaManagementSystem.exception;

public class InsufficientMaterialStockException extends RuntimeException {
    public InsufficientMaterialStockException(String message) {
        super(message);
    }
}