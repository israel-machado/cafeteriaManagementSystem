package com.project.cafeteriaManagementSystem.model.Lote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoteRequest {

    @NotNull(message = "A data de validade do lote é obrigatória")
    @Future(message = "A data de validade do lote deve ser uma data futura")
    private LocalDate validity;
}
