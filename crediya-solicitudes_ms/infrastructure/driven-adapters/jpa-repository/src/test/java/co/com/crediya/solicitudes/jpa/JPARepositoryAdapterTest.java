package co.com.crediya.solicitudes.jpa;

import co.com.crediya.solicitudes.jpa.entity.SolicitudEntity;
import co.com.crediya.solicitudes.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JPARepositoryAdapterTest {
    @Mock
    private JPARepository solicitudRepository;
    @Mock
    private TipoPrestamoJPARepository tipoPrestamoRepository;

    private JPARepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JPARepositoryAdapter(solicitudRepository, tipoPrestamoRepository);
    }

    @Test
    void savesSolicitudAndReturnsGeneratedId() {
        TipoPrestamoEntity tipoEntity = new TipoPrestamoEntity();
        tipoEntity.setId(3L);
        when(tipoPrestamoRepository.getReferenceById(3L)).thenReturn(tipoEntity);
        when(solicitudRepository.save(any(SolicitudEntity.class))).thenAnswer(invocation -> {
            SolicitudEntity entity = invocation.getArgument(0);
            entity.setId(20L);
            return entity;
        });

        Solicitud solicitud = Solicitud.builder()
                .documentoCliente("10000000")
                .monto(new BigDecimal("2500000"))
                .plazoMeses(24)
                .tipoPrestamo(TipoPrestamo.builder()
                        .id(3L)
                        .codigo("LIBRE_INVERSION")
                        .nombre("Crédito de libre inversión")
                        .plazoMinimoMeses(6)
                        .plazoMaximoMeses(60)
                        .activo(true)
                        .build())
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .build();

        Solicitud saved = adapter.save(solicitud);

        assertEquals(20L, saved.getId());
        assertEquals("10000000", saved.getDocumentoCliente());
        assertEquals("LIBRE_INVERSION", saved.getTipoPrestamo().getCodigo());
        assertEquals("Pendiente de revisión", saved.getEstado().getDescripcion());
    }
}