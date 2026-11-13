package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfig.class)
class ApiRestTest {

    private static final String REQUEST = """
            {
              "nombres": "Ana María",
              "apellidos": "Gómez Pérez",
              "documento": "1020304050",
              "fechaNacimiento": "1995-04-20",
              "direccion": "Calle 10 # 20-30",
              "telefono": "3001234567",
              "correoElectronico": "ana@example.com",
              "salarioBase": 2500000.00
            }
            """;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    private RestTestClient client;

    @BeforeEach
    void setup() {
        reset(registrarUsuarioUseCase);
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void shouldRegisterUser() {
        when(registrarUsuarioUseCase.registrarUsuario(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.<Usuario>getArgument(0).toBuilder().id(1L).build());

        client.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(REQUEST)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void shouldRejectInvalidEmail() {
        String requestWithInvalidEmail = REQUEST.replace("ana@example.com", "correo-invalido");

        client.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestWithInvalidEmail)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnNotFoundForInvalidPath() {
        client.get()
                .uri("/api/v1/usuarios/invalid")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
