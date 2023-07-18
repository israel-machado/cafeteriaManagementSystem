package com.project.cafeteriaManagementSystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Exceções genéricas

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorDetail> handleInvalidDataException(InvalidDataException ex) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorDetail.setMessage("Invalid data: " + ex.getMessage());

        return ResponseEntity.badRequest().body(errorDetail);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorDetail> handleInsufficientStockException(InsufficientStockException ex) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setStatusCode(HttpStatus.CONFLICT.value());
        errorDetail.setMessage("Insufficient stock: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetail);
    }

    // Exceções específicas

    @ExceptionHandler(InvalidMaterialDataException.class)
    public ResponseEntity<ErrorDetail> handleInvalidMaterialDataException(InvalidMaterialDataException ex) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorDetail.setMessage("Invalid material data: " + ex.getMessage());

        return ResponseEntity.badRequest().body(errorDetail);
    }

    @ExceptionHandler(InsufficientMaterialStockException.class)
    public ResponseEntity<ErrorDetail> handleInsufficientMaterialStockException(InsufficientMaterialStockException ex) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setStatusCode(HttpStatus.CONFLICT.value());
        errorDetail.setMessage("Insufficient material stock: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetail);
    }

    @ExceptionHandler(InvalidMaterialDataException.class)
    public ResponseEntity<ErrorDetail> handleInvalidMenuItemDataException(InvalidMenuItemDataException ex) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorDetail.setMessage("Invalid menu item data: " + ex.getMessage());

        return ResponseEntity.badRequest().body(errorDetail);
    }
}
