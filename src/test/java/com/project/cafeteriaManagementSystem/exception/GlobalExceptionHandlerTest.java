package com.project.cafeteriaManagementSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandlerTest {

    // Exceções genéricas

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorDetailTest> handleInvalidDataException(InvalidDataException ex) {
        ErrorDetailTest errorDetailTest = new ErrorDetailTest();
        errorDetailTest.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorDetailTest.setMessage("Invalid data: " + ex.getMessage());

        return ResponseEntity.badRequest().body(errorDetailTest);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorDetailTest> handleInsufficientStockException(InsufficientStockException ex) {
        ErrorDetailTest errorDetailTest = new ErrorDetailTest();
        errorDetailTest.setStatusCode(HttpStatus.CONFLICT.value());
        errorDetailTest.setMessage("Insufficient stock: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetailTest);
    }

    // Exceções específicas

    @ExceptionHandler(InvalidMaterialDataException.class)
    public ResponseEntity<ErrorDetailTest> handleInvalidMaterialDataException(InvalidMaterialDataException ex) {
        ErrorDetailTest errorDetailTest = new ErrorDetailTest();
        errorDetailTest.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorDetailTest.setMessage("Invalid material data: " + ex.getMessage());

        return ResponseEntity.badRequest().body(errorDetailTest);
    }

    @ExceptionHandler(InsufficientMaterialStockException.class)
    public ResponseEntity<ErrorDetailTest> handleInsufficientMaterialStockException(InsufficientMaterialStockException ex) {
        ErrorDetailTest errorDetailTest = new ErrorDetailTest();
        errorDetailTest.setStatusCode(HttpStatus.CONFLICT.value());
        errorDetailTest.setMessage("Insufficient material stock: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetailTest);
    }

    @ExceptionHandler(InvalidMenuItemDataException.class)
    public ResponseEntity<ErrorDetailTest> handleInvalidMenuItemDataException(InvalidMenuItemDataException ex) {
        ErrorDetailTest errorDetailTest = new ErrorDetailTest();
        errorDetailTest.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorDetailTest.setMessage("Invalid menu item data: " + ex.getMessage());

        return ResponseEntity.badRequest().body(errorDetailTest);
    }
}
