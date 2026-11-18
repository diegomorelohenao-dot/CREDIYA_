package co.com.crediya.solicitudes.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class SolicitudResponse {
    private final Long id;
    private final String documentoCliente;
    private final BigDecimal monto;
    private final Integer plazoMeses;
    private final String codigoTipoPrestamo;
    private final String nombreTipoPrestamo;
    private final String estado;
}