package co.com.crediya.solicitudes.usecase.registrarsolicitud;

import co.com.crediya.solicitudes.model.exceptions.PlazoPrestamoInvalidoException;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidarPlazoSolicitudTest {
    private final ValidarPlazoSolicitud validator = new ValidarPlazoSolicitud();

    @Test
    void acceptsTermsAtBothLimits() {
        TipoPrestamo type = loanType(6, 60);

        assertDoesNotThrow(() -> validator.validar(6, type));
        assertDoesNotThrow(() -> validator.validar(60, type));
    }

    @Test
    void rejectsTermsOutsideLimits() {
        assertThrows(PlazoPrestamoInvalidoException.class,
                () -> validator.validar(5, loanType(6, 60)));
    }

    private TipoPrestamo loanType(int min, int max) {
        return TipoPrestamo.builder()
                .codigo("LIBRE_INVERSION")
                .nombre("Crédito de libre inversión")
                .plazoMinimoMeses(min)
                .plazoMaximoMeses(max)
                .activo(true)
                .build();
    }
}