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
    @Mock private SolicitudRepository solicitudRepository;
    @Mock private TipoPrestamoRepository tipoPrestamoRepository;
    private RegistrarSolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarSolicitudUseCase(solicitudRepository, tipoPrestamoRepository,
                new ValidarPlazoSolicitud(), new CalcularCuotaMensual());
    }

    @Test
    void registersSolicitudWithPendingStatusRateAndInstallment() {
        TipoPrestamo tipo = tipoPrestamo();
        when(tipoPrestamoRepository.findActiveByCode(tipo.getCodigo())).thenReturn(Optional.of(tipo));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> invocation.<Solicitud>getArgument(0).toBuilder().id(42L).build());

        Solicitud result = useCase.execute(solicitud("LIBRE_INVERSION", 24));

        assertEquals(42L, result.getId());
        assertEquals(15L, result.getSolicitanteId());
        assertEquals(EstadoSolicitud.PENDIENTE_REVISION, result.getEstado());
        assertEquals(tipo.getTasaInteres(), result.getTasaInteres());
        assertEquals(0, new BigDecimal("130685.16").compareTo(result.getCuotaMensual()));
        assertEquals("Ana María Gómez", result.getNombreCliente());
        assertEquals("ana@example.com", result.getCorreoElectronico());
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
                .solicitanteId(15L)
                .documentoCliente("10000000")
                .nombreCliente("Ana María Gómez")
                .correoElectronico("ana@example.com")
                .salarioBase(new BigDecimal("2500000"))
                .monto(new BigDecimal("2500000"))
                .plazoMeses(plazo)
                .tipoPrestamo(TipoPrestamo.builder().codigo(codigo).build())
                .build();
    }

    private TipoPrestamo tipoPrestamo() {
        return TipoPrestamo.builder().id(1L).codigo("LIBRE_INVERSION")
                .nombre("Crédito de libre inversión").plazoMinimoMeses(6).plazoMaximoMeses(60)
                .tasaInteres(new BigDecimal("0.019")).activo(true).build();
    }
}
