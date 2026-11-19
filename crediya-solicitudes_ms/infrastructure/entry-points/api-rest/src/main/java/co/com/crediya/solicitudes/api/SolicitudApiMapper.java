package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.security.JwtAuthenticatedUser;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;

public final class SolicitudApiMapper {
    private SolicitudApiMapper() {
    }

    /**
     * El solicitante y su correo se derivan del JWT; no se aceptan en el cuerpo
     * para impedir que un cliente radique solicitudes a nombre de otro.
     */
    public static Solicitud toDomain(SolicitudRequest request, JwtAuthenticatedUser autenticado) {
        return Solicitud.builder()
                .solicitanteId(autenticado.id())
                .documentoCliente(request.getDocumentoCliente())
                .nombreCliente(request.getNombreCliente())
                .correoElectronico(autenticado.correoElectronico())
                .salarioBase(request.getSalarioBase())
                .monto(request.getMonto())
                .plazoMeses(request.getPlazoMeses())
                .tipoPrestamo(TipoPrestamo.builder()
                        .codigo(request.getCodigoTipoPrestamo())
                        .build())
                .build();
    }

    public static SolicitudResponse toResponse(Solicitud solicitud) {
        TipoPrestamo tipoPrestamo = solicitud.getTipoPrestamo();
        return SolicitudResponse.builder()
                .id(solicitud.getId())
                .solicitanteId(solicitud.getSolicitanteId())
                .documentoCliente(solicitud.getDocumentoCliente())
                .nombreCliente(solicitud.getNombreCliente())
                .correoElectronico(solicitud.getCorreoElectronico())
                .salarioBase(solicitud.getSalarioBase())
                .monto(solicitud.getMonto())
                .plazoMeses(solicitud.getPlazoMeses())
                .codigoTipoPrestamo(tipoPrestamo.getCodigo())
                .nombreTipoPrestamo(tipoPrestamo.getNombre())
                .tasaInteres(solicitud.getTasaInteres())
                .cuotaMensual(solicitud.getCuotaMensual())
                .estado(solicitud.getEstado().name())
                .build();
    }
}
