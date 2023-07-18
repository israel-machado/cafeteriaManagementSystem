package com.project.cafeteriaManagementSystem.exceptions;

public class InvalidMenuItemDataException extends RuntimeException {
    public InvalidMenuItemDataException(String message) {
        super(message);
    }
}