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
    private final Long solicitanteId;
    private final String documentoCliente;
    private final String nombreCliente;
    private final String correoElectronico;
    private final BigDecimal salarioBase;
    private final BigDecimal monto;
    private final Integer plazoMeses;
    private final String codigoTipoPrestamo;
    private final String nombreTipoPrestamo;
    private final BigDecimal tasaInteres;
    private final BigDecimal cuotaMensual;
    private final String estado;
}
