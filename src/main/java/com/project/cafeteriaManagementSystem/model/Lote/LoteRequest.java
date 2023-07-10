package com.project.cafeteriaManagementSystem.model.Lote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoteRequest {

    private LocalDate validity;
}
