package co.com.crediya.solicitudes.model.solicitud;

import java.math.BigDecimal;

public record SolicitudRevision(
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

    public static SolicitudRevision from(Solicitud solicitud, BigDecimal deudaAprobada) {
        return new SolicitudRevision(
                solicitud.getId(),
                solicitud.getSolicitanteId(),
                solicitud.getDocumentoCliente(),
                solicitud.getNombreCliente(),
                solicitud.getCorreoElectronico(),
                solicitud.getSalarioBase(),
                solicitud.getMonto(),
                solicitud.getPlazoMeses(),
                solicitud.getTipoPrestamo().getCodigo(),
                solicitud.getTipoPrestamo().getNombre(),
                solicitud.getTasaInteres(),
                solicitud.getEstado(),
                deudaAprobada
        );
    }
}
