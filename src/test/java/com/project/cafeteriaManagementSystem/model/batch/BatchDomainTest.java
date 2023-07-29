package com.project.cafeteriaManagementSystem.model.batch;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BatchDomainTest {

    @Test
    void testBatchDomainCompareTo() {
        // Crie dois objetos BatchDomain com validades diferentes
        BatchDomain batch1 = new BatchDomain();
        batch1.setId("1");
        batch1.setValidity(LocalDateTime.of(2023, 7, 1, 12, 0)); // 1º de julho de 2023, 12:00

        BatchDomain batch2 = new BatchDomain();
        batch2.setId("2");
        batch2.setValidity(LocalDateTime.of(2023, 7, 2, 12, 0)); // 2 de julho de 2023, 12:00

        // Verifique se o método compareTo() funciona corretamente
        assertTrue(batch1.compareTo(batch2) < 0); // batch1 é anterior a batch2
        assertTrue(batch2.compareTo(batch1) > 0); // batch2 é posterior a batch1
        assertEquals(0, batch1.compareTo(batch1)); // batch1 é igual a ele mesmo
    }
}
