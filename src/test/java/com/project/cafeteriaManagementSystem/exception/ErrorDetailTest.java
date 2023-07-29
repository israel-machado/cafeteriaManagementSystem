package com.project.cafeteriaManagementSystem.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDetailTest {
    private int statusCode;
    private String message;
}
