package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;

public final class SolicitudApiMapper {
    private SolicitudApiMapper() {
    }

    public static Solicitud toDomain(SolicitudRequest request) {
        return Solicitud.builder()
                .documentoCliente(request.getDocumentoCliente())
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
                .documentoCliente(solicitud.getDocumentoCliente())
                .monto(solicitud.getMonto())
                .plazoMeses(solicitud.getPlazoMeses())
                .codigoTipoPrestamo(tipoPrestamo.getCodigo())
                .nombreTipoPrestamo(tipoPrestamo.getNombre())
                .estado(solicitud.getEstado().getDescripcion())
                .build();
    }
}