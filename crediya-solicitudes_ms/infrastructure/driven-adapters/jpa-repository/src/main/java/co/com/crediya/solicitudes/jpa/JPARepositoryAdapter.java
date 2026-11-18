package co.com.crediya.solicitudes.jpa;

import co.com.crediya.solicitudes.jpa.entity.SolicitudEntity;
import co.com.crediya.solicitudes.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JPARepositoryAdapter implements SolicitudRepository {
    private final JPARepository solicitudRepository;
    private final TipoPrestamoJPARepository tipoPrestamoRepository;

    @Override
    @Transactional
    public Solicitud save(Solicitud solicitud) {
        TipoPrestamoEntity tipoPrestamo = tipoPrestamoRepository.getReferenceById(
                solicitud.getTipoPrestamo().getId()
        );

        SolicitudEntity entity = new SolicitudEntity();
        entity.setDocumentoCliente(solicitud.getDocumentoCliente());
        entity.setMonto(solicitud.getMonto());
        entity.setPlazoMeses(solicitud.getPlazoMeses());
        entity.setTipoPrestamo(tipoPrestamo);
        entity.setEstado(solicitud.getEstado().getDescripcion());

        SolicitudEntity saved = solicitudRepository.save(entity);
        return Solicitud.builder()
                .id(saved.getId())
                .documentoCliente(saved.getDocumentoCliente())
                .monto(saved.getMonto())
                .plazoMeses(saved.getPlazoMeses())
                .tipoPrestamo(solicitud.getTipoPrestamo())
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .build();
    }
}