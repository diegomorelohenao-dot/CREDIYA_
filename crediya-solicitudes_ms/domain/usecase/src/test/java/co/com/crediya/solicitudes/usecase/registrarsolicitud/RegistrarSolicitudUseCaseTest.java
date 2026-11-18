package co.com.crediya.solicitudes.usecase.registrarsolicitud;

import co.com.crediya.solicitudes.model.exceptions.PlazoPrestamoInvalidoException;
import co.com.crediya.solicitudes.model.exceptions.TipoPrestamoNoEncontradoException;
import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarSolicitudUseCaseTest {
    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    private RegistrarSolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarSolicitudUseCase(
                solicitudRepository, tipoPrestamoRepository, new ValidarPlazoSolicitud()
        );
    }

    @Test
    void registersSolicitudWithPendingStatus() {
        TipoPrestamo tipo = tipoPrestamo();
        when(tipoPrestamoRepository.findActiveByCode(tipo.getCodigo())).thenReturn(Optional.of(tipo));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> invocation.<Solicitud>getArgument(0).toBuilder().id(42L).build());

        Solicitud result = useCase.execute(solicitud("LIBRE_INVERSION", 24));

        assertEquals(42L, result.getId());
        assertEquals(EstadoSolicitud.PENDIENTE_REVISION, result.getEstado());
        assertEquals("LIBRE_INVERSION", result.getTipoPrestamo().getCodigo());
        verify(solicitudRepository).save(any(Solicitud.class));
    }

    @Test
    void rejectsUnknownLoanTypeWithoutSaving() {
        when(tipoPrestamoRepository.findActiveByCode("NO_EXISTE")).thenReturn(Optional.empty());

        assertThrows(TipoPrestamoNoEncontradoException.class,
                () -> useCase.execute(solicitud("NO_EXISTE", 24)));

        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void rejectsTermOutsideTheSelectedTypeRangeWithoutSaving() {
        TipoPrestamo tipo = tipoPrestamo();
        when(tipoPrestamoRepository.findActiveByCode(tipo.getCodigo())).thenReturn(Optional.of(tipo));

        assertThrows(PlazoPrestamoInvalidoException.class,
                () -> useCase.execute(solicitud("LIBRE_INVERSION", 61)));

        verify(solicitudRepository, never()).save(any());
    }

    private Solicitud solicitud(String codigo, int plazo) {
        return Solicitud.builder()
                .documentoCliente("10000000")
                .monto(new BigDecimal("2500000"))
                .plazoMeses(plazo)
                .tipoPrestamo(TipoPrestamo.builder().codigo(codigo).build())
                .build();
    }

    private TipoPrestamo tipoPrestamo() {
        return TipoPrestamo.builder()
                .id(1L)
                .codigo("LIBRE_INVERSION")
                .nombre("Crédito de libre inversión")
                .plazoMinimoMeses(6)
                .plazoMaximoMeses(60)
                .activo(true)
                .build();
    }
}