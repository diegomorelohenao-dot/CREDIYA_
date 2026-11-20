package co.com.crediya.solicitudes.jpa;

import co.com.crediya.solicitudes.jpa.entity.SolicitudEntity;
import co.com.crediya.solicitudes.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitudes;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JPARepositoryAdapterTest {
    @Mock private JPARepository solicitudRepository;
    @Mock private TipoPrestamoJPARepository tipoPrestamoRepository;

    private JPARepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JPARepositoryAdapter(solicitudRepository, tipoPrestamoRepository);
    }

    @Test
    void savesSolicitudWithSnapshotAndEnumState() {
        TipoPrestamoEntity tipoEntity = tipoEntity();
        when(tipoPrestamoRepository.getReferenceById(3L)).thenReturn(tipoEntity);
        when(solicitudRepository.save(any(SolicitudEntity.class))).thenAnswer(invocation -> {
            SolicitudEntity entity = invocation.getArgument(0);
            entity.setId(20L);
            return entity;
        });

        Solicitud saved = adapter.save(solicitud());

        assertEquals(20L, saved.getId());
        assertEquals("10000000", saved.getDocumentoCliente());
        assertEquals("Ana María Gómez", saved.getNombreCliente());
        assertEquals("ana@example.com", saved.getCorreoElectronico());
        assertEquals(EstadoSolicitud.PENDIENTE_REVISION, saved.getEstado());
        assertEquals(new BigDecimal("0.019000"), saved.getTasaInteres());
        assertEquals(new BigDecimal("130685.16"), saved.getCuotaMensual());
        assertEquals("LIBRE_INVERSION", saved.getTipoPrestamo().getCodigo());
    }

    @Test
    void mapsFilteredPageToDomain() {
        SolicitudEntity entity = entity(7L);
        when(solicitudRepository.findAllFiltered(eq(null), eq(null), eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));

        var pagina = adapter.findAll(FiltroSolicitudes.todos(), 0, 20);

        assertEquals(1, pagina.contenido().size());
        assertEquals(7L, pagina.contenido().getFirst().getId());
        assertEquals(EstadoSolicitud.PENDIENTE_REVISION, pagina.contenido().getFirst().getEstado());
    }

    @Test
    void normalizesLoanTypeFilterToUpperCase() {
        var filtro = new FiltroSolicitudes(null, "libre_inversion", null);
        when(solicitudRepository.findAllFiltered(eq(null), eq("LIBRE_INVERSION"), eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        adapter.findAll(filtro, 0, 20);

        org.mockito.Mockito.verify(solicitudRepository)
                .findAllFiltered(eq(null), eq("LIBRE_INVERSION"), eq(null), any(Pageable.class));
    }

    @Test
    void returnsZeroWhenThereIsNoApprovedDebt() {
        when(solicitudRepository.sumarCuotasAprobadas(any(), eq(EstadoSolicitud.APROBADA))).thenReturn(null);
        assertEquals(0, BigDecimal.ZERO.compareTo(adapter.sumarCuotasAprobadas(15L)));
    }

    private TipoPrestamoEntity tipoEntity() {
        TipoPrestamoEntity tipo = new TipoPrestamoEntity();
        tipo.setId(3L);
        tipo.setCodigo("LIBRE_INVERSION");
        tipo.setNombre("Crédito de libre inversión");
        tipo.setPlazoMinimoMeses(6);
        tipo.setPlazoMaximoMeses(60);
        tipo.setTasaInteres(new BigDecimal("0.019000"));
        tipo.setActivo(true);
        return tipo;
    }

    private SolicitudEntity entity(Long id) {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setId(id);
        entity.setSolicitanteId(15L);
        entity.setDocumentoCliente("10000000");
        entity.setNombreCliente("Ana María Gómez");
        entity.setCorreoElectronico("ana@example.com");
        entity.setSalarioBase(new BigDecimal("2500000"));
        entity.setMonto(new BigDecimal("2500000"));
        entity.setPlazoMeses(24);
        entity.setTipoPrestamo(tipoEntity());
        entity.setTasaInteres(new BigDecimal("0.019000"));
        entity.setCuotaMensual(new BigDecimal("130685.16"));
        entity.setEstado(EstadoSolicitud.PENDIENTE_REVISION);
        return entity;
    }

    private Solicitud solicitud() {
        return Solicitud.builder()
                .solicitanteId(15L)
                .documentoCliente("10000000")
                .nombreCliente("Ana María Gómez")
                .correoElectronico("ana@example.com")
                .salarioBase(new BigDecimal("2500000"))
                .monto(new BigDecimal("2500000"))
                .plazoMeses(24)
                .tipoPrestamo(TipoPrestamo.builder()
                        .id(3L)
                        .codigo("LIBRE_INVERSION")
                        .nombre("Crédito de libre inversión")
                        .plazoMinimoMeses(6)
                        .plazoMaximoMeses(60)
                        .tasaInteres(new BigDecimal("0.019000"))
                        .activo(true)
                        .build())
                .tasaInteres(new BigDecimal("0.019000"))
                .cuotaMensual(new BigDecimal("130685.16"))
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .build();
    }
}
