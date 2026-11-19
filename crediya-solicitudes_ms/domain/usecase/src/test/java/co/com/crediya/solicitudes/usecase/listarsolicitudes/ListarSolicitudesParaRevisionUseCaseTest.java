package co.com.crediya.solicitudes.usecase.listarsolicitudes;

import co.com.crediya.solicitudes.model.exceptions.RolNoAutorizadoException;
import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitudes;
import co.com.crediya.solicitudes.model.solicitud.Pagina;
import co.com.crediya.solicitudes.model.solicitud.RolSolicitante;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarSolicitudesParaRevisionUseCaseTest {
    @Mock private SolicitudRepository repository;
    private ListarSolicitudesParaRevisionUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListarSolicitudesParaRevisionUseCase(repository);
    }

    @Test
    void advisorCanListAndGetsApprovedDebtPerApplicant() {
        when(repository.findAll(eq(FiltroSolicitudes.todos()), eq(0), eq(20)))
                .thenReturn(new Pagina<>(List.of(solicitud()), 0, 20, 1, 1));
        when(repository.sumarCuotasAprobadas(anyLong())).thenReturn(new BigDecimal("350000.00"));

        var pagina = useCase.execute(FiltroSolicitudes.todos(), RolSolicitante.ASESOR, 0, 20);

        assertEquals(1, pagina.contenido().size());
        assertEquals(new BigDecimal("350000.00"),
                pagina.contenido().getFirst().deudaTotalMensualSolicitudesAprobadas());
        assertEquals("LIBRE_INVERSION", pagina.contenido().getFirst().codigoTipoPrestamo());
        assertEquals(1, pagina.totalElementos());
    }

    @Test
    void adminCanListWithFilters() {
        var filtro = new FiltroSolicitudes(EstadoSolicitud.PENDIENTE_REVISION, "LIBRANZA", "10000000");
        when(repository.findAll(filtro, 1, 10))
                .thenReturn(new Pagina<>(List.of(), 1, 10, 0, 0));

        var pagina = useCase.execute(filtro, RolSolicitante.ADMIN, 1, 10);

        assertEquals(0, pagina.contenido().size());
        verify(repository).findAll(filtro, 1, 10);
    }

    @Test
    void clientCannotListForReview() {
        assertThrows(RolNoAutorizadoException.class,
                () -> useCase.execute(FiltroSolicitudes.todos(), RolSolicitante.CLIENTE, 0, 20));
        verify(repository, never()).findAll(org.mockito.ArgumentMatchers.any(), anyInt(), anyInt());
    }

    @Test
    void rejectsInvalidPagination() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(FiltroSolicitudes.todos(), RolSolicitante.ADMIN, -1, 20));
        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(FiltroSolicitudes.todos(), RolSolicitante.ADMIN, 0, 101));
    }

    private Solicitud solicitud() {
        return Solicitud.builder()
                .id(7L).solicitanteId(15L).documentoCliente("10000000").nombreCliente("Ana María Gómez")
                .correoElectronico("ana@example.com").salarioBase(new BigDecimal("2500000"))
                .monto(new BigDecimal("5000000")).plazoMeses(24)
                .tipoPrestamo(TipoPrestamo.builder().codigo("LIBRE_INVERSION")
                        .nombre("Crédito de libre inversión").build())
                .tasaInteres(new BigDecimal("0.019")).cuotaMensual(new BigDecimal("261370.32"))
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .build();
    }
}
