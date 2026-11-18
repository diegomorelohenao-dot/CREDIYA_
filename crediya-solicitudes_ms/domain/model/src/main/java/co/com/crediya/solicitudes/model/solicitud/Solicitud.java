package co.com.crediya.solicitudes.model.solicitud;

import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class Solicitud {
    private final Long id;
    private final String documentoCliente;
    private final BigDecimal monto;
    private final Integer plazoMeses;
    private final TipoPrestamo tipoPrestamo;
    private final EstadoSolicitud estado;
}