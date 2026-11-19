package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.model.exceptions.TipoPrestamoNoEncontradoException;
import co.com.crediya.solicitudes.model.solicitud.EstadoSolicitud;
import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitudes;
import co.com.crediya.solicitudes.model.solicitud.Pagina;
import co.com.crediya.solicitudes.model.solicitud.RolSolicitante;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.SolicitudRevision;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.usecase.listarsolicitudes.ListarSolicitudesParaRevisionUseCase;
import co.com.crediya.solicitudes.usecase.registrarsolicitud.RegistrarSolicitudUseCase;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfig.class, properties = "security.jwt.secret=AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=")
class ApiRestTest {
    private static final String TOKEN = token(15L, "CLIENTE", "ana@example.com");
    private static final String REQUEST = """
            {"documentoCliente":"10000000","nombreCliente":"Ana María Gómez","salarioBase":2500000,
             "monto":2500000,"plazoMeses":24,"codigoTipoPrestamo":"LIBRE_INVERSION"}
            """;
    @Autowired private WebApplicationContext context;
    @Autowired private RegistrarSolicitudUseCase registrarSolicitudUseCase;
    @Autowired private ListarSolicitudesParaRevisionUseCase listarSolicitudesParaRevisionUseCase;
    private RestTestClient client;

    @BeforeEach
    void setup() {
        reset(registrarSolicitudUseCase, listarSolicitudesParaRevisionUseCase);
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void rejectsAnonymousLoanRequest() {
        client.post().uri("/api/v1/solicitud").contentType(MediaType.APPLICATION_JSON)
                .body(REQUEST).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void derivesApplicantAndEmailFromJwtInsteadOfRequestBody() {
        when(registrarSolicitudUseCase.execute(any(Solicitud.class))).thenReturn(solicitud());

        client.post().uri("/api/v1/solicitud").header("Authorization", "Bearer " + TOKEN)
                .contentType(MediaType.APPLICATION_JSON).body(REQUEST).exchange()
                .expectStatus().isCreated().expectBody()
                .jsonPath("$.id").isEqualTo(15)
                .jsonPath("$.solicitanteId").isEqualTo(15)
                .jsonPath("$.correoElectronico").isEqualTo("ana@example.com")
                .jsonPath("$.estado").isEqualTo("PENDIENTE_REVISION")
                .jsonPath("$.codigoTipoPrestamo").isEqualTo("LIBRE_INVERSION");

        ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);
        verify(registrarSolicitudUseCase).execute(captor.capture());
        assertEquals(15L, captor.getValue().getSolicitanteId());
        assertEquals("ana@example.com", captor.getValue().getCorreoElectronico());
        assertEquals("Ana María Gómez", captor.getValue().getNombreCliente());
    }

    @Test
    void returnsBadRequestWhenRequestFieldsAreInvalid() {
        client.post().uri("/api/v1/solicitud").header("Authorization", "Bearer " + TOKEN)
                .contentType(MediaType.APPLICATION_JSON).body("""
                        {"documentoCliente":"x","nombreCliente":"","salarioBase":0,
                         "monto":0,"plazoMeses":0,"codigoTipoPrestamo":""}
                        """).exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.details.documentoCliente").exists()
                .jsonPath("$.details.monto").exists()
                .jsonPath("$.details.plazoMeses").exists();
        verify(registrarSolicitudUseCase, never()).execute(any(Solicitud.class));
    }

    @Test
    void returnsBadRequestWhenLoanTypeDoesNotExist() {
        when(registrarSolicitudUseCase.execute(any(Solicitud.class)))
                .thenThrow(new TipoPrestamoNoEncontradoException("NO_EXISTE"));
        client.post().uri("/api/v1/solicitud").header("Authorization", "Bearer " + TOKEN)
                .contentType(MediaType.APPLICATION_JSON).body(REQUEST).exchange()
                .expectStatus().isBadRequest().expectBody().jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void deniesNonClientRoleForCreation() {
        client.post().uri("/api/v1/solicitud").header("Authorization", "Bearer " + token(15L, "ASESOR", "a@x.com"))
                .contentType(MediaType.APPLICATION_JSON).body(REQUEST).exchange().expectStatus().isForbidden();
    }

    @Test
    void advisorCanListForReviewAndReceivesPaginationMetadata() {
        when(listarSolicitudesParaRevisionUseCase.execute(any(FiltroSolicitudes.class),
                any(RolSolicitante.class), anyInt(), anyInt()))
                .thenReturn(new Pagina<>(List.of(revision()), 0, 20, 1, 1));

        client.get().uri("/api/v1/solicitud?page=0&size=20")
                .header("Authorization", "Bearer " + token(9L, "ASESOR", "asesor@crediya.com"))
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.content[0].id").isEqualTo(7)
                .jsonPath("$.content[0].deudaTotalMensualSolicitudesAprobadas").isEqualTo(350000.00)
                .jsonPath("$.totalElements").isEqualTo(1)
                .jsonPath("$.totalPages").isEqualTo(1);
    }

    @Test
    void clientCannotListForReview() {
        client.get().uri("/api/v1/solicitud").header("Authorization", "Bearer " + TOKEN)
                .exchange().expectStatus().isForbidden();
    }

    @Test
    void invalidStatusFilterUsesTheCommonErrorFormat() {
        client.get().uri("/api/v1/solicitud?estado=NO_EXISTE")
                .header("Authorization", "Bearer " + token(9L, "ADMIN", "admin@crediya.com"))
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").exists()
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void returnsNotFoundInStandardFormatForUnknownRoute() {
        client.get().uri("/api/v1/no-existe").exchange().expectStatus().isNotFound();
    }

    private Solicitud solicitud() {
        return Solicitud.builder().id(15L).solicitanteId(15L).documentoCliente("10000000")
                .nombreCliente("Ana María Gómez").correoElectronico("ana@example.com")
                .salarioBase(new BigDecimal("2500000")).monto(new BigDecimal("2500000")).plazoMeses(24)
                .tipoPrestamo(TipoPrestamo.builder().id(1L).codigo("LIBRE_INVERSION")
                        .nombre("Crédito de libre inversión").plazoMinimoMeses(6).plazoMaximoMeses(60)
                        .tasaInteres(new BigDecimal("0.019")).activo(true).build())
                .tasaInteres(new BigDecimal("0.019")).cuotaMensual(new BigDecimal("130685.16"))
                .estado(EstadoSolicitud.PENDIENTE_REVISION).build();
    }

    private SolicitudRevision revision() {
        return new SolicitudRevision(7L, 15L, "10000000", "Ana María Gómez", "ana@example.com",
                new BigDecimal("2500000"), new BigDecimal("5000000"), 24, "LIBRE_INVERSION",
                "Crédito de libre inversión", new BigDecimal("0.019"), EstadoSolicitud.PENDIENTE_REVISION,
                new BigDecimal("350000.00"));
    }

    private static String token(Long id, String role, String email) {
        var key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8="));
        Instant now = Instant.now();
        return Jwts.builder().issuer("crediya-auth").subject(id.toString())
                .claim("email", email).claim("role", role)
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(300))).signWith(key).compact();
    }
}
