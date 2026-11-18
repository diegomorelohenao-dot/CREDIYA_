package co.com.crediya.solicitudes.usecase.registrarsolicitud;

import co.com.crediya.solicitudes.model.exceptions.PlazoPrestamoInvalidoException;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;

public class ValidarPlazoSolicitud {

    public void validar(Integer plazoMeses, TipoPrestamo tipoPrestamo) {
        if (plazoMeses < tipoPrestamo.getPlazoMinimoMeses()
                || plazoMeses > tipoPrestamo.getPlazoMaximoMeses()) {
            throw new PlazoPrestamoInvalidoException(
                    "El plazo para " + tipoPrestamo.getNombre() + " debe estar entre "
                            + tipoPrestamo.getPlazoMinimoMeses() + " y "
                            + tipoPrestamo.getPlazoMaximoMeses() + " meses."
            );
        }
    }
}