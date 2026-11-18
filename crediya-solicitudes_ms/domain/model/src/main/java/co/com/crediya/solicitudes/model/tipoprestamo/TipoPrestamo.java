package co.com.crediya.solicitudes.model.tipoprestamo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TipoPrestamo {
    private final Long id;
    private final String codigo;
    private final String nombre;
    private final Integer plazoMinimoMeses;
    private final Integer plazoMaximoMeses;
    private final boolean activo;
}