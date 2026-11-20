package co.com.crediya.solicitudes.jpa;

import co.com.crediya.solicitudes.jpa.entity.SolicitudEntity;
import co.com.crediya.solicitudes.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitudes;
import co.com.crediya.solicitudes.model.solicitud.Pagina;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JPARepositoryAdapter implements SolicitudRepository {
    private final JPARepository solicitudRepository;
    private final TipoPrestamoJPARepository tipoPrestamoRepository;

    @Override
    @Transactional
    public Solicitud save(Solicitud solicitud) {
        TipoPrestamoEntity tipoPrestamo = tipoPrestamoRepository.getReferenceById(
                solicitud.getTipoPrestamo().getId());

        SolicitudEntity entity = new SolicitudEntity();
        entity.setSolicitanteId(solicitud.getSolicitanteId());
        entity.setDocumentoCliente(solicitud.getDocumentoCliente());
        entity.setNombreCliente(solicitud.getNombreCliente());
        entity.setCorreoElectronico(solicitud.getCorreoElectronico());
        entity.setSalarioBase(solicitud.getSalarioBase());
        entity.setMonto(solicitud.getMonto());
        entity.setPlazoMeses(solicitud.getPlazoMeses());
        entity.setTipoPrestamo(tipoPrestamo);
        entity.setTasaInteres(solicitud.getTasaInteres());
        entity.setCuotaMensual(solicitud.getCuotaMensual());
        entity.setEstado(solicitud.getEstado());

        return toDomain(solicitudRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Pagina<Solicitud> findAll(FiltroSolicitudes filtro, int pagina, int tamano) {
        var pageable = PageRequest.of(pagina, tamano, Sort.by(Sort.Direction.DESC, "id"));
        var codigoTipoPrestamo = filtro.codigoTipoPrestamo() == null
                ? null
                : filtro.codigoTipoPrestamo().toUpperCase();
        var resultado = solicitudRepository.findAllFiltered(
                filtro.estado(), codigoTipoPrestamo, filtro.documentoCliente(), pageable);

        List<Solicitud> contenido = resultado.getContent().stream().map(this::toDomain).toList();
        return new Pagina<>(contenido, resultado.getNumber(), resultado.getSize(),
                resultado.getTotalElements(), resultado.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumarCuotasAprobadas(Long solicitanteId) {
        BigDecimal total = solicitudRepository.sumarCuotasAprobadas(solicitanteId, EstadoSolicitud.APROBADA);
        return total == null ? BigDecimal.ZERO : total;
    }

    private Solicitud toDomain(SolicitudEntity entity) {
        TipoPrestamoEntity tipo = entity.getTipoPrestamo();
        return Solicitud.builder()
                .id(entity.getId())
                .solicitanteId(entity.getSolicitanteId())
                .documentoCliente(entity.getDocumentoCliente())
                .nombreCliente(entity.getNombreCliente())
                .correoElectronico(entity.getCorreoElectronico())
                .salarioBase(entity.getSalarioBase())
                .monto(entity.getMonto())
                .plazoMeses(entity.getPlazoMeses())
                .tipoPrestamo(TipoPrestamo.builder()
                        .id(tipo.getId())
                        .codigo(tipo.getCodigo())
                        .nombre(tipo.getNombre())
                        .plazoMinimoMeses(tipo.getPlazoMinimoMeses())
                        .plazoMaximoMeses(tipo.getPlazoMaximoMeses())
                        .tasaInteres(tipo.getTasaInteres())
                        .activo(tipo.isActivo())
                        .build())
                .tasaInteres(entity.getTasaInteres())
                .cuotaMensual(entity.getCuotaMensual())
                .estado(entity.getEstado())
                .build();
    }
}
