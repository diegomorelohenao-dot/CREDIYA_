package co.com.crediya.solicitudes.usecase.registrarsolicitud;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalcularCuotaMensualTest {
    private final CalcularCuotaMensual calcular = new CalcularCuotaMensual();

    @Test
    void calculatesAmortizedInstallment() {
        BigDecimal cuota = calcular.calcular(new BigDecimal("2500000"), new BigDecimal("0.019"), 24);
        assertEquals(0, new BigDecimal("130685.16").compareTo(cuota));
    }

    @Test
    void dividesEquallyWhenRateIsZero() {
        BigDecimal cuota = calcular.calcular(new BigDecimal("1200"), BigDecimal.ZERO, 12);
        assertEquals(0, new BigDecimal("100.00").compareTo(cuota));
    }

    @Test
    void rejectsMissingParameters() {
        assertThrows(IllegalArgumentException.class,
                () -> calcular.calcular(new BigDecimal("1200"), new BigDecimal("0.01"), 0));
    }
}
