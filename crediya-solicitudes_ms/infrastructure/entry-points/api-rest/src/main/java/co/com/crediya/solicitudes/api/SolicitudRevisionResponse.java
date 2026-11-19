package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import co.com.crediya.solicitudes.model.solicitud.SolicitudRevision;

import java.math.BigDecimal;

public record SolicitudRevisionResponse(
        Long id,
        Long solicitanteId,
        String documentoCliente,
        String nombreCliente,
        String correoElectronico,
        BigDecimal salarioBase,
        BigDecimal monto,
        Integer plazoMeses,
        String codigoTipoPrestamo,
        String nombreTipoPrestamo,
        BigDecimal tasaInteres,
        EstadoSolicitud estado,
        BigDecimal deudaTotalMensualSolicitudesAprobadas) {

    public static SolicitudRevisionResponse from(SolicitudRevision revision) {
        return new SolicitudRevisionResponse(
                revision.id(),
                revision.solicitanteId(),
                revision.documentoCliente(),
                revision.nombreCliente(),
                revision.correoElectronico(),
                revision.salarioBase(),
                revision.monto(),
                revision.plazoMeses(),
                revision.codigoTipoPrestamo(),
                revision.nombreTipoPrestamo(),
                revision.tasaInteres(),
                revision.estado(),
                revision.deudaTotalMensualSolicitudesAprobadas()
        );
    }
}
