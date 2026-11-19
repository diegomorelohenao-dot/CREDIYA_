package co.com.crediya.solicitudes.model.solicitud;

import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * La solicitud guarda una instantánea de los datos del cliente y de la tasa
 * vigente al momento de radicarla, de modo que el listado de revisión no
 * dependa de llamadas síncronas al microservicio de autenticación.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class Solicitud {
    private final Long id;
    private final Long solicitanteId;
    private final String documentoCliente;
    private final String nombreCliente;
    private final String correoElectronico;
    private final BigDecimal salarioBase;
    private final BigDecimal monto;
    private final Integer plazoMeses;
    private final TipoPrestamo tipoPrestamo;
    private final BigDecimal tasaInteres;
    private final BigDecimal cuotaMensual;
    private final EstadoSolicitud estado;
}
