package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.exceptions.TipoPrestamoNoEncontradoException;
import co.com.crediya.solicitudes.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfig.class)
class ApiRestTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private RegistrarSolicitudUseCase registrarSolicitudUseCase;

    private RestTestClient client;

    @BeforeEach
    void setup() {
        reset(registrarSolicitudUseCase);
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void registersSolicitudAndReturnsCreatedResponse() {
        when(registrarSolicitudUseCase.execute(any(Solicitud.class))).thenReturn(solicitud());

        client.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "documentoCliente": "10000000",
                          "monto": 2500000,
                          "plazoMeses": 24,
                          "codigoTipoPrestamo": "LIBRE_INVERSION"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(15)
                .jsonPath("$.estado").isEqualTo("Pendiente de revisión")
                .jsonPath("$.codigoTipoPrestamo").isEqualTo("LIBRE_INVERSION");

        verify(registrarSolicitudUseCase).execute(any(Solicitud.class));
    }

    @Test
    void returnsBadRequestWhenRequestFieldsAreInvalid() {
        client.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "documentoCliente": "x",
                          "monto": 0,
                          "plazoMeses": 0,
                          "codigoTipoPrestamo": ""
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.details.documentoCliente").exists()
                .jsonPath("$.details.monto").exists()
                .jsonPath("$.details.plazoMeses").exists();

        verify(registrarSolicitudUseCase, org.mockito.Mockito.never()).execute(any(Solicitud.class));
    }

    @Test
    void returnsBadRequestWhenLoanTypeDoesNotExist() {
        when(registrarSolicitudUseCase.execute(any(Solicitud.class)))
                .thenThrow(new TipoPrestamoNoEncontradoException("NO_EXISTE"));

        client.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "documentoCliente": "10000000",
                          "monto": 2500000,
                          "plazoMeses": 24,
                          "codigoTipoPrestamo": "NO_EXISTE"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").exists();
    }

    @Test
    void returnsNotFoundInStandardFormatForUnknownRoute() {
        client.get()
                .uri("/api/v1/no-existe")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").exists();
    }

    @Test
    void hidesTechnicalDetailsWhenUnexpectedErrorOccurs() {
        when(registrarSolicitudUseCase.execute(any(Solicitud.class)))
                .thenThrow(new IllegalStateException("database password leaked"));

        client.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "documentoCliente": "10000000",
                          "monto": 2500000,
                          "plazoMeses": 24,
                          "codigoTipoPrestamo": "LIBRE_INVERSION"
                        }
                        """)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.message")
                .isEqualTo("Ocurrió un error interno. Intenta nuevamente más tarde.")
                .jsonPath("$.trace").doesNotExist();
    }
    private Solicitud solicitud() {
        return Solicitud.builder()
                .id(15L)
                .documentoCliente("10000000")
                .monto(new BigDecimal("2500000"))
                .plazoMeses(24)
                .tipoPrestamo(TipoPrestamo.builder()
                        .id(1L)
                        .codigo("LIBRE_INVERSION")
                        .nombre("Crédito de libre inversión")
                        .plazoMinimoMeses(6)
                        .plazoMaximoMeses(60)
                        .activo(true)
                        .build())
                .estado(EstadoSolicitud.PENDIENTE_REVISION)
                .build();
    }
}